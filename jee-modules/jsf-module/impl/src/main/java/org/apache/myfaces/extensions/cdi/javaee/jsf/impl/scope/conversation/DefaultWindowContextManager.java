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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.RequestCache;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.*;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ExceptionUtils.windowContextNotEditableException;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowContextFactory;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowContextQuotaHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowHandler;

import javax.enterprise.inject.Typed;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
@Typed()
@SuppressWarnings({"UnusedDeclaration"})
public class DefaultWindowContextManager implements EditableWindowContextManager
{
    private static final long serialVersionUID = 2872151847183166424L;

    private Map<String, EditableWindowContext> windowContextMap =
            new ConcurrentHashMap<String, EditableWindowContext>();

    private ProjectStage projectStage;

    private JsfAwareWindowContextConfig jsfAwareWindowContextConfig;

    private boolean allowUnknownWindowIds = false;

    private boolean urlParameterSupported = true;

    private WindowHandler windowHandler;

    private boolean projectStageDevelopment;

    private WindowContextQuotaHandler windowContextQuotaHandler;

    //TODO add config + refactor DefaultWindowContextManager
    private static final int DEFAULT_WINDOW_KEY_LENGTH = 3;

    protected DefaultWindowContextManager(JsfAwareWindowContextConfig jsfAwareWindowContextConfig,
                                          ProjectStage projectStage)
    {
        this.jsfAwareWindowContextConfig = jsfAwareWindowContextConfig;
        this.projectStage = projectStage;
        init();
    }

    protected void init()
    {
        this.windowHandler = this.jsfAwareWindowContextConfig.getWindowHandler();
        this.windowContextQuotaHandler = this.jsfAwareWindowContextConfig.getWindowContextQuotaHandler();
        this.allowUnknownWindowIds = this.jsfAwareWindowContextConfig.isUnknownWindowIdsAllowed();
        this.urlParameterSupported = this.jsfAwareWindowContextConfig.isUrlParameterSupported();

        this.projectStageDevelopment = ProjectStage.Development.equals(this.projectStage);
    }

    public WindowContext getCurrentWindowContext()
    {
        WindowContext windowContext = RequestCache.getCurrentWindowContext();

        if(windowContext != null)
        {
            return windowContext;
        }

        String windowContextId =
                resolveWindowContextId(this.windowHandler, this.urlParameterSupported, this.allowUnknownWindowIds);

        if (windowContextId == null)
        {
            windowContextId = createNewWindowContextId();
        }

        windowContext = getWindowContext(windowContextId);

        RequestCache.setCurrentWindowContext(windowContext);

        return windowContext;
    }

    private synchronized String createNewWindowContextId()
    {
        String windowContextId = this.windowHandler.createWindowId();

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        if(this.windowContextQuotaHandler.checkQuota(getNumberOfNextWindowContext()))
        {
            if(!cleanupInactiveWindowContexts(this))
            {
                this.windowContextQuotaHandler.handleQuotaViolation();
            }
        }

        if(this.projectStageDevelopment &&
                this.windowHandler instanceof DefaultWindowHandler /*only in this case we know all details*/)
        {
            //it's easier for developers to check the current window context
            //after a cleanup of window contexts it isn't reliable
            //however - such a cleanup shouldn't occur during development
            windowContextId = convertToDevWindowContextId(windowContextId, getNumberOfNextWindowContext());
        }

        storeCreatedWindowContextId(externalContext, windowContextId);
        cacheWindowId(externalContext, windowContextId, this.allowUnknownWindowIds);

        return windowContextId;
    }

    private int getNumberOfNextWindowContext()
    {
        return this.windowContextMap.size() + 1;
    }

    public synchronized WindowContext getWindowContext(String windowContextId)
    {
        EditableWindowContext result = this.windowContextMap.get(windowContextId);

        if(result != null && !result.isActive())
        {
            removeWindowContext(result);
            result = null;
        }

        if (result == null)
        {
            result = createWindowContext(windowContextId);

            this.windowContextMap.put(windowContextId, result);
        }

        result.touch();

        return result;
    }

    private EditableWindowContext createWindowContext(String windowContextId)
    {
        WindowContextFactory windowContextFactory = this.jsfAwareWindowContextConfig.getWindowContextFactory();

        if(windowContextFactory != null)
        {
            return windowContextFactory.createWindowContext(windowContextId, this.jsfAwareWindowContextConfig);
        }
        
        return new JsfWindowContext(windowContextId, this.jsfAwareWindowContextConfig, this.projectStageDevelopment);
    }

    public boolean activateWindowContext(String windowContextId)
    {
        return activateWindowContext(convert(getWindowContext(windowContextId)));
    }

    public boolean activateWindowContext(EditableWindowContext windowContext)
    {
        JsfUtils.resetCaches();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        WindowContextIdHolderComponent windowContextIdHolder =
                ConversationUtils.getWindowContextIdHolderComponent(facesContext);

        if (windowContextIdHolder != null)
        {
            windowContextIdHolder.changeWindowContextId(windowContext.getId());
        }

        return cacheWindowId(facesContext.getExternalContext(), windowContext.getId(), this.allowUnknownWindowIds);
    }

    public void resetCurrentWindowContext()
    {
        resetWindowContext(convert(getCurrentWindowContext()));
    }

    public void resetWindowContext(String windowContextId)
    {
        resetWindowContext(convert(getWindowContext(windowContextId)));
    }

    public void resetWindowContext(EditableWindowContext windowContext)
    {
        JsfUtils.resetCaches();
        for (Conversation conversation : windowContext.getConversations().values())
        {
            conversation.restart();
        }
    }

    public void resetConversations()
    {
        resetConversations(convert(getCurrentWindowContext()));
    }

    public void resetConversations(String windowContextId)
    {
        resetConversations(convert(getWindowContext(windowContextId)));
    }

    public void resetConversations(EditableWindowContext windowContext)
    {
        JsfUtils.resetCaches();
        for (EditableConversation conversation : windowContext.getConversations().values())
        {
            conversation.deactivate();
             //it isn't possible to deactivate window scoped conversations
            if (!conversation.isActive())
            {
                conversation.restart();
            }
        }
    }

    public void removeCurrentWindowContext()
    {
        removeWindowContext(convert(getCurrentWindowContext()));
    }

    public void removeWindowContext(String windowContextId)
    {
        removeWindowContext(convert(getWindowContext(windowContextId)));
    }

    public void removeWindowContext(EditableWindowContext windowContext)
    {
        JsfUtils.resetCaches();
        this.windowContextMap.remove(windowContext.getId());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        removeWindowContextIdHolderComponent(facesContext);

        //reset existing information
        removeExistingWindowId(externalContext, windowContext.getId());
        externalContext.getRequestMap().remove(WINDOW_CONTEXT_ID_PARAMETER_KEY);

        windowContext.endConversations();
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
        String devWindowContextId = currentWindowContextCount + windowContextId;

        if(devWindowContextId.length() > DEFAULT_WINDOW_KEY_LENGTH + ("" + currentWindowContextCount).length())
        {
            return windowContextId;
        }
        return devWindowContextId;
    }

    public Collection<EditableWindowContext> getWindowContexts()
    {
        return Collections.unmodifiableCollection(this.windowContextMap.values());
    }

    public void destroy()
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

    private EditableWindowContext convert(WindowContext windowContext)
    {
        if(!(windowContext instanceof EditableWindowContext))
        {
            throw windowContextNotEditableException(windowContext);
        }

        return (EditableWindowContext)windowContext;
    }
}
