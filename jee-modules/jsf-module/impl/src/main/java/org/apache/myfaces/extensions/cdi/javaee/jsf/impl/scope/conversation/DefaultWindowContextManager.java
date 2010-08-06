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
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.EditableConversation;
import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_MANAGER_BEAN_NAME;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.PhaseId;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.request.RequestTypeResolver;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.request.BeforeFacesRequest;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.RequestCache;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ExceptionUtils.tooManyOpenWindowException;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.*;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.EditableWindowContext;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.annotation.Annotation;

/**
 * TODO don't cleanup in case of partial requests (via RequestTypeResolver)
 *
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
@Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)
@SessionScoped
public class DefaultWindowContextManager implements WindowContextManager
{
    private static final long serialVersionUID = 2872151847183166424L;

    private Map<String, WindowContext> windowContextMap = new ConcurrentHashMap<String, WindowContext>();

    @Inject
    @SuppressWarnings({"UnusedDeclaration"})
    private ConfigResolver configResolver;

    @Inject
    @SuppressWarnings({"UnusedDeclaration"})
    private ProjectStage projectStage;

    private WindowContextConfig windowContextConfig;

    private WindowHandler windowHandler;

    private boolean projectStageDevelopment;

    //just for project stage dev.
    private AtomicInteger windowContextCounter = new AtomicInteger(1);

    @PostConstruct
    protected void init()
    {
        this.windowContextConfig = this.configResolver.resolve(WindowContextConfig.class);
        this.windowHandler = configResolver.resolve(JsfAwareWindowContextConfig.class).getWindowHandler();
        this.projectStageDevelopment = ProjectStage.Development.equals(this.projectStage);
    }

    @PreDestroy
    protected void destroyAllConversations()
    {
        for (WindowContext windowContext : this.windowContextMap.values())
        {
            for (Conversation conversation :
                    ((EditableWindowContext)windowContext).getConversations().values())
            {
                conversation.end();
            }

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
        //don't ignore partial requests - in case of ajax-navigation we wouldn't check for expiration
        if (!requestTypeResolver.isPostRequest())
        {
            return;
        }

        cleanupInactiveConversations();
        cleanupInactiveWindowContexts();
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
        String windowContextId = RequestCache.getCurrentWindowId();

        if(windowContextId == null)
        {
            windowContextId = resolveWindowContextId(this.windowContextConfig.isUrlParameterSupported(),
                                                     this.windowHandler);

            if (windowContextId == null)
            {
                windowContextId = createNewWindowContextId();
            }

            RequestCache.setCurrentWindowId(windowContextId);
        }

        return getWindowContext(windowContextId);
    }

    private synchronized String createNewWindowContextId()
    {
        String windowContextId = this.windowHandler.createWindowId();

        int currentWindowContextCount = this.windowContextCounter.getAndIncrement();

        if(this.windowContextConfig.getMaxWindowContextCount() < currentWindowContextCount)
        {
            if(!cleanupInactiveWindowContexts())
            {
                //TODO
                throw tooManyOpenWindowException(this.windowContextConfig.getWindowContextTimeoutInMinutes());
            }

            currentWindowContextCount = this.windowContextMap.size() + 1;
            this.windowContextCounter.set(currentWindowContextCount + 1);
        }

        if(this.projectStageDevelopment &&
                this.windowHandler instanceof DefaultWindowHandler /*only in this case we know all details*/)
        {
            //it's easier for developers to check the current window context
            //after a cleanup of window contexts it isn't reliable
            //however - such a cleanup shouldn't occur during development
            windowContextId = convertToDevWindowContextId(windowContextId, currentWindowContextCount);
        }
        cacheWindowId(windowContextId);
        return windowContextId;
    }

    public synchronized WindowContext getWindowContext(String windowContextId)
    {
        WindowContext result = this.windowContextMap.get(windowContextId);

        if (result == null)
        {
            result = new JsfWindowContext(windowContextId, this.windowContextConfig, this.projectStageDevelopment);

            this.windowContextMap.put(windowContextId, result);
        }

        if(result instanceof EditableWindowContext)
        {
            ((EditableWindowContext)result).touch();
        }

        return result;
    }

    public boolean activateWindowContext(String windowContextId)
    {
        return activateWindowContext(getWindowContext(windowContextId));
    }

    public boolean activateWindowContext(WindowContext windowContext)
    {
        JsfUtils.resetCaches();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        WindowContextIdHolderComponent windowContextIdHolder =
                ConversationUtils.getWindowContextIdHolderComponent(facesContext);

        if (windowContextIdHolder != null)
        {
            windowContextIdHolder.changeWindowContextId(windowContext.getId());
        }

        return cacheWindowId(facesContext.getExternalContext(), windowContext.getId());
    }

    //TODO
    public void resetCurrentWindowContext()
    {
        resetWindowContext(getCurrentWindowContext());
    }

    //TODO
    public void resetWindowContext(String windowContextId)
    {
        resetWindowContext(getWindowContext(windowContextId));
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

    public void resetConversations(String windowContextId)
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

    public void removeWindowContext(String windowContextId)
    {
        removeWindowContext(getWindowContext(windowContextId));
    }

    public void removeWindowContext(WindowContext windowContext)
    {
        JsfUtils.resetCaches();
        this.windowContextMap.remove(windowContext.getId());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        removeWindowContextIdHolderComponent(facesContext);

        //reset existing information
        getExistingWindowIdSet(externalContext).remove(windowContext.getId());
        externalContext.getRequestMap().remove(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);

        windowContext.endConversations();
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

        ((EditableWindowContext)windowContext).removeInactiveConversations();
    }

    private boolean cleanupInactiveWindowContexts()
    {
        int count = this.windowContextMap.size();

        for (WindowContext windowContext : this.windowContextMap.values())
        {
            if(windowContext instanceof EditableWindowContext &&
                    !((EditableWindowContext)windowContext).isActive())
            {
                removeWindowContext(windowContext);
            }
        }

        return this.windowContextMap.size() < count;
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

    private String convertToDevWindowContextId(String windowContextId, int currentWindowContextCount)
    {
        Set<String> windowContextIdSet =
                ConversationUtils.getExistingWindowIdSet(FacesContext.getCurrentInstance().getExternalContext());

        if(windowContextIdSet.remove(windowContextId))
        {
            String devWindowContextId = currentWindowContextCount + windowContextId;
            windowContextIdSet.add(devWindowContextId);
            return devWindowContextId;
        }
        return windowContextId;
    }
}
