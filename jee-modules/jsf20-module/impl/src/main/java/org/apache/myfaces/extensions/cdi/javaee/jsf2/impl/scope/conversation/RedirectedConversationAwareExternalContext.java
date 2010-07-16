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
package org.apache.myfaces.extensions.cdi.javaee.jsf2.impl.scope.conversation;

import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.*;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.RedirectProcessor;

import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import java.net.URL;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 */
public class RedirectedConversationAwareExternalContext extends ExternalContext
{
    private final ExternalContext wrapped;

    private RedirectProcessor redirectProcessor;

    public RedirectedConversationAwareExternalContext(ExternalContext wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public void addResponseCookie(String s, String s1, Map<String, Object> stringObjectMap)
    {
        wrapped.addResponseCookie(s, s1, stringObjectMap);
    }

    @Override
    public void addResponseHeader(String s, String s1)
    {
        wrapped.addResponseHeader(s, s1);
    }

    public void dispatch(String s)
            throws IOException
    {
        wrapped.dispatch(s);
    }

    public String encodeActionURL(String s)
    {
        return wrapped.encodeActionURL(s);
    }

    @Override
    public String encodeBookmarkableURL(String s, Map<String, List<String>> stringListMap)
    {
        return wrapped.encodeBookmarkableURL(s, stringListMap);
    }

    public String encodeNamespace(String s)
    {
        return wrapped.encodeNamespace(s);
    }

    @Override
    public String encodePartialActionURL(String s)
    {
        return wrapped.encodePartialActionURL(s);
    }

    @Override
    public String encodeRedirectURL(String s, Map<String, List<String>> stringListMap)
    {
        return wrapped.encodeRedirectURL(s, stringListMap);
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

    @Override
    public String getContextName()
    {
        return wrapped.getContextName();
    }

    @Override
    public Flash getFlash()
    {
        return wrapped.getFlash();
    }

    public String getInitParameter(String s)
    {
        return wrapped.getInitParameter(s);
    }

    public Map getInitParameterMap()
    {
        return wrapped.getInitParameterMap();
    }

    @Override
    public String getMimeType(String s)
    {
        return wrapped.getMimeType(s);
    }

    @Override
    public String getRealPath(String s)
    {
        return wrapped.getRealPath(s);
    }

    public String getRemoteUser()
    {
        return wrapped.getRemoteUser();
    }

    public Object getRequest()
    {
        return wrapped.getRequest();
    }

    @Override
    public String getRequestCharacterEncoding()
    {
        //TODO codi config
        return wrapped.getRequestCharacterEncoding();
    }

    @Override
    public int getRequestContentLength()
    {
        return wrapped.getRequestContentLength();
    }

    @Override
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

    @Override
    public String getRequestScheme()
    {
        return wrapped.getRequestScheme();
    }

    @Override
    public String getRequestServerName()
    {
        return wrapped.getRequestServerName();
    }

    public int getRequestServerPort()
    {
        return wrapped.getRequestServerPort();
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

    @Override
    public int getResponseBufferSize()
    {
        return wrapped.getResponseBufferSize();
    }

    @Override
    public String getResponseCharacterEncoding()
    {
        //TODO codi config - depending on getResponseContentType

        return wrapped.getResponseCharacterEncoding();
    }

    @Override
    public String getResponseContentType()
    {
        return wrapped.getResponseContentType();
    }

    @Override
    public OutputStream getResponseOutputStream()
            throws IOException
    {
        return wrapped.getResponseOutputStream();
    }

    @Override
    public Writer getResponseOutputWriter()
            throws IOException
    {
        return wrapped.getResponseOutputWriter();
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

    @Override
    public void invalidateSession()
    {
        wrapped.invalidateSession();
    }

    @Override
    public boolean isResponseCommitted()
    {
        return wrapped.isResponseCommitted();
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

    @Override
    public void responseFlushBuffer()
            throws IOException
    {
        wrapped.responseFlushBuffer();
    }

    @Override
    public void responseReset()
    {
        wrapped.responseReset();
    }

    @Override
    public void responseSendError(int i, String s)
            throws IOException
    {
        wrapped.responseSendError(i, s);
    }

    @Override
    public void setRequest(Object o)
    {
        wrapped.setRequest(o);
    }

    @Override
    public void setRequestCharacterEncoding(String s)
            throws UnsupportedEncodingException
    {
        wrapped.setRequestCharacterEncoding(s);
    }

    @Override
    public void setResponse(Object o)
    {
        wrapped.setResponse(o);
    }

    @Override
    public void setResponseBufferSize(int i)
    {
        wrapped.setResponseBufferSize(i);
    }

    @Override
    public void setResponseCharacterEncoding(String s)
    {
        wrapped.setResponseCharacterEncoding(s);
    }

    @Override
    public void setResponseContentLength(int i)
    {
        wrapped.setResponseContentLength(i);
    }

    @Override
    public void setResponseContentType(String s)
    {
        wrapped.setResponseContentType(s);
    }

    @Override
    public void setResponseHeader(String s, String s1)
    {
        wrapped.setResponseHeader(s, s1);
    }

    @Override
    public void setResponseStatus(int i)
    {
        wrapped.setResponseStatus(i);
    }

    public void redirect(String url)
            throws IOException
    {
        lazyInit();
        sendRedirect(this.wrapped, url, this.redirectProcessor);
    }

    private synchronized void lazyInit()
    {
        if(this.redirectProcessor == null)
        {
            this.redirectProcessor = getRedirectProcessor();
        }
    }
}
