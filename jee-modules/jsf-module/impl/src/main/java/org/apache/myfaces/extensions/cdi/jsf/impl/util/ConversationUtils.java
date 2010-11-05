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
package org.apache.myfaces.extensions.cdi.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.getOrCreateScopedInstanceOfBeanByClass;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.WindowContextIdHolderComponent;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * internal! utils
 * @author Gerhard Petracek
 */
public class ConversationUtils
{
    public static final String EXISTING_WINDOW_ID_SET_KEY =
            WindowContext.class.getName() + ":EXISTING_WINDOW_ID_LIST";

    private static final String OLD_VIEW_ID_KEY = "oldViewId";
    private static final String NEW_VIEW_ID_KEY = "newViewId";

    private static Map<Class, Class<? extends Annotation>> conversationGroupToScopeCache =
            new ConcurrentHashMap<Class, Class<? extends Annotation>>();

    private static final String REDIRECT_PERFORMED_KEY = WindowHandler.class.getName() + "redirect:KEY";
    
    public static Class getConversationGroup(Bean<?> bean)
    {
        Class<? extends Annotation> scopeType = bean.getScope();

        //TODO check if we should support conversation groups for @WindowScoped
        if(WindowScoped.class.isAssignableFrom(scopeType))
        {
            return WindowScoped.class;
        }

        ConversationGroup conversationGroupAnnotation = findConversationGroupAnnotation(bean);

        if(conversationGroupAnnotation == null)
        {
            return bean.getBeanClass();
        }

        return conversationGroupAnnotation.value();
    }

    private static ConversationGroup findConversationGroupAnnotation(Bean<?> bean)
    {
        Set<Annotation> qualifiers = bean.getQualifiers();

        for(Annotation qualifier : qualifiers)
        {
            if(ConversationGroup.class.isAssignableFrom(qualifier.annotationType()))
            {
                return (ConversationGroup)qualifier;
            }
        }
        return null;
    }

    //TODO
    public static String resolveWindowContextId(WindowHandler windowHandler,
                                                boolean requestParameterSupported,
                                                boolean allowUnknownWindowIds)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
        Map<String, Object> requestMap = externalContext.getRequestMap();

        //try to find id in request map
        String id = tryToFindWindowIdInRequestMap(requestMap);

        if((id == null || id.length() == 0) && windowHandler != null)
        {
            id = windowHandler.restoreWindowId(facesContext.getExternalContext());
        }

        if(id == null || id.length() == 0)
        {
            //depending on the config we have to check it in case of server-side window-ids +a forced id for the request
            id = tryToRestoreWindowIdFromRequestParameterMap(requestParameterSupported, requestParameterMap);
        }

        if(id != null && id.length() > 0 && !cacheWindowId(externalContext, id, allowUnknownWindowIds))
        {
            id = null;
        }

        if (id != null && id.length() > 0)
        {
            return id;
        }

        if("".equals(id))
        {
            //return null to force a new window id - e.g. in case of #{currentWindow.useNewId}
            return null;
        }

        //try to restore id from component
        WindowContextIdHolderComponent windowContextIdHolder = getWindowContextIdHolderComponent(facesContext);

