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

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * TODO
 *
 * @author Jakob Korherr
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
            String windowIdParam = servletRequest.getParameter("windowId");

            if (windowIdParam == null)
            {
                // GET request without windowId - send windowhandler.html
                sendWindowHandler(httpRequest, httpResponse);
            }
            else
            {
                // GET request with windowId (from AJAX)

                // generate new windowId and set it as response header
                String windowId; //X TODO get new windowId from CODI algorithm
                //X TODO temp - random windowId
                windowId = UUID.randomUUID().toString().replace("-", "");

                // set response header for JavaScript
                httpResponse.setHeader("myfaces-codi-windowId", windowId);

                // pass through with WindowIdServletRequestWrapper
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

    private void sendWindowHandler(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html");

        InputStream is = ClassUtils.getClassLoader(null).getResourceAsStream("static/windowhandlerfilter.html");
        OutputStream os = resp.getOutputStream();
        try
        {
            byte[] buf = new byte[16 * 4096];
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1)
            {
                os.write(buf, 0, bytesRead);
            }
        }
        finally
        {
            is.close();
            os.close();
        }
    }

}
