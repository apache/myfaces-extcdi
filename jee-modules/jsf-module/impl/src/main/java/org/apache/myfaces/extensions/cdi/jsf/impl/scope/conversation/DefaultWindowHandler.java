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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_ID_PARAMETER_KEY;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.getExistingWindowIdSet;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.getWindowContextIdHolderComponent;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultWindowHandler implements WindowHandler
{
    private static final long serialVersionUID = -103516988654873089L;

    private static final int DEFAULT_WINDOW_KEY_LENGTH = 3;

    private static final String WINDOW_ID_PARAMETER_KEY = WINDOW_CONTEXT_ID_PARAMETER_KEY + "=";

    protected boolean useWindowAwareUrlEncoding;

    protected DefaultWindowHandler()
    {
    }

    @Inject
    protected DefaultWindowHandler(WindowContextConfig config)
    {
        this.useWindowAwareUrlEncoding = config.isUrlParameterSupported();
    }

    public String encodeURL(String url)
    {
        if(this.useWindowAwareUrlEncoding)
        {
            return addWindowIdIfNecessary(url, getCurrentWindowId());
        }
        return url;
    }

    public void sendRedirect(ExternalContext externalContext, String url, boolean addRequestParameter)
            throws IOException
    {
        //X TODO windowId is added "twice" here, once in encodeURL() and once in externalContext.encodeActionURL()
        // see RedirectedConversationAwareExternalContext.encodeActionURL().

        // add windowId if necessary
        url = encodeURL(url);

        if(addRequestParameter)
        {
            url = addRequestParameter(externalContext, url);
        }

        // call encodeActionURL() after all parameters have been added
        url = externalContext.encodeActionURL(url);

        externalContext.redirect(url);
    }

    protected String addRequestParameter(ExternalContext externalContext, String url)
            throws UnsupportedEncodingException
    {
        StringBuilder finalUrl = new StringBuilder(url);

        Map<String, String> requestParms = externalContext.getRequestParameterMap();
        for(Map.Entry<String, String> requestParam : requestParms.entrySet())
        {
            String key = requestParam.getKey();

            if(!url.contains(key + "="))
            {
                finalUrl.append("&");
                finalUrl.append(key);
                finalUrl.append("=");
                finalUrl.append(JsfUtils.encodeURLParameterValue(requestParam.getValue(), externalContext));
            }
        }
        return finalUrl.toString();
    }

    //TODO add a counter in case of project stage dev
    public String createWindowId()
    {
        String oldWindowContextId = resolveExpiredWindowContextId();

        if(oldWindowContextId != null)
        {
            return oldWindowContextId;
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        synchronized (externalContext.getSessionMap())
        {
            Set<String> existingWindowIdSet = getExistingWindowIdSet(externalContext);

            String shortUuid;
            int startIndex = 0;

            while(startIndex + DEFAULT_WINDOW_KEY_LENGTH < uuid.length())
            {
                shortUuid = uuid.substring(startIndex, startIndex + DEFAULT_WINDOW_KEY_LENGTH);

                if(!existingWindowIdSet.contains(shortUuid))
                {
                    uuid = shortUuid;
                    break;
                }
                startIndex++;
            }
        }

        return uuid;
    }

    //to avoid inconsistent behavior in case of re-activated but expired windows in combination with browser refreshes
    //-> get and recycle old id to avoid a redirect
    private String resolveExpiredWindowContextId()
    {
        WindowContextIdHolderComponent windowContextIdHolderComponent =
                getWindowContextIdHolderComponent(FacesContext.getCurrentInstance());

        if(windowContextIdHolderComponent == null)
        {
            return null;
        }

        return windowContextIdHolderComponent.getWindowContextId();
    }

    public String restoreWindowId(ExternalContext externalContext)
    {
        if(!this.useWindowAwareUrlEncoding)
        {
            return null;
        }

        return externalContext.getRequestParameterMap().get(WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }


    //don't use {@link RequestCache} here directly - due to the redirect it was cleared
    protected String getCurrentWindowId()
    {
        return RequestCache.getWindowContextManager().getCurrentWindowContext().getId();
    }

    private String addWindowIdIfNecessary(String url, String windowId)
    {
        if(url.contains(WINDOW_ID_PARAMETER_KEY))
        {
            return url;
        }
        
        StringBuilder newUrl = new StringBuilder(url);

        if(url.contains("?"))
        {
            newUrl.append("&");
        }
        else
        {
            newUrl.append("?");
        }

        newUrl.append(WINDOW_CONTEXT_ID_PARAMETER_KEY);
        newUrl.append("=");
        newUrl.append(windowId);
        return newUrl.toString();
    }
}