        if (windowContextIdHolder != null)
        {
            //TODO cache for request
            id = windowContextIdHolder.getWindowContextId();

            if(id != null && !cacheWindowId(externalContext, id, allowUnknownWindowIds))
            {
                id = null;
            }

            if(id != null)
            {
                requestMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY,
                               windowContextIdHolder.getWindowContextId());
            }
        }

        return id;
    }

    private static String tryToRestoreWindowIdFromRequestParameterMap(
            boolean requestParameterSupported, Map<String, String> requestParameterMap)
    {
        //try to restore get-request parameter
        String idViaGetRequest = null;

        if (requestParameterSupported)
        {
            idViaGetRequest = requestParameterMap.get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
        }

        return idViaGetRequest;
    }

    /**
     * @param externalContext externalContext
     * @param id windowId
     * @param allowUnknownWindowIds true to force the usage of the given id
     * @return false if the id doesn't exist in the storage (e.g. in case of bookmarks)
     */
    public static boolean cacheWindowId(ExternalContext externalContext, String id, boolean allowUnknownWindowIds)
    {
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        Set<String> existingWindowIdSet = (Set)sessionMap.get(EXISTING_WINDOW_ID_SET_KEY);

        if(existingWindowIdSet == null)
        {
            existingWindowIdSet = new HashSet<String>();
            sessionMap.put(EXISTING_WINDOW_ID_SET_KEY, existingWindowIdSet);
        }

        if(!allowUnknownWindowIds && !existingWindowIdSet.contains(id))
        {
            return false;
        }

        //TODO check if it should be replace with the RequestCache 
        Map<String, Object> requestMap = externalContext.getRequestMap();
        requestMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, id);

        return true;
    }

    private static String tryToFindWindowIdInRequestMap(Map<String, Object> requestMap)
    {
        return (String) requestMap.get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }

    public static void storeCurrentViewIdAsOldViewId(FacesContext facesContext
    /*TODO add window context as parameter and test it in combination with redirects*/)
    {
        storeCurrentViewIdAsOldViewId(facesContext, getWindowContextManager());
    }

    public static void storeCurrentViewIdAsOldViewId(
            FacesContext facesContext, WindowContextManager windowContextManager)
    {
        String oldViewId = facesContext.getViewRoot().getViewId();
        windowContextManager.getCurrentWindowContext().setAttribute(OLD_VIEW_ID_KEY, oldViewId);
    }

    public static void storeCurrentViewIdAsNewViewId(FacesContext facesContext)
    {
        storeCurrentViewIdAsNewViewId(facesContext, getWindowContextManager().getCurrentWindowContext());
    }

    public static void storeCurrentViewIdAsNewViewId(FacesContext facesContext, WindowContext windowContext)
    {
        String newViewId = facesContext.getViewRoot().getViewId();
        windowContext.setAttribute(NEW_VIEW_ID_KEY, newViewId);
    }

    public static String getOldViewId()
    {
        return getWindowContextManager().getCurrentWindowContext().getAttribute(OLD_VIEW_ID_KEY, String.class);
    }

    public static String getNewViewId()
    {
        return getWindowContextManager().getCurrentWindowContext().getAttribute(NEW_VIEW_ID_KEY, String.class);
    }

    public static WindowContextIdHolderComponent getWindowContextIdHolderComponent(FacesContext facesContext)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(uiViewRoot == null)
        {
            return null;
        }

        List<UIComponent> uiComponents = uiViewRoot.getChildren();
        for (UIComponent uiComponent : uiComponents)
        {
            if (uiComponent instanceof WindowContextIdHolderComponent)
            {
                return ((WindowContextIdHolderComponent) uiComponent);
            }
        }

        return null;
    }

    public static void addWindowContextIdHolderComponent()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(uiViewRoot == null)
        {
            return;
        }

        List<UIComponent> uiComponents = uiViewRoot.getChildren();
        for (UIComponent uiComponent : uiComponents)
        {
            if (uiComponent instanceof WindowContextIdHolderComponent)
            {
                //in this case we have the same view-root
                return;
            }
        }

        uiViewRoot.getChildren().add(createComponentWithCurrentWindowContextId());
    }

    private static WindowContextIdHolderComponent createComponentWithCurrentWindowContextId()
    {
        WindowContextManager conversationManager = getOrCreateScopedInstanceOfBeanByClass(WindowContextManager.class);

        return new WindowContextIdHolderComponent(conversationManager.getCurrentWindowContext().getId());
    }

    public static void sendRedirect(ExternalContext externalContext,
                                    String url,
                                    WindowHandler windowHandler) throws IOException
    {
        if(isMultipleRedirectDetected(externalContext))
        {
            return;
        }
        else
        {
            redirectPerformed(externalContext);
        }

        storeCurrentViewIdAsOldViewId(FacesContext.getCurrentInstance());

        RequestCache.resetCache();
        
        if(windowHandler != null)
        {
            windowHandler.sendRedirect(externalContext, url, false);
        }
        else
        {
            //TODO log warning in case of project stage dev.
            externalContext.redirect(url);
        }
    }

    private static boolean isMultipleRedirectDetected(ExternalContext externalContext)
    {
        return externalContext.getRequestMap().containsKey(REDIRECT_PERFORMED_KEY);
    }

    private static void redirectPerformed(ExternalContext externalContext)
    {
        externalContext.getRequestMap().put(REDIRECT_PERFORMED_KEY, Boolean.TRUE);
    }

    public static WindowHandler getWindowHandler()
    {
        return getOrCreateScopedInstanceOfBeanByClass(WindowHandler.class);
    }

    public static WindowContextManager getWindowContextManager()
    {
        return RequestCache.getWindowContextManager();
    }

    public static boolean removeExistingWindowId(ExternalContext externalContext, String windowContextId)
    {
        return getEditableWindowIdSet(externalContext).remove(windowContextId);
    }

    public static Set<String> getExistingWindowIdSet(ExternalContext externalContext)
    {
        Set<String> existingWindowIdSet = getEditableWindowIdSet(externalContext);
        return Collections.unmodifiableSet(existingWindowIdSet);
    }

    public static void storeCreatedWindowContextId(ExternalContext externalContext, String windowContextId)
    {
        getEditableWindowIdSet(externalContext).add(windowContextId);
    }

    private static Set<String> getEditableWindowIdSet(ExternalContext externalContext)
    {
        Map<String, Object> sessionMap = externalContext.getSessionMap();

        @SuppressWarnings({"unchecked"})
        Set<String> existingWindowIdSet = (Set)sessionMap.get(EXISTING_WINDOW_ID_SET_KEY);

        if(existingWindowIdSet == null)
        {
            existingWindowIdSet = new HashSet<String>();
            sessionMap.put(EXISTING_WINDOW_ID_SET_KEY, existingWindowIdSet);
        }
        return existingWindowIdSet;
    }

    public static boolean cleanupInactiveWindowContexts(EditableWindowContextManager windowContextManager)
    {
        Collection<EditableWindowContext> windowContexts = windowContextManager.getWindowContexts();
        int count = windowContexts.size();

        for (EditableWindowContext windowContext : windowContexts)
        {
            if(isEligibleForCleanup(windowContext))
            {
                windowContextManager.closeWindowContext(windowContext);
            }
        }

        return windowContexts.size() < count;
    }

    public static Class<? extends Annotation> convertToScope(
            BeanManager beanManager, Class conversationGroupKey, Annotation... qualifiers)
    {
        Class<? extends Annotation> scopeType = conversationGroupToScopeCache.get(conversationGroupKey);

        if(scopeType != null)
        {
            return scopeType;
        }

        if (WindowScoped.class.isAssignableFrom(conversationGroupKey))
        {
            scopeType = WindowScoped.class;
        }
        else
        {
            //we just find a bean if the class name is used as implicit group-key
            //explicit group-keys are only supported for @ConversationScoped
            Set<Bean<?>> beans = beanManager.getBeans(conversationGroupKey, qualifiers);

            if(beans.size() == 1)
            {
                scopeType = beans.iterator().next().getScope();
            }
            else
            {
                scopeType = ConversationScoped.class;
            }
        }

        conversationGroupToScopeCache.put(conversationGroupKey, scopeType);

        return scopeType;
    }

    private static boolean isEligibleForCleanup(EditableWindowContext editableWindowContext)
    {
        return !editableWindowContext.isActive() || editableWindowContext.getConversations().isEmpty();
    }
}
