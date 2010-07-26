/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.EditableConversation;
import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_MANAGER_BEAN_NAME;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.PhaseId;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.request.RequestTypeResolver;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.request.BeforeFacesRequest;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.RequestCache;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.*;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.RedirectHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Bean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.lang.annotation.Annotation;

/**
 * TODO don't cleanup in case of partial requests (via RequestTypeResolver)
 *
 * @author Gerhard Petracek
 */
@Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)
@SessionScoped
public class DefaultWindowContextManager implements WindowContextManager
{
    private static final long serialVersionUID = 2872151847183166424L;

    private Map<Long, WindowContext> windowContextMap = new ConcurrentHashMap<Long, WindowContext>();

    //TODO refactor to UUID?
    private AtomicLong lastWindowContextId = new AtomicLong(0);

    @Inject
    @SuppressWarnings({"UnusedDeclaration"})
    private ConfigResolver configResolver;

    private WindowContextConfig windowContextConfig;

    private RedirectHandler redirectHandler;

    @PostConstruct
    protected void init()
    {
        this.windowContextConfig = this.configResolver.resolve(WindowContextConfig.class);
        this.redirectHandler = configResolver.resolve(JsfAwareWindowContextConfig.class).getRedirectHandler();
    }

    @PreDestroy
    protected void destroyAllConversations()
    {
        for (WindowContext windowContext : this.windowContextMap.values())
        {
            for (Conversation conversation :
                    ((EditableWindowContext)windowContext).getConversations().values())
            {
                //TODO
                if(conversation instanceof EditableConversation)
                {
                    ((EditableConversation)conversation).forceEnd();
                }
                else
                {
                    conversation.end();
                }
            }

            //TODO
            ((EditableWindowContext)windowContext).removeInactiveConversations();
        }
    }

    //don't change/optimize this observer!!!
    protected void cleanup(@Observes @AfterPhase(PhaseId.RESTORE_VIEW) PhaseEvent phaseEvent,
                           RequestTypeResolver requestTypeResolver,
                           WindowContextManager windowContextManager)
    {
        restoreInformationOfRequest(phaseEvent.getFacesContext(), windowContextManager);

        //for performance reasons + cleanup at the beginning of the request (check timeout,...)
        //+ we aren't allowed to cleanup in case of redirects
        //we would transfer the restored view-id into the conversation
        if (isPartialOrGetRequest(requestTypeResolver))
        {
            return;
        }

        cleanupInactiveConversations();
    }

    protected void recordCurrentViewAsOldViewId(@Observes @AfterPhase(PhaseId.RENDER_RESPONSE) PhaseEvent phaseEvent)
    {
        storeCurrentViewIdAsOldViewId(FacesContext.getCurrentInstance());
        RequestCache.resetCache();
    }

    protected void resetCacheInDevMode(@Observes @BeforeFacesRequest FacesContext facesContext)
    {
        //TODO activate it only in project-stage dev.
        //org.apache.myfaces.view.facelets.tag.ui.DebugPhaseListener causes re-eval (+ caching) of window-id
        JsfUtils.resetCaches();
    }

    private boolean isPartialOrGetRequest(RequestTypeResolver requestTypeResolver)
    {
        return requestTypeResolver.isPartialRequest() || !requestTypeResolver.isPostRequest();
    }

    private void cleanupInactiveConversations()
    {
        //don't cleanup all window contexts (it would cause a side-effect with the access-scope and multiple windows 
        WindowContext windowContext = getCurrentWindowContext();

        for (Conversation conversation : ((EditableWindowContext)windowContext).getConversations().values())
        {
            //TODO test the usage of #isActiveState instead of isActive
            if (!((EditableConversation)conversation).isActive())
            {
                conversation.end();
            }
        }

        //TODO
        ((EditableWindowContext)windowContext).removeInactiveConversations();
    }

    @Produces
    @Named(WindowContext.CURRENT_WINDOW_CONTEXT_BEAN_NAME)
    @RequestScoped
    protected WindowContext currentWindowContext()
    {
        return getCurrentWindowContext();
    }

