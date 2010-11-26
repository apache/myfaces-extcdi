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

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .CREATE_NEW_WINDOW_CONTEXT_ID_VALUE;
import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_ID_PARAMETER_KEY;

/**
 * JSF Phase Listener to track windowId creation.
 *
 * @author Mark Struberg
 * @author Jakob Korherr
 */
@JsfPhaseListener
public class WindowHandlerPhaseListener implements javax.faces.event.PhaseListener
{

    private static final String WINDOW_ID_COOKIE_NAME = "codiWindowId";

    private Boolean isClientHandlerEnabled = null;

    public PhaseId getPhaseId()
    {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent phaseEvent)
    {
        FacesContext facesContext = phaseEvent.getFacesContext();

        if (facesContext.isPostback() || !isClientHandlerEnabled())
        {
            // POST request or not enabled
            return;
        }

        ClientInformation clientInfo =
                CodiUtils.getOrCreateScopedInstanceOfBeanByClass(ClientInformation.class, false);
        if (!clientInfo.isJavaScriptEnabled())
        {
            // no handling possible
            return;
        }
        
        ExternalContext externalContext = facesContext.getExternalContext();

        String windowId = getWindowIdFromCookie(externalContext);
        if (windowId == null)
        {
            // GET request without windowId - send windowhandlerfilter.html
            sendWindowHandler(externalContext, clientInfo, null);
            facesContext.responseComplete();
        }
        else
        {
            EditableWindowContextManager windowContextManager =
                    CodiUtils.getOrCreateScopedInstanceOfBeanByClass(EditableWindowContextManager.class, false);

            if (CREATE_NEW_WINDOW_CONTEXT_ID_VALUE.equals(windowId) || !isWindowIdAlive(windowId, windowContextManager))
            {
                // no or invalid windowId --> create new one
                windowId = windowContextManager.getCurrentWindowContext().getId();

                // GET request with NEW windowId - send windowhandlerfilter.html
                sendWindowHandler(externalContext, clientInfo, windowId);
                facesContext.responseComplete();
            }
            else
            {
                // we have a valid windowId - set it an continue with the request

                //X TODO find better way to provide the windowId, because this approach assumes
                // that the windowId will be cached on the RequestMap and the cache is the only
                // point to get it #HACK
                externalContext.getRequestMap().put(WINDOW_CONTEXT_ID_PARAMETER_KEY, windowId);
            }
        }
    }

    private void sendWindowHandler(ExternalContext externalContext,
                                   ClientInformation clientInfo, String windowId)
    {
        HttpServletResponse httpResponse = (HttpServletResponse) externalContext.getResponse();

        try
        {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("text/html");

            String windowHandlerHtml = clientInfo.getWindowHandlerHtml();

            if (windowId != null)
            {
                // we send the _real_ windowId
                windowHandlerHtml = windowHandlerHtml.replace("uninitializedWindowId", windowId);
            }

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

    private boolean isClientHandlerEnabled()
    {
        if (isClientHandlerEnabled == null)
        {
            //X TODO gerhard, where got this config moved to?
            //X isClientHandlerEnabled = CodiUtils.getOrCreateScopedInstanceOfBeanByClass(
            isClientHandlerEnabled = Boolean.TRUE; //X TODO get from codi config!
        }

        return isClientHandlerEnabled; 
    }

    private String getWindowIdFromCookie(ExternalContext externalContext)
    {
        Cookie cookie = (Cookie) externalContext.getRequestCookieMap().get(WINDOW_ID_COOKIE_NAME);

        if (cookie != null)
        {
            return cookie.getValue();
        }

        return null;
    }

    private boolean isWindowIdAlive(String windowId, EditableWindowContextManager windowContextManager)
    {
        for (EditableWindowContext wc : windowContextManager.getWindowContexts())
        {
            if (windowId.equals(wc.getId()))
            {
                return true;
            }
        }

        return false;
    }

    public void afterPhase(PhaseEvent phaseEvent)
    {
       // do nothing
    }

}
