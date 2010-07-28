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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_ID_PARAMETER_KEY;

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.RequestCache;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * @author Gerhard Petracek
 */
public class DefaultWindowHandler implements WindowHandler
{
    private static final long serialVersionUID = -103516988654873089L;
    private static final int DEFAULT_WINDOW_KEY_LENGTH = 3;

    private final String windowIdParameter = WINDOW_CONTEXT_ID_PARAMETER_KEY + "=";

    protected final boolean useWindowAwareUrlEncoding;

    protected DefaultWindowHandler(boolean useWindowAwareUrlEncoding)
    {
        this.useWindowAwareUrlEncoding = useWindowAwareUrlEncoding;
    }

    public String encodeURL(ExternalContext externalContext, String url)
    {
        if(this.useWindowAwareUrlEncoding)
        {
            return encodeActionURL(url, getCurrentWindowId());
        }
        return url;
    }

    public void sendRedirect(ExternalContext externalContext, String url) throws IOException
    {
        url = externalContext.encodeActionURL(encodeURL(externalContext, url));

        externalContext.redirect(url);
    }

    //TODO add a counter in case of project stage dev
    public String createWindowId()
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

        synchronized (externalContext.getSessionMap())
        {
            Set<String> existingWindowIdSet = ConversationUtils.getExistingWindowIdSet(externalContext);

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
            existingWindowIdSet.add(uuid);
        }

        return uuid;
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

    private String encodeActionURL(String url, String windowId)
    {
        if(url.contains(this.windowIdParameter))
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
