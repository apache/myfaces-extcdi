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
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;

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
    private final ExternalContext wrapped;

    private WindowHandler windowHandler;

    private boolean encodeActionURLs;

    /**
     * Constructor for wrapping the given {@link ExternalContext}
     * @param wrapped external-context which should be wrapped
     */
    public RedirectedConversationAwareExternalContext(ExternalContext wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * Adds the current window-id to the URL (if permitted)
     * {@inheritDoc}
     */
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

    /**
     * Triggers a redirect which is aware of the current window and preserves the
     * {@link javax.faces.application.FacesMessage}s.
     * {@inheritDoc}
     */
    public void redirect(String url)
            throws IOException
    {
        lazyInit();
        sendRedirect(this.wrapped, url, this.windowHandler);
    }

    private void lazyInit()
    {
        if(this.windowHandler == null)
        {
            this.windowHandler = getWindowHandler();
            this.encodeActionURLs = CodiUtils
                    .getContextualReferenceByClass(WindowContextConfig.class)
                    .isAddWindowIdToActionUrlsEnabled();
        }
    }

    private String addWindowIdToUrl(String url)
    {
        return this.windowHandler.encodeURL(url);
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public void dispatch(String s)
            throws IOException
    {
        wrapped.dispatch(s);
    }

    /**
     * {@inheritDoc}
     */
    public String encodeNamespace(String s)
    {
        return wrapped.encodeNamespace(s);
    }

    /**
     * {@inheritDoc}
     */
    public String encodeResourceURL(String s)
    {
        return wrapped.encodeResourceURL(s);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getApplicationMap()
    {
        return wrapped.getApplicationMap();
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthType()
    {
        return wrapped.getAuthType();
    }

    /**
     * {@inheritDoc}
     */
    public Object getContext()
    {
        return wrapped.getContext();
    }

    /**
     * {@inheritDoc}
     */
    public String getInitParameter(String s)
    {
        return wrapped.getInitParameter(s);
    }

    /**
     * {@inheritDoc}
     */
    public Map getInitParameterMap()
    {
        return wrapped.getInitParameterMap();
    }

    /**
     * {@inheritDoc}
     */
    public String getRemoteUser()
    {
        return wrapped.getRemoteUser();
    }

    /**
     * {@inheritDoc}
     */
    public Object getRequest()
    {
        return wrapped.getRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestCharacterEncoding()
    {
        return wrapped.getRequestCharacterEncoding();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestContentType()
    {
        return wrapped.getRequestContentType();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestContextPath()
    {
        return wrapped.getRequestContextPath();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getRequestCookieMap()
    {
        return wrapped.getRequestCookieMap();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getRequestHeaderMap()
    {
        return wrapped.getRequestHeaderMap();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getRequestHeaderValuesMap()
    {
        return wrapped.getRequestHeaderValuesMap();
    }

    /**
     * {@inheritDoc}
     */
    public Locale getRequestLocale()
    {
        return wrapped.getRequestLocale();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Locale> getRequestLocales()
    {
        return wrapped.getRequestLocales();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getRequestMap()
    {
        return wrapped.getRequestMap();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getRequestParameterMap()
    {
        return wrapped.getRequestParameterMap();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getRequestParameterNames()
    {
        return wrapped.getRequestParameterNames();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getRequestParameterValuesMap()
    {
        return wrapped.getRequestParameterValuesMap();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestPathInfo()
    {
        return wrapped.getRequestPathInfo();
    }

    /**
     * {@inheritDoc}
     */
    public String getRequestServletPath()
    {
        return wrapped.getRequestServletPath();
    }

    /**
     * {@inheritDoc}
     */
    public URL getResource(String s)
            throws MalformedURLException
    {
        return wrapped.getResource(s);
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getResourceAsStream(String s)
    {
        return wrapped.getResourceAsStream(s);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getResourcePaths(String s)
    {
        return wrapped.getResourcePaths(s);
    }

    /**
     * {@inheritDoc}
     */
    public Object getResponse()
    {
        return wrapped.getResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseContentType()
    {
        return wrapped.getResponseContentType();
    }

    /**
     * {@inheritDoc}
     */
    public Object getSession(boolean b)
    {
        return wrapped.getSession(b);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getSessionMap()
    {
        return wrapped.getSessionMap();
    }

    /**
     * {@inheritDoc}
     */
    public Principal getUserPrincipal()
    {
        return wrapped.getUserPrincipal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequest(Object o)
    {
        wrapped.setRequest(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequestCharacterEncoding(String s)
            throws UnsupportedEncodingException
    {
        wrapped.setRequestCharacterEncoding(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponse(Object o)
    {
        wrapped.setResponse(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponseCharacterEncoding(String s)
    {
        wrapped.setResponseCharacterEncoding(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseCharacterEncoding()
    {
        return wrapped.getResponseCharacterEncoding();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUserInRole(String s)
    {
        return wrapped.isUserInRole(s);
    }

    /**
     * {@inheritDoc}
     */
    public void log(String s)
    {
        wrapped.log(s);
    }

    /**
     * {@inheritDoc}
     */
    public void log(String s, Throwable throwable)
    {
        wrapped.log(s, throwable);
    }
}
