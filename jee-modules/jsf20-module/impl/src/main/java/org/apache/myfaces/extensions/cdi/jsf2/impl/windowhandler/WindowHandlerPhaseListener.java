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
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;

import javax.faces.application.ResourceHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * JSF Phase Listener to track windowId creation.
 *
 * @author Mark Struberg
 * @author Jakob Korherr
 */
@JsfPhaseListener
public class WindowHandlerPhaseListener implements javax.faces.event.PhaseListener
{
    private Boolean isClientHandlerEnabled = null;

    public PhaseId getPhaseId()
    {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent phaseEvent)
    {
        FacesContext facesContext = phaseEvent.getFacesContext();
        if (facesContext.isPostback())
        {
            return;
        }
        if (isClientHandlerEnabled == null)
        {
            //X TODO gerhard, where got this config moved to?
            //X isClientHandlerEnabled = CodiUtils.getOrCreateScopedInstanceOfBeanByClass(
            isClientHandlerEnabled = Boolean.TRUE; //X TODO get from codi config!
        }

        if (!isClientHandlerEnabled)
        {
            return;
        }
        
        // request/response have to support http
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest httpRequest = (HttpServletRequest) externalContext.getRequest();
        HttpServletResponse httpResponse = (HttpServletResponse) externalContext.getResponse();

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
                return;
            }

            Cookie[] cookies = httpRequest.getCookies();

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
                facesContext.responseComplete();
            }
            else if ("automatedEntryPoint".equals(windowId))
            {
                WindowContext windowContext = windowContextManager.getCurrentWindowContext();
                windowId = windowContext.getId();

                // GET request with NEW windowId - send windowhandlerfilter.html
                sendWindowHandler(httpRequest, httpResponse, clientInfo, windowId);
                facesContext.responseComplete();
            }
            else
            {
                httpRequest.setAttribute("windowId", windowId);
            }
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
    {
        try
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
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    public void afterPhase(PhaseEvent phaseEvent)
    {
       // do nothing
    }


}
