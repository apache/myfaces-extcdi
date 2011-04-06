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

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationRequired;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.impl.util.AnyLiteral;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * keep in sync with extval!
 *
 * @author Gerhard Petracek
 */
@Typed()
public abstract class JsfUtils
{
    private JsfUtils()
    {
        // prevent instantiation
    }

    /**
     * Resets the conversation cache of the current request
     */
    public static void resetConversationCache()
    {
        RequestCache.resetConversationCache();
    }

    /**
     * Resets all caches of the current request
     */
    public static void resetCaches()
    {
        RequestCache.resetCache();
    }

    /**
     * Adds the given {@link PhaseListener} to the application
     * @param phaseListener current phase-listener
     */
    public static void registerPhaseListener(PhaseListener phaseListener)
    {
        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(
                FactoryFinder.LIFECYCLE_FACTORY);

        String currentId;
        Lifecycle currentLifecycle;
        Iterator lifecycleIds = lifecycleFactory.getLifecycleIds();
        while (lifecycleIds.hasNext())
        {
            currentId = (String) lifecycleIds.next();
            currentLifecycle = lifecycleFactory.getLifecycle(currentId);
            currentLifecycle.addPhaseListener(phaseListener);
        }
    }

    /**
     * Exposes the default message-bundle of jsf for the given {@link Locale}
     * @param locale current local
     * @return default message-bundle
     */
    public static ResourceBundle getDefaultFacesMessageBundle(Locale locale)
    {
        return ResourceBundle.getBundle(FacesMessage.FACES_MESSAGES, locale);
    }

    /**
     * Exposes the (optional) custom message-bundle configured in the faces-config for the given {@link Locale}
     * @param locale current local
     * @return custom message-bundle
     */
    public static ResourceBundle getCustomFacesMessageBundle(Locale locale)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String bundleName = facesContext.getApplication().getMessageBundle();

        if (bundleName == null)
        {
            return null;
        }