    @Produces
    @Dependent
    protected Conversation currentConversation(final InjectionPoint injectionPoint,
                                               final WindowContextManager windowContextManager)
    {
        //for @Inject Conversation conversation;
        return new Conversation()
        {
            private static final long serialVersionUID = 7754789230388003028L;

            public void end()
            {
                findConversation().end();
            }

            public void restart()
            {
                findConversation().restart();
            }

            private Conversation findConversation()
            {
                Bean<?> bean = injectionPoint.getBean();
                Class conversationGroup = ConversationUtils.getConversationGroup(bean);

                Set<Annotation> qualifiers = bean.getQualifiers();

                conversationGroup = ConversationUtils.convertViewAccessScope(bean, conversationGroup, qualifiers);

                return ((EditableWindowContext)windowContextManager.getCurrentWindowContext())
                        .getConversation(conversationGroup, qualifiers.toArray(new Annotation[qualifiers.size()]));
            }
        };
    }

    //TODO improve performance
    public WindowContext getCurrentWindowContext()
    {
        Long windowContextId = RequestCache.getCurrentWindowId();

        if(windowContextId == null)
        {
            windowContextId = resolveWindowContextId(this.windowContextConfig.isGetRequestParameterSupported(),
                                                     this.redirectHandler);

            if (windowContextId == null)
            {
                windowContextId = this.lastWindowContextId.incrementAndGet();
                cacheWindowId(windowContextId);
            }

            RequestCache.setCurrentWindowId(windowContextId);
        }

        return getWindowContext(windowContextId);
    }

    public synchronized WindowContext getWindowContext(long windowContextId)
    {
        WindowContext result = this.windowContextMap.get(windowContextId);

        if (result == null)
        {
            result = new JsfWindowContext(windowContextId, this.windowContextConfig);

            this.windowContextMap.put(windowContextId, result);
        }
        return result;
    }

    public void activateWindowContext(long id)
    {
        activateWindowContext(getWindowContext(id));
    }

    public void activateWindowContext(WindowContext windowContext)
    {
        JsfUtils.resetCaches();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        WindowContextIdHolderComponent windowContextIdHolder =
                ConversationUtils.getWindowContextIdHolderComponent(facesContext);

        if (windowContextIdHolder != null)
        {
            windowContextIdHolder.changeWindowContextId(windowContext.getId());
        }

        setWindowContextIdOfRequest(facesContext, windowContext.getId());
    }

    //TODO
    public void resetCurrentWindowContext()
    {
        resetWindowContext(getCurrentWindowContext());
    }

    //TODO
    public void resetWindowContext(long id)
    {
        resetWindowContext(getWindowContext(id));
    }

    public void resetWindowContext(WindowContext windowContext)
    {
        JsfUtils.resetCaches();
        for (Conversation conversation : ((EditableWindowContext)windowContext).getConversations().values())
        {
            conversation.restart();
        }
    }

    public void resetConversations()
    {
        resetConversations(getCurrentWindowContext());
    }

    public void resetConversations(long windowContextId)
    {
        resetConversations(getWindowContext(windowContextId));
    }

    public void resetConversations(WindowContext windowContext)
    {
        JsfUtils.resetCaches();
        for (Conversation conversation : ((EditableWindowContext)windowContext).getConversations().values())
        {
            //TODO
            ((EditableConversation)conversation).deactivate();
             //it isn't possible to deactivate window scoped conversations
            if (!((EditableConversation)conversation).isActive())
            {
                conversation.restart();
            }
        }
    }

    public void removeCurrentWindowContext()
    {
        removeWindowContext(getCurrentWindowContext());
    }

    public void removeWindowContext(long id)
    {
        removeWindowContext(getWindowContext(id));
    }

    public void removeWindowContext(WindowContext windowContext)
    {
        JsfUtils.resetCaches();
        this.windowContextMap.remove(windowContext.getId());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        removeWindowContextIdHolderComponent(facesContext);
        setWindowContextIdOfRequest(facesContext, null);

        windowContext.endConversations();
    }


    private void setWindowContextIdOfRequest(FacesContext facesContext, Long newId)
    {
        Map requestMap = facesContext.getExternalContext().getRequestMap();

        if (newId != null)
        {
            //noinspection unchecked
            requestMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, newId);
        }
        requestMap.remove(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }

    private void removeWindowContextIdHolderComponent(FacesContext facesContext)
    {
        JsfUtils.resetCaches();
        Iterator<UIComponent> uiComponents = facesContext.getViewRoot().getChildren().iterator();

        UIComponent uiComponent;
        while (uiComponents.hasNext())
        {
            uiComponent = uiComponents.next();
            if (uiComponent instanceof WindowContextIdHolderComponent)
            {
                uiComponents.remove();
                return;
            }
        }
    }
}
