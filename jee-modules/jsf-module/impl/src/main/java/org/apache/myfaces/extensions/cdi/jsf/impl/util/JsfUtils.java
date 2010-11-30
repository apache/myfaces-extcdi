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

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * keep in sync with extval!
 *
 * @author Gerhard Petracek
 */
public class JsfUtils
{
    public static void resetConversationCache()
    {
        RequestCache.resetConversationCache();
    }

    public static void resetCaches()
    {
        RequestCache.resetCache();
    }

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

    public static ResourceBundle getDefaultFacesMessageBundle(Locale locale)
    {
        return ResourceBundle.getBundle(FacesMessage.FACES_MESSAGES, locale);
    }

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

    @Deprecated
    public static <T> T getCustomImplementation(Class<T> targetType)
    {
        String className = getWebXmlParameter(targetType.getName());

        if(className != null && !"".equals(className.trim()))
        {
            return ClassUtils.tryToInstantiateClassForName(className, targetType);
        }
        return null;
    }
    
    public static String getWebXmlParameter(String parameterKey)
    {
        return FacesContext.getCurrentInstance().getExternalContext().getInitParameter(parameterKey);
    }

    /**
     * Encodes the given value using URLEncoder.encode() with the charset returned
     * from ExternalContext.getResponseCharacterEncoding().
     * This is exactly how the ExternalContext impl encodes URL parameter values.
     *
     * @param value
     * @param externalContext
     * @return
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

    public static String addRequestParameter(ExternalContext externalContext, String url)
            throws UnsupportedEncodingException
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

    public static Set<RequestParameter> getRequestParameters(ExternalContext externalContext, boolean filterViewState)
    {
        Set<RequestParameter> result = new HashSet<RequestParameter>();

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
}