        return ResourceBundle.getBundle(bundleName, locale);
    }

    /**
     * Encodes the given value using URLEncoder.encode() with the charset returned
     * from ExternalContext.getResponseCharacterEncoding().
     * This is exactly how the ExternalContext impl encodes URL parameter values.
     *
     * @param value value which should be encoded
     * @param externalContext current external-context
     * @return encoded value
     */
    public static String encodeURLParameterValue(String value, ExternalContext externalContext)
    {
        // copied from MyFaces ServletExternalContextImpl.encodeURL()
        try
        {
            return URLEncoder.encode(value, externalContext.getResponseCharacterEncoding());
        }
        catch (UnsupportedEncodingException e)
        {
            throw new UnsupportedOperationException("Encoding type="
                    + externalContext.getResponseCharacterEncoding() + " not supported", e);
        }
    }

    /**
     * Adds the current request-parameters to the given url
     * @param externalContext current external-context
     * @param url current url
     * @return url with request-parameters
     */
    public static String addRequestParameter(ExternalContext externalContext, String url)
    {
        StringBuilder finalUrl = new StringBuilder(url);
        boolean existingParameters = url.contains("?");

        for(RequestParameter requestParam : getRequestParameters(externalContext, true))
        {
            String key = requestParam.getKey();

            for(String parameterValue : requestParam.getValues())
            {
                if(!url.contains(key + "=" + parameterValue))
                {
                    if(!existingParameters)
                    {
                        finalUrl.append("?");
                        existingParameters = true;
                    }
                    else
                    {
                        finalUrl.append("&");
                    }
                    finalUrl.append(key);
                    finalUrl.append("=");
                    finalUrl.append(JsfUtils.encodeURLParameterValue(parameterValue, externalContext));
                }
            }
        }
        return finalUrl.toString();
    }

    /**
     * Exposes all request-parameters (including or excluding the view-state)
     * @param externalContext current external-context
     * @param filterViewState flag which indicates if the view-state should be added or not
     * @return current request-parameters
     */
    public static Set<RequestParameter> getRequestParameters(ExternalContext externalContext, boolean filterViewState)
    {
        Set<RequestParameter> result = new HashSet<RequestParameter>();

        if(externalContext == null || externalContext.getRequestParameterValuesMap() == null)
        {
            return result;
        }

        String key;
        for(Map.Entry<String, String[]> entry : externalContext.getRequestParameterValuesMap().entrySet())
        {
            key = entry.getKey();
            if(filterViewState && "javax.faces.ViewState".equals(key))
            {
                continue;
            }

            result.add(new RequestParameter(key, entry.getValue()));
        }

        return result;
    }

    /**
     * Checks if the page-bean for the current view hosts {@link ConversationRequired} and the conversation has been
     * started or the current page is an allowed entry-point for the conversation. If a violation is detected,
     * the default-entry-point (specified by {@link ConversationRequired}) will be used as new view
     * which will be rendered.
     *
     * @param facesContext current faces-context
     */
    public static void ensureExistingConversation(FacesContext facesContext)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(uiViewRoot == null)
        {
            return;
        }

        String oldViewId = uiViewRoot.getViewId();

        if(oldViewId == null)
        {
            return;
        }

        ViewConfigDescriptor entry = ViewConfigCache.getViewConfigDescriptor(oldViewId);

        if(entry == null)
        {
            return;
        }

        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

        ConversationConfig conversationConfig =
                CodiUtils.getContextualReferenceByClass(beanManager, ConversationConfig.class);

        if(!conversationConfig.isConversationRequiredEnabled())
        {
            return;
        }

        String newViewId = checkConversationRequired(beanManager, uiViewRoot, entry);

        if(newViewId != null && !oldViewId.equals(newViewId))
        {
            UIViewRoot newViewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, newViewId);

            if(newViewRoot != null)
            {
                facesContext.setViewRoot(newViewRoot);
            }
        }
    }

    private static String checkConversationRequired(BeanManager beanManager,
                                                    UIViewRoot uiViewRoot,
                                                    ViewConfigDescriptor viewConfigDescriptor)
    {
        Class<? extends ViewConfig> currentView =
                ViewConfigCache.getViewConfigDescriptor(uiViewRoot.getViewId()).getViewConfig();

        if(currentView == null)
        {
            return null;
        }

        List<PageBeanDescriptor> pageBeanDescriptorList = viewConfigDescriptor.getPageBeanDescriptors();
        for(PageBeanDescriptor pageBeanDescriptor : pageBeanDescriptorList)
        {
            Class<?> pageBeanClass = pageBeanDescriptor.getBeanClass();

            ConversationRequired conversationRequired =
                    resolveConversationRequiredAnnotation(viewConfigDescriptor, pageBeanDescriptorList, pageBeanClass);

            if(conversationRequired == null)
            {
                continue;
            }

            if(!isEntryPoint(currentView, conversationRequired.defaultEntryPoint(), conversationRequired.entryPoints()))
            {
                EditableWindowContext editableWindowContext =
                        (EditableWindowContext)ConversationUtils.getWindowContextManager().getCurrentWindowContext();

                Set<? extends Bean> foundBeans =
                        beanManager.getBeans(pageBeanDescriptor.getBeanClass(), new AnyLiteral());

                Bean<?> foundBean;
                Set<Bean<?>> beanSet;
                Class<?> conversationGroup;
                for(Bean<?> currentBean : foundBeans)
                {
                    beanSet = new HashSet<Bean<?>>(1);
                    beanSet.add(currentBean);
                    foundBean = beanManager.resolve(beanSet);

                    //only page-beans are supported -> we have to compare them by bean-name
                    if(!pageBeanDescriptor.getBeanName().equals(foundBean.getName()))
                    {
                        continue;
                    }

                    if(ConversationRequired.class.equals(conversationRequired.conversationGroup()))
                    {
                        conversationGroup = ConversationUtils.getConversationGroup(foundBean);
                    }
                    else
                    {
                        conversationGroup = conversationRequired.conversationGroup();
                    }

                    if(!editableWindowContext.isConversationActive(conversationGroup,
                            foundBean.getQualifiers().toArray(new Annotation[foundBean.getQualifiers().size()])))
                    {
                        return ViewConfigCache
                                .getViewConfigDescriptor(conversationRequired.defaultEntryPoint()).getViewId();
                    }
                }
            }
        }
        return null;
    }

    private static ConversationRequired resolveConversationRequiredAnnotation(ViewConfigDescriptor viewConfigDescriptor,
            List<PageBeanDescriptor> pageBeanDescriptorList, Class<?> pageBeanClass)
    {
        ConversationRequired conversationRequired = pageBeanClass.getAnnotation(ConversationRequired.class);

        //here we support just simple constellations
        //TODO handle unsupported constellations
        if(conversationRequired == null && pageBeanDescriptorList.size() == 1)
        {
            List<ConversationRequired> conversationRequiredMetaData =
                    viewConfigDescriptor.getMetaData(ConversationRequired.class);

            if(conversationRequiredMetaData.size() == 1)
            {
                conversationRequired = conversationRequiredMetaData.iterator().next();
            }
        }
        return conversationRequired;
    }

    private static boolean isEntryPoint(Class<? extends ViewConfig> currentView,
                                        Class<? extends ViewConfig> defaultEntryPoint,
                                        Class<? extends ViewConfig>[] entryPoints)
    {
        if(currentView.equals(defaultEntryPoint))
        {
            return true;
        }

        for(Class<? extends ViewConfig> entryPoint : entryPoints)
        {
            if(currentView.equals(entryPoint))
            {
                return true;
            }
        }
        return false;
    }
}
