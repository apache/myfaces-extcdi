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
import static org.apache.myfaces.extensions.cdi.core.api.manager.BeanManagerProvider.getInstance;
import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.tools.annotate.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.WindowContextIdHolderComponent;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.RedirectHandler;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * internal! utils
 * @author Gerhard Petracek
 */
public class ConversationUtils
{
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
        Set<Class<? extends Annotation>> stereotypes = bean.getStereotypes();

        if(stereotypes.contains(WindowScoped.class))
        {
            return WindowScoped.class;
        }

        if(stereotypes.contains(ViewAccessScoped.class))
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
    public static Long resolveWindowContextId(boolean requestParameterSupported, RedirectHandler redirectHandler)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map<String, String> requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();

        //try to find id in request map
        Long id = tryToFindWindowIdInRequestMap(requestMap);

        if(id == null && redirectHandler != null)
        {
            id = redirectHandler.restoreWindowId(facesContext.getExternalContext());
        }

        if(id == null)
        {
            id = tryToRestoreWindowIdFromRequestParameterMap(requestParameterSupported, requestParameterMap);
        }

        if(id != null)
        {
            cacheWindowId(requestMap, id);
        }

        if (id != null)
        {
            return id;
        }

        //try to restore id from component
        WindowContextIdHolderComponent windowContextIdHolder = getWindowContextIdHolderComponent(facesContext);

        if (windowContextIdHolder != null)
        {
            requestMap.put(
                    WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, windowContextIdHolder.getWindowContextId());

            //TODO cache for request
            return windowContextIdHolder.getWindowContextId();
        }

        return null;
    }

    private static Long tryToRestoreWindowIdFromRequestParameterMap(
            boolean requestParameterSupported, Map<String, String> requestParameterMap)
    {
        //try to restore get-request parameter
        String idViaGetRequest = null;

        if (requestParameterSupported)
        {
            idViaGetRequest = requestParameterMap.get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
        }

        return parseWindowId(idViaGetRequest);
    }

    public static Long parseWindowId(String windowIdAsString)
    {
        if (windowIdAsString != null)
        {
            try
            {
                return Long.parseLong(windowIdAsString);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
        return null;
    }

    public static void cacheWindowId(Long id)
    {
        cacheWindowId(FacesContext.getCurrentInstance().getExternalContext().getRequestMap(), id);
    }
    
    private static void cacheWindowId(Map<String, Object> requestMap, Long id)
    {
        requestMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, id);
    }

    private static Long tryToFindWindowIdInRequestMap(Map<String, Object> requestMap)
    {
        return (Long) requestMap.get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }

    public static void restoreInformationOfRequest(FacesContext facesContext,
                                                   WindowContextManager windowContextManager)
    {
        WindowContext windowContext = windowContextManager.getCurrentWindowContext();
        windowContext.setAttribute(NEW_VIEW_ID_KEY, facesContext.getViewRoot().getViewId());
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

    public static void sendRedirect(ExternalContext externalContext,
                                    String url,
                                    RedirectHandler redirectHandler) throws IOException
    {
        storeCurrentViewIdAsOldViewId(FacesContext.getCurrentInstance());

        RequestCache.resetCache();
        
        if(redirectHandler != null)
        {
            redirectHandler.sendRedirect(
                    externalContext, url, getWindowContextManager().getCurrentWindowContext().getId());
        }
        else
        {
            //TODO log warning in case of project stage dev.
            externalContext.redirect(url);
        }
    }

    public static RedirectHandler getRedirectHandler()
    {
        Set<Bean<?>> configResolvers = getInstance().getBeanManager().getBeans(ConfigResolver.class);

        //TODO
        ConfigResolver configResolver = (ConfigResolver) CodiUtils
                .getOrCreateScopedInstanceOfBean(configResolvers.iterator().next());

        return configResolver.resolve(JsfAwareWindowContextConfig.class).getRedirectHandler();
    }

    public static WindowContextManager getWindowContextManager()
    {
        return RequestCache.getWindowContextManager();
    }
}
