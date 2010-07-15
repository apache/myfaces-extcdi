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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.manager.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.tools.annotate.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.WindowContextIdHolderComponent;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.UuidEntry;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.annotation.Annotation;
import java.io.IOException;

/**
 * internal! utils
 * @author Gerhard Petracek
 */
public class ConversationUtils
{
    public static final String UUID_ID_KEY = "uuid";

    private static final ViewAccessScoped VIEW_ACCESS_SCOPED = DefaultAnnotation.of(ViewAccessScoped.class);

    private static final Jsf JSF_QUALIFIER = DefaultAnnotation.of(Jsf.class);

    private static final String OLD_VIEW_ID_KEY = "oldViewId";
    private static final String NEW_VIEW_ID_KEY = "newViewId";

    /**
     * @return the descriptor of a custom
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * with the qualifier {@link org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf} or
     *         the descriptor of the default implementation provided by this module
     */
    public static Bean<WindowContextManager> resolveConversationManagerBean()
    {
        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

        Set<?> conversationManagerBeans = beanManager.getBeans(WindowContextManager.class, JSF_QUALIFIER);

        if (conversationManagerBeans.isEmpty())
        {
            conversationManagerBeans = getDefaultConversationManager(beanManager);
        }

        if (conversationManagerBeans.size() != 1)
        {
            throw new IllegalStateException(conversationManagerBeans.size() + " conversation-managers were found");
        }
        //noinspection unchecked
        return (Bean<WindowContextManager>) conversationManagerBeans.iterator().next();
    }

    public static Class convertViewAccessScope(Bean<?> bean, Class conversationGroup, Set<Annotation> qualifiers)
    {
        //workaround to keep the existing api
        if(ViewAccessScoped.class.isAssignableFrom(conversationGroup))
        {
            //TODO maybe we have to add a real qualifier instead
            qualifiers.add(VIEW_ACCESS_SCOPED);
            conversationGroup = bean.getBeanClass();
        }
        return conversationGroup;
    }

    public static Class getConversationGroup(Bean<?> bean)
    {
        if(bean.getStereotypes().contains(WindowScoped.class))
        {
            return WindowScoped.class;
        }

        if(bean.getStereotypes().contains(ViewAccessScoped.class))
        {
            return ViewAccessScoped.class;
        }

        ConversationGroup conversationGroupAnnotation = findConversationGroupAnnotation(bean);

        if(conversationGroupAnnotation == null)
        {
            return bean.getBeanClass();
        }

        Class groupClass = conversationGroupAnnotation.value();

        if(WindowScoped.class.isAssignableFrom(groupClass))
        {
            return WindowScoped.class;
        }

        if(ViewAccessScoped.class.isAssignableFrom(groupClass))
        {
            return ViewAccessScoped.class;
        }

        return groupClass;
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

    /**
     * @param beanManager current {@link javax.enterprise.inject.spi.BeanManager}
     * @return the descriptor of the default
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     */
    private static Set<Bean<?>> getDefaultConversationManager(BeanManager beanManager)
    {
        return beanManager.getBeans(WindowContextManager.class);
    }

    //TODO
    public static Long resolveWindowContextId(boolean requestParameterSupported)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map<String, String> requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();

        String uuidKey = requestParameterMap.get(UUID_ID_KEY);

        //try to restore {@link UuidEntry}
        if(uuidKey != null)
        {
            UuidEntry uuidEntry = getUuidEntryMap(facesContext.getExternalContext().getSessionMap()).remove(uuidKey);

            if (uuidEntry != null)
            {
                restoreInformationFromUuidEntry(requestMap, uuidEntry);
            }
        }

        //try to restore get-request parameter
        String idViaGetRequest = null;

        if (requestParameterSupported)
        {
            idViaGetRequest = requestParameterMap.get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
        }

        Long id = null;
        if (idViaGetRequest != null)
        {
            try
            {
                id = Long.parseLong(idViaGetRequest);
            }
            catch (NumberFormatException e)
            {
                id = null;
            }
        }

        //TODO test if we can move it to the beginning
        //try to find id in request map
        if (id == null)
        {
            id = tryToFindWindowIdInRequestMap(requestMap);
        }

        if (id != null)
        {
            return id;
        }

        //try to restore id from component
        WindowContextIdHolderComponent windowContextIdHolder = getWindowContextIdHolderComponent(facesContext);

        if (windowContextIdHolder != null)
        {
            return windowContextIdHolder.getWindowContextId();
        }

        return null;
    }

