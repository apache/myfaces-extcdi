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
package org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.DefaultWindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.jsf2.api.config.ClientInformation;
import org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation.spi.LifecycleAwareWindowHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * WindowHandler which uses JavaScript to store the windowId.
 *
 * @author Mark Struberg
 * @author Jakob Korherr
 */
@Alternative
@ApplicationScoped
public class ClientSideWindowHandler extends DefaultWindowHandler implements LifecycleAwareWindowHandler
{
    private static final long serialVersionUID = 5293942986187078113L;

    private static final String WINDOW_ID_COOKIE_SUFFIX = "-codiWindowId";
    private static final String UNINITIALIZED_WINDOW_ID_VALUE = "uninitializedWindowId";
    private static final String WINDOW_ID_REPLACE_PATTERN = "$$windowIdValue$$";

    @Inject
    private ClientInformation clientInformation;

    @Inject
    private EditableWindowContextManager windowContextManager;

    protected ClientSideWindowHandler()
    {
        // needed for proxying
    }

    @Inject
    protected ClientSideWindowHandler(WindowContextConfig config)
    {
        super(config);
    }

    @Override
    public String encodeURL(String url)
    {
        if (this.clientInformation.isJavaScriptEnabled())
        {
            // do not add the windowId
            return url;
        }
        else
        {
            // fallback
            return super.encodeURL(url);
        }
    }

    @Override
    public String restoreWindowId(ExternalContext externalContext)
    {
        if (this.clientInformation.isJavaScriptEnabled())
        {
            return (String) externalContext.getRequestMap().get(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
        }
        else
        {
            // fallback
            return super.restoreWindowId(externalContext);
        }
    }

    public void beforeLifecycleExecute(FacesContext facesContext)
    {
        if (!isClientSideWindowHandlerRequest(facesContext))
        {
            return;
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        String windowId = getWindowIdFromCookie(externalContext);
        if (windowId == null)
        {
            // GET request without windowId - send windowhandlerfilter.html to get the windowId
            sendWindowHandlerHtml(externalContext, null);
            facesContext.responseComplete();
        }
        else
        {
            if (WindowContextManager.AUTOMATED_ENTRY_POINT_PARAMETER_KEY.equals(windowId)
                    || !ConversationUtils.isWindowActive(this.windowContextManager, windowId))
            {
                // no or invalid windowId --> create new one
                // don't use createWindowId() the following call will ensure the max. window context count,...
                windowId = this.windowContextManager.getCurrentWindowContext().getId();

                // GET request with NEW windowId - send windowhandlerfilter.html to set and re-get the windowId
                sendWindowHandlerHtml(externalContext, windowId);
                facesContext.responseComplete();
            }
            else
            {
                // we have a valid windowId - set it and continue with the request
                // TODO only set internally and provide via restoreWindowId()? 
                externalContext.getRequestMap().put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, windowId);
            }
        }
    }

    private boolean isClientSideWindowHandlerRequest(FacesContext facesContext)
    {
        // no POST request and javascript enabled
        // NOTE that for POST-requests the windowId is saved in the state (see WindowContextIdHolderComponent)
        return !facesContext.isPostback() && this.clientInformation.isJavaScriptEnabled();
    }

    private void sendWindowHandlerHtml(ExternalContext externalContext, String windowId)
    {
        HttpServletResponse httpResponse = (HttpServletResponse) externalContext.getResponse();

        try
        {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("text/html");

            String windowHandlerHtml = this.clientInformation.getWindowHandlerHtml();

            if (windowId == null)
            {
                windowId = UNINITIALIZED_WINDOW_ID_VALUE;
            }

            // set the windowId value in the javascript code
            windowHandlerHtml = windowHandlerHtml.replace(WINDOW_ID_REPLACE_PATTERN, windowId);

            OutputStream os = httpResponse.getOutputStream();
            try
            {
                os.write(windowHandlerHtml.getBytes());
            }
            finally
            {
                os.close();
            }
        }
        catch (IOException ioe)
        {
            throw new FacesException(ioe);
        }
    }

    private String getWindowIdFromCookie(ExternalContext externalContext)
    {
        String cookieName = getEncodedPathName(externalContext) + WINDOW_ID_COOKIE_SUFFIX;
        Cookie cookie = (Cookie) externalContext.getRequestCookieMap().get(cookieName);

        if (cookie == null)
        {
            // if the current request went to a welcome page, we should only consider the contextPath
            cookieName = getEncodedContextPath(externalContext) + WINDOW_ID_COOKIE_SUFFIX;
            cookie = (Cookie) externalContext.getRequestCookieMap().get(cookieName);
        }

        if (cookie != null)
        {
            return cookie.getValue();
        }

        return null;
    }

    private String getEncodedPathName(ExternalContext externalContext)
    {
        StringBuilder sb = new StringBuilder();

        String contextPath = externalContext.getRequestContextPath();
        if (contextPath != null)
        {
            sb.append(contextPath);
        }

        String servletPath = externalContext.getRequestServletPath();
        if (servletPath != null)
        {
            sb.append(servletPath);
        }

        String pathInfo = externalContext.getRequestPathInfo();
        if (pathInfo != null)
        {
            sb.append(pathInfo);
        }

        // remove all "/", because they can be different in JavaScript
        String pathName = sb.toString().replace("/", "");

        return JsfUtils.encodeURLParameterValue(pathName, externalContext);
    }

    private String getEncodedContextPath(ExternalContext externalContext)
    {
        String contextPath = externalContext.getRequestContextPath();
        if (contextPath != null)
        {
            // remove all "/", because they can be different in JavaScript
            contextPath = contextPath.replace("/", "");

            return JsfUtils.encodeURLParameterValue(contextPath, externalContext);
        }

        return "";
    }
}
