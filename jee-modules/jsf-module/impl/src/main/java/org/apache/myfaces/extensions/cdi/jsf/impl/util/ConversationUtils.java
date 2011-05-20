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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.JsfModuleConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.request.FacesMessageEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.WindowContextIdHolderComponent;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.ViewAccessConversationExpirationEvaluatorRegistry;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.message.api.Message;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.Typed;
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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * internal! utils
 * @author Gerhard Petracek
 */
@Typed()
public abstract class ConversationUtils
{
    public static final String EXISTING_WINDOW_ID_SET_KEY =
            WindowContext.class.getName() + ":EXISTING_WINDOW_ID_LIST";

    private static final String OLD_VIEW_ID_KEY = "oldViewId";
    private static final String NEW_VIEW_ID_KEY = "newViewId";

    private static Map<Class, Class<? extends Annotation>> conversationGroupToScopeCache =
            new ConcurrentHashMap<Class, Class<? extends Annotation>>();

    private static final String REDIRECT_PERFORMED_KEY = WindowHandler.class.getName() + "redirect:KEY";

    private ConversationUtils()
    {
        // prevent instantiation
    }

    /**
     * Calculates the conversation-group for a given {@link Bean}.
     * Conversation-groups are only supported in combination with
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped}
     * @param bean current bean
     * @return class which represents the conversation-group
     */
    public static Class getConversationGroup(Bean<?> bean)
    {
        Class<? extends Annotation> scopeType = bean.getScope();

        if(ViewAccessScoped.class.isAssignableFrom(scopeType))
        {
            return bean.getBeanClass();
        }

        if(WindowScoped.class.isAssignableFrom(scopeType))
        {
            return WindowScoped.class;
        }

        //don't cache it (due to the support of different producers)
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

    /**
     * Tries to resolve the window-id via {@link WindowHandler}, request-parameters, request-map, component
     * @param windowHandler current window-handler
     * @param requestParameterSupported flag which indicates if it is allowed to restore the id from the request-params
     * @param allowUnknownWindowIds flag which indicates if it is allowed to use id's which haven't been created for
     * the current user
     * @return restored window-id, null otherwise
     */
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

    /**
     * Stores the view-id of the current {@link FacesContext} as prev. view-id e.g. before a navigation occurs
     * @param facesContext current faces-context
     */
    public static void storeCurrentViewIdAsOldViewId(FacesContext facesContext
    /*TODO add window context as parameter and test it in combination with redirects*/)
    {
        storeCurrentViewIdAsOldViewId(facesContext, getWindowContextManager());
    }

    /**
     * Stores the view-id of the current {@link FacesContext} as prev. view-id e.g. before a navigation occurs
     * @param facesContext current faces-context
     * @param windowContextManager current window-context-manager
     */
    public static void storeCurrentViewIdAsOldViewId(
            FacesContext facesContext, WindowContextManager windowContextManager)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(uiViewRoot != null)
        {
            String oldViewId =  uiViewRoot.getViewId();
            windowContextManager.getCurrentWindowContext().setAttribute(OLD_VIEW_ID_KEY, oldViewId);
        }
    }

    /**
     * Stores the view-id of the current {@link FacesContext} as next view-id e.g. after a navigation occurred
     * @param facesContext current faces-context
     */
    public static void storeCurrentViewIdAsNewViewId(FacesContext facesContext)
    {
        storeCurrentViewIdAsNewViewId(facesContext, getWindowContextManager().getCurrentWindowContext());
    }

