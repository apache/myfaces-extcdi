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

import javax.enterprise.inject.Typed;
import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.PageParameterContext;

/**
 * keep in sync with extval!
 *
 * @author Gerhard Petracek
 */
@Typed()
public abstract class JsfUtils
{
    public static final String FACES_CONTEXT_MANUAL_WRAPPER_KEY =
            FacesContext.class.getName() + ":manuallyWrappedByCodi";

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
     * @param addRequestParameter flag which indicates if the request params should be added or not
     * @param addPageParameter flag which indicates if the view params should be added or not {@see ViewParameter}
     * @param encodeValues flag which indicates if parameter values should be encoded or not
     * @return url with request-parameters
     */
    public static String addParameters(ExternalContext externalContext, String url,
                                       boolean addRequestParameter, boolean addPageParameter, boolean encodeValues)
    {
        StringBuilder finalUrl = new StringBuilder(url);
        boolean existingParameters = url.contains("?");
        boolean urlContainsWindowId = url.contains(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY + "=");

        for(RequestParameter requestParam :
                getParameters(externalContext, true, addRequestParameter, addPageParameter))
        {
            String key = requestParam.getKey();

            //TODO eval if we should also filter the other params
            if(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY.equals(key) && urlContainsWindowId)
            {
                continue;
            }

            for(String parameterValue : requestParam.getValues())
            {
                if(!url.contains(key + "=" + parameterValue) &&
                        !url.contains(key + "=" + encodeURLParameterValue(parameterValue, externalContext)))
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

                    if(encodeValues)
                    {
                        finalUrl.append(JsfUtils.encodeURLParameterValue(parameterValue, externalContext));
                    }
                    else
                    {
                        finalUrl.append(parameterValue);
                    }
                }
            }
        }
        return finalUrl.toString();
    }

    /**
     * Exposes all request-parameters (including or excluding the view-state)
     * @param externalContext current external-context
     * @param filterViewState flag which indicates if the view-state should be added or not
     * @param addRequestParameter flag which indicates if the request params should be added or not
     * @param addPageParameter flag which indicates if the view params should be added or not {@see ViewParameter}
     * @return current request-parameters
     */
    public static Set<RequestParameter> getParameters(ExternalContext externalContext,
                                                      boolean filterViewState,
                                                      boolean addRequestParameter,
                                                      boolean addPageParameter)
    {
        Set<RequestParameter> result = new HashSet<RequestParameter>();

        if(externalContext == null || externalContext.getRequestParameterValuesMap() == null)
        {
            return result;
        }

        if(addRequestParameter)
        {
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
        }

        if(addPageParameter)
        {
            PageParameterContext pageParameterContext =
                    CodiUtils.getContextualReferenceByClass(PageParameterContext.class, true);

            if(pageParameterContext != null)
            {
                for(Map.Entry<String, String> entry : pageParameterContext.getPageParameters().entrySet())
                {
                    //TODO add multi-value support - see comment in PageParameterContext
                    result.add(new RequestParameter(entry.getKey(), new String[] {entry.getValue()}));
                }
            }
        }

        return result;
    }

    public static <T> T getValueOfExpression(String expression, Class<T> targetType)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (T)facesContext.getApplication().evaluateExpressionGet(facesContext, expression, targetType);
    }

    public static String getValueOfExpressionAsString(String expression)
    {
        Object result = getValueOfExpression(expression, Object.class);

        return result != null ? result.toString() : "null";
    }
}
