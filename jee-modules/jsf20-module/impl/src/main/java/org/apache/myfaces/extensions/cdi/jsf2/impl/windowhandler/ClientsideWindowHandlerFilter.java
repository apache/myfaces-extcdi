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
package org.apache.myfaces.extensions.cdi.jsf2.impl.windowhandler;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO
 *
 * @author Jakob Korherr
 * @author Mark Struberg
 */
public class ClientsideWindowHandlerFilter implements Filter
{

    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException
    {
        // request/response have to support http
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if ("GET".equals(httpRequest.getMethod()) && !isResourceRequest(httpRequest))
        {
            // only for GET requests

            ClientInformation clientInfo =
                    CodiUtils.getOrCreateScopedInstanceOfBeanByClass(ClientInformation.class, false);

            EditableWindowContextManager windowContextManager =
                    CodiUtils.getOrCreateScopedInstanceOfBeanByClass(EditableWindowContextManager.class, false);

            String windowId = null;
            if (!clientInfo.isJavaScriptEnabled())
            {
                // no handling possible
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();

            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if ("codiWindowId".equals(cookie.getName()))
                    {
                        windowId = cookie.getValue();
                        cookie.setMaxAge(0);
                        break;
                    }
                }
            }

            if (windowId == null)
            {
                // GET request without windowId - send windowhandlerfilter.html
                sendWindowHandler(httpRequest, httpResponse, clientInfo, null);
            }
            else if ("automatedEntryPoint".equals(windowId))
            {
                WindowContext windowContext = windowContextManager.getCurrentWindowContext();
                windowId = windowContext.getId();

                // GET request with NEW windowId - send windowhandlerfilter.html
                sendWindowHandler(httpRequest, httpResponse, clientInfo, windowId);
            }
            else
            {
                // GET request with windowId from Cookie

                // pass through with WindowIdServletRequestWrapper
                //X TODO don't think this is the best way to do it!
                //X TODO we should tell the requests WindowManager the id directly!
                //X otherwise we might get the windowId = xxxx in a link somewhere...
                WindowIdServletRequestWrapper requestWrapper = new WindowIdServletRequestWrapper(httpRequest, windowId);
                filterChain.doFilter(requestWrapper, servletResponse);
            }
        }
        else
        {
            // POST or resource request - no handling necessary
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy()
    {
    }

    private boolean isResourceRequest(HttpServletRequest httpRequest)
    {
        // TODO more detail: copy algorithm from ResourceHandlerImpl

        String requestURL = httpRequest.getRequestURL().toString();

        return requestURL.contains(ResourceHandler.RESOURCE_IDENTIFIER);
    }

    private void sendWindowHandler(HttpServletRequest req, HttpServletResponse resp,
                                   ClientInformation clientInfo, String windowId)
            throws ServletException, IOException
    {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html");

        String windowHandlerHtml = clientInfo.getWindowHandlerHtml();

        if (windowId != null)
        {
            // we send the _real_ windowId
            windowHandlerHtml = windowHandlerHtml.replace("automatedEntryPoint", windowId);
        }

        OutputStream os = resp.getOutputStream();
        try
        {
                os.write(windowHandlerHtml.getBytes());
        }
        finally
        {
            os.close();
        }
    }

}