    /**
     * Stores the view-id of the current {@link FacesContext} as next view-id e.g. after a navigation occurred
     * @param facesContext current faces-context
     * @param windowContext current window-context
     */
    public static void storeCurrentViewIdAsNewViewId(FacesContext facesContext, WindowContext windowContext)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(uiViewRoot != null)
        {
            String newViewId = uiViewRoot.getViewId();
            storeViewIdAsNewViewId(windowContext, newViewId);
        }
    }

    /**
     * Stores the given view-id as next view-id e.g. after a navigation occurred
     * @param windowContext current window-context
     * @param newViewId next view-id
     */
    public static void storeViewIdAsNewViewId(WindowContext windowContext, String newViewId)
    {
        windowContext.setAttribute(NEW_VIEW_ID_KEY, newViewId);
    }

    /**
     * Exposes the prev. view-id.
     * @return prev. view-id
     */
    public static String getOldViewId()
    {
        return getWindowContextManager().getCurrentWindowContext().getAttribute(OLD_VIEW_ID_KEY, String.class);
    }

    /**
     * Exposes the next view-id.
     * @return next view-id
     */
    public static String getNewViewId()
    {
        return getWindowContextManager().getCurrentWindowContext().getAttribute(NEW_VIEW_ID_KEY, String.class);
    }

    /**
     * Resolves {@link WindowContextIdHolderComponent} which is responsible for storing the window-id in case of a
     * server-side window-handler.
     * @param facesContext current faces-context
     * @return window-id holder which has been found, null otherwise
     */
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

    /**
     * Needed for server-side window-handler and client-side window handler for supporting postbacks
     */
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
        WindowContextManager conversationManager = CodiUtils.getContextualReferenceByClass(WindowContextManager.class);

        return new WindowContextIdHolderComponent(conversationManager.getCurrentWindowContext().getId());
    }

    /**
     * Triggers a redirect via the {@link ExternalContext} or the current {@link WindowHandler}, resets caches and
     * prevents {@link javax.faces.application.FacesMessage}s
     * @param externalContext current external-context
     * @param url target URL
     * @param windowHandler current window-handler
     * @throws IOException in case of a failed redirect
     */
    public static void sendRedirect(ExternalContext externalContext,
                                    String url,
                                    WindowHandler windowHandler) throws IOException
    {
        if(isMultipleRedirectDetected(externalContext) || FacesContext.getCurrentInstance().getResponseComplete())
        {
            return;
        }
        else
        {
            redirectPerformed(externalContext);
        }

        storeCurrentViewIdAsOldViewId(FacesContext.getCurrentInstance());

        RequestCache.resetCache();

        //don't use @AfterFacesRequest event
        //there might be an issue if the ServletRequestListener e.g. of OWB gets called earlier
        saveFacesMessages(externalContext);

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

    private static void saveFacesMessages(ExternalContext externalContext)
    {
        JsfModuleConfig jsfModuleConfig = CodiUtils.getContextualReferenceByClass(JsfModuleConfig.class);

        if(jsfModuleConfig != null && jsfModuleConfig.isAlwaysKeepMessages())
        {
            Map<String, Object> requestMap = externalContext.getRequestMap();

            @SuppressWarnings({"unchecked"})
            List<FacesMessageEntry> facesMessageEntryList =
                    (List<FacesMessageEntry>)requestMap.get(Message.class.getName());

            if(facesMessageEntryList == null)
            {
                facesMessageEntryList = new CopyOnWriteArrayList<FacesMessageEntry>();
            }
            getWindowContextManager().getCurrentWindowContext()
                    .setAttribute(Message.class.getName(), facesMessageEntryList, true);
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

    /**
     * Resolves the current {@link WindowHandler}
     * @return current window-handler
     */
    public static WindowHandler getWindowHandler()
    {
        return CodiUtils.getContextualReferenceByClass(WindowHandler.class);
    }

    /**
     * Resolves the current {@link WindowContextManager}
     * @return current window-context-manager
     */
    public static WindowContextManager getWindowContextManager()
    {
        return RequestCache.getWindowContextManager();
    }

    /**
     * Allows to remove a window-id which has been created for a user(-session)
     * @param externalContext current external-context
     * @param windowContextId window-id to remove
     * @return true if it was a known window-id, false otherwise
     */
    public static boolean removeExistingWindowId(ExternalContext externalContext, String windowContextId)
    {
        return getEditableWindowIdSet(externalContext).remove(windowContextId);
    }

    /**
     * Exposes an unmodifiable representation of the active window-ids
     * which have been generated and stored for the current user
     * @param externalContext current external-context
     * @return active window-ids
     */
    public static Set<String> getExistingWindowIdSet(ExternalContext externalContext)
    {
        Set<String> existingWindowIdSet = getEditableWindowIdSet(externalContext);
        return Collections.unmodifiableSet(existingWindowIdSet);
    }

    /**
     * Allows to store the given window-id as active window-id
     * @param externalContext current external-context
     * @param windowContextId window-id
     */
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

    /**
     * Allows to cleanup empty or inactive {@link WindowContext}s which saves memory
     * @param windowContextManager current window-context-manager
     * @return true if 1-n instances were destroyed, false otherwise
     */
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

    /**
     * Maps the given conversation-group-key to a scope-annotation
     * @param beanManager current bean-manager
     * @param conversationGroupKey current conversation-group key
     * @param qualifiers current qualifiers
     * @return annotation-class of the scope
     */
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
                if(!beanManager.getClass().getName().startsWith("org.apache.webbeans."))
                {
                    //workaround for weld v1.1.1
                    Bean<?> bean = WeldCache.getBean();
                    if(bean != null)
                    {
                        scopeType = bean.getScope();
                    }
                }

                //default behaviour for cdi implementations without bug
                if(scopeType == null)
                {
                    scopeType = ConversationScoped.class;
                }
            }
        }

        conversationGroupToScopeCache.put(conversationGroupKey, scopeType);

        return scopeType;
    }

    private static boolean isEligibleForCleanup(EditableWindowContext editableWindowContext)
    {
        return !editableWindowContext.isActive() || editableWindowContext.getConversations().isEmpty();
    }

    /**
     * alternative to {@link ConversationUtils#getExistingWindowIdSet} because it might be deactivated...
     *
     * @param windowContextManager current windowContextManager
     * @param windowId windowId in question
     * @return true if the window is known and active, false otherwise
     */
    public static boolean isWindowActive(EditableWindowContextManager windowContextManager, String windowId)
    {
        for (EditableWindowContext editableWindowContext : windowContextManager.getWindowContexts())
        {
            if (windowId.equals(editableWindowContext.getId()) && editableWindowContext.isActive())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Performs the cleanup of inactive and empty {@link WindowContext}s (if permitted) and resets caches
     * @param facesContext current faces-context
     */
    //don't move it to an observer due to an unpredictable invocation order
    public static void postRenderCleanup(FacesContext facesContext)
    {
        try
        {
            BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

            EditableWindowContextManager windowContextManager =
                    CodiUtils.getContextualReferenceByClass(beanManager, EditableWindowContextManager.class);

            WindowContextConfig windowContextConfig =
                    CodiUtils.getContextualReferenceByClass(beanManager, WindowContextConfig.class);

            ViewAccessConversationExpirationEvaluatorRegistry registry =
                    CodiUtils.getContextualReferenceByClass(
                            beanManager, ViewAccessConversationExpirationEvaluatorRegistry.class);

            UIViewRoot uiViewRoot = facesContext.getViewRoot();

            //e.g. in case of a ViewExpiredException (e.g. in case of an expired session)
            if(uiViewRoot == null)
            {
                return;
            }

            registry.broadcastRenderedViewId(uiViewRoot.getViewId());

            storeCurrentViewIdAsOldViewId(facesContext);

            if(windowContextConfig.isCloseEmptyWindowContextsEnabled())
            {
                cleanupInactiveWindowContexts(windowContextManager);
            }
        }
        catch (ContextNotActiveException e)
        {
            if(ProjectStageProducer.getInstance().getProjectStage() ==
                                org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage.Development)
            {
                e.printStackTrace();
            }
            //we can ignore the exception because it's just an optional (immediate) cleanup
        }

        //if the cache would get resetted by an observer or a phase-listener
        //it might be the case that a 2nd observer accesses the cache again and afterwards there won't be a cleanup
        //-> don't remove:
        RequestCache.resetCache();
    }
}
