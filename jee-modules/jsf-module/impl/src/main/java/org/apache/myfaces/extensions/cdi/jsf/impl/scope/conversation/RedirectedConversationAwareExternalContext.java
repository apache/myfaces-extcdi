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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.*;

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;

import javax.faces.context.ExternalContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class RedirectedConversationAwareExternalContext extends ExternalContext
{
    private ExternalContext wrapped;

    private WindowHandler windowHandler;

    private boolean encodeActionURLs;

    public RedirectedConversationAwareExternalContext(ExternalContext wrapped)
    {
        this.wrapped = wrapped;
    }

    public String encodeActionURL(String s)
    {
        lazyInit();

        if(this.encodeActionURLs)
        {
            String url = addWindowIdToUrl(s);
            return this.wrapped.encodeActionURL(url);
        }
        return this.wrapped.encodeActionURL(s);
    }

    public void redirect(String url)
            throws IOException
    {
        lazyInit();
        sendRedirect(this.wrapped, url, windowHandler);
    }

    private synchronized void lazyInit()
    {
        if(this.windowHandler == null)
        {
            this.windowHandler = getWindowHandler();
            this.encodeActionURLs = CodiUtils
                    .getOrCreateScopedInstanceOfBeanByClass(ConfigResolver.class)
                    .resolve(JsfAwareWindowContextConfig.class).isAddWindowIdToActionUrlsEnabled();
        }
    }

    private String addWindowIdToUrl(String url)
    {
        return this.windowHandler.encodeURL(url);
    }

    /*
     * generated
     */
    public void dispatch(String s)
            throws IOException
    {
        wrapped.dispatch(s);
    }

    public String encodeNamespace(String s)
    {
        return wrapped.encodeNamespace(s);
    }

    public String encodeResourceURL(String s)
    {
        return wrapped.encodeResourceURL(s);
    }

    public Map<String, Object> getApplicationMap()
    {
        return wrapped.getApplicationMap();
    }

    public String getAuthType()
    {
        return wrapped.getAuthType();
    }

    public Object getContext()
    {
        return wrapped.getContext();
    }

    public String getInitParameter(String s)
    {
        return wrapped.getInitParameter(s);
    }

    public Map getInitParameterMap()
    {
        return wrapped.getInitParameterMap();
    }

    public String getRemoteUser()
    {
        return wrapped.getRemoteUser();
    }

    public Object getRequest()
    {
        return wrapped.getRequest();
    }

    public String getRequestCharacterEncoding()
    {
        return wrapped.getRequestCharacterEncoding();
    }

    public String getRequestContentType()
    {
        return wrapped.getRequestContentType();
    }

    public String getRequestContextPath()
    {
        return wrapped.getRequestContextPath();
    }

    public Map<String, Object> getRequestCookieMap()
    {
        return wrapped.getRequestCookieMap();
    }

    public Map<String, String> getRequestHeaderMap()
    {
        return wrapped.getRequestHeaderMap();
    }

    public Map<String, String[]> getRequestHeaderValuesMap()
    {
        return wrapped.getRequestHeaderValuesMap();
    }

    public Locale getRequestLocale()
    {
        return wrapped.getRequestLocale();
    }

    public Iterator<Locale> getRequestLocales()
    {
        return wrapped.getRequestLocales();
    }

    public Map<String, Object> getRequestMap()
    {
        return wrapped.getRequestMap();
    }

    public Map<String, String> getRequestParameterMap()
    {
        return wrapped.getRequestParameterMap();
    }

    public Iterator<String> getRequestParameterNames()
    {
        return wrapped.getRequestParameterNames();
    }

    public Map<String, String[]> getRequestParameterValuesMap()
    {
        return wrapped.getRequestParameterValuesMap();
    }

    public String getRequestPathInfo()
    {
        return wrapped.getRequestPathInfo();
    }

    public String getRequestServletPath()
    {
        return wrapped.getRequestServletPath();
    }

    public URL getResource(String s)
            throws MalformedURLException
    {
        return wrapped.getResource(s);
    }

    public InputStream getResourceAsStream(String s)
    {
        return wrapped.getResourceAsStream(s);
    }

    public Set<String> getResourcePaths(String s)
    {
        return wrapped.getResourcePaths(s);
    }

    public Object getResponse()
    {
        return wrapped.getResponse();
    }

    public String getResponseContentType()
    {
        return wrapped.getResponseContentType();
    }

    public Object getSession(boolean b)
    {
        return wrapped.getSession(b);
    }

    public Map<String, Object> getSessionMap()
    {
        return wrapped.getSessionMap();
    }

    public Principal getUserPrincipal()
    {
        return wrapped.getUserPrincipal();
    }

    public void setRequest(Object o)
    {
        wrapped.setRequest(o);
    }

    public void setRequestCharacterEncoding(String s)
            throws UnsupportedEncodingException
    {
        wrapped.setRequestCharacterEncoding(s);
    }

    public void setResponse(Object o)
    {
        wrapped.setResponse(o);
    }

    public void setResponseCharacterEncoding(String s)
    {
        wrapped.setResponseCharacterEncoding(s);
    }

    public String getResponseCharacterEncoding()
    {
        return wrapped.getResponseCharacterEncoding();
    }

    public boolean isUserInRole(String s)
    {
        return wrapped.isUserInRole(s);
    }

    public void log(String s)
    {
        wrapped.log(s);
    }

    public void log(String s, Throwable throwable)
    {
        wrapped.log(s, throwable);
    }
}