    private static void restoreInformationFromUuidEntry(Map<String, Object> requestMap, UuidEntry uuidEntry)
    {
        requestMap.put(OLD_VIEW_ID_KEY, uuidEntry.getViewId());

        requestMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, uuidEntry.getWindowContextId());
    }

    private static Long tryToFindWindowIdInRequestMap(Map<String, Object> requestMap)
    {
        return (Long) requestMap.get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }

    public static UuidEntry storeUuidEntry(Map<String, Object> sessionMap, Long windowContextId, String oldViewId)
    {
        UuidEntry uuidEntry = new UuidEntry(windowContextId, oldViewId);
        getUuidEntryMap(sessionMap).put(uuidEntry.getUuid(), uuidEntry);
        return uuidEntry;
    }

    private static Map<String, UuidEntry> getUuidEntryMap(Map<String, Object> sessionMap)
    {
        String key = ConversationUtils.class.getName() + ":uuid:map";
        if(!sessionMap.containsKey(key))
        {
            sessionMap.put(key, new ConcurrentHashMap<String, UuidEntry>());
        }

        //noinspection unchecked
        return (Map<String, UuidEntry>)sessionMap.get(key);
    }

    //TODO
    public static void restoreInformationOfRequest(FacesContext facesContext)
    {
        Map<String, String> requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        Map<String, Object> requstMap = facesContext.getExternalContext().getRequestMap();
        Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();

        requstMap.put(NEW_VIEW_ID_KEY, facesContext.getViewRoot().getViewId());

        String uuidKey = requestParameterMap.get(UUID_ID_KEY);

        if(uuidKey != null)
        {
            UuidEntry uuidEntry = getUuidEntryMap(sessionMap).remove(uuidKey);

            if (uuidEntry != null)
            {
                requstMap.put(OLD_VIEW_ID_KEY, uuidEntry.getViewId());

                requstMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, uuidEntry.getWindowContextId());
            }
        }

        String oldViewId = requestParameterMap.get(OLD_VIEW_ID_KEY);

        if (oldViewId != null)
        {
            requstMap.put(OLD_VIEW_ID_KEY, oldViewId);
        }
    }

    public static void storeCurrentViewIdAsOldViewId(FacesContext facesContext)
    {
        String oldViewId = facesContext.getViewRoot().getViewId();
        facesContext.getExternalContext().getRequestMap().put(OLD_VIEW_ID_KEY, oldViewId);
    }

    public static void storeCurrentViewIdAsNewViewId(FacesContext facesContext)
    {
        String newViewId = facesContext.getViewRoot().getViewId();
        facesContext.getExternalContext().getRequestMap().put(NEW_VIEW_ID_KEY, newViewId);
    }

    public static String getOldViewIdFromRequest(FacesContext facesContext)
    {
        return getOldViewIdFromRequest(facesContext.getExternalContext().getRequestMap());
    }

    public static String getOldViewIdFromRequest(Map<String, Object> requstMap)
    {
        return (String)requstMap.get(OLD_VIEW_ID_KEY);
    }

    public static String getNewViewIdFromRequest(FacesContext facesContext)
    {
        return (String)facesContext.getExternalContext().getRequestMap().get(NEW_VIEW_ID_KEY);
    }

    public static WindowContextIdHolderComponent getWindowContextIdHolderComponent(FacesContext facesContext)
    {
        List<UIComponent> uiComponents = facesContext.getViewRoot().getChildren();
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
        List<UIComponent> uiComponents = facesContext.getViewRoot().getChildren();
        for (UIComponent uiComponent : uiComponents)
        {
            if (uiComponent instanceof WindowContextIdHolderComponent)
            {
                //in this case we have the same view-root
                return;
            }
        }

        facesContext.getViewRoot().getChildren().add(createComponentWithCurrentWindowContextId());
    }

    private static WindowContextIdHolderComponent createComponentWithCurrentWindowContextId()
    {
        Bean<WindowContextManager> conversationManagerBean = resolveConversationManagerBean();

        WindowContextManager conversationManager = CodiUtils.getOrCreateScopedInstanceOfBean(conversationManagerBean);

        return new WindowContextIdHolderComponent(conversationManager.getCurrentWindowContext().getId());
    }

    public static void sendRedirect(ExternalContext externalContext, String url) throws IOException
    {
        Long windowContextId = resolveWindowContextId();

        if (windowContextId != null)
        {
            UuidEntry uuidEntry = storeUuidEntry(externalContext.getSessionMap(),
                                                 windowContextId,
                                                 getOldViewIdFromRequest(externalContext.getRequestMap()));

            url = url + "?" + UUID_ID_KEY + "=" + uuidEntry.getUuid();
            url = externalContext.encodeActionURL(url);
        }

        externalContext.redirect(url);
    }

    private static Long resolveWindowContextId()
    {
        return ConversationUtils.resolveWindowContextId(false
                /*TODO log warning if request parameter is disabled - we have to use false here*/);
    }
}
