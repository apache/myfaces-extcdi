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

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfModuleConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf2.impl.windowhandler.Jsf2WindowHandlerServlet;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.getWindowHandler;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.sendRedirect;

/**
 * @author Gerhard Petracek
 */
public class RedirectedConversationAwareExternalContext extends ExternalContextWrapper
{
    private final ExternalContext wrapped;

    private WindowHandler windowHandler;

    private boolean encodeActionURLs;

    private boolean clientSideWindowHandlerUsed = false;

    public RedirectedConversationAwareExternalContext(ExternalContext wrapped)
    {
        this.wrapped = wrapped;
    }

    public ExternalContext getWrapped()
    {
        return this.wrapped;
    }

    @Override
    public void redirect(String url)
            throws IOException
    {
        lazyInit();
        sendRedirect(this.wrapped, url, this.windowHandler);
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


    @Override
    public String encodeBookmarkableURL(String baseUrl, Map<String, List<String>> parameters)
    {
        lazyInit();

        if(!this.clientSideWindowHandlerUsed)
        {
            return super.encodeBookmarkableURL(baseUrl, parameters);
        }
        
        Map<String, List<String>> newparms = new HashMap<String, List<String>>();
        List<String> urlParam= new ArrayList<String>();
        urlParam.add(wrapped.encodeBookmarkableURL(baseUrl, parameters));
        newparms.put(Jsf2WindowHandlerServlet.URL_PARAM, urlParam);
        return wrapped.encodeBookmarkableURL(getWindowHandlerPath(), newparms);
    }

    private synchronized void lazyInit()
    {
        if(this.windowHandler == null)
        {
            this.windowHandler = getWindowHandler();

            if(this.windowHandler instanceof Jsf2WindowHandler)
            {
                this.clientSideWindowHandlerUsed = true;
            }

            this.encodeActionURLs = CodiUtils
                    .getContextualReferenceByClass(JsfModuleConfig.class)
                    .isAddWindowIdToActionUrlsEnabled();
        }
    }

    private String addWindowIdToUrl(String url)
    {
        return this.windowHandler.encodeURL(url);
    }

    private String getWindowHandlerPath()
    {
        String contextPath = getRequestContextPath();

        if (contextPath == null)
        {
            return Jsf2WindowHandlerServlet.WINDOWHANDLER_URL;
        }
        else
        {
            return contextPath + Jsf2WindowHandlerServlet.WINDOWHANDLER_URL;
        }
    }

}
