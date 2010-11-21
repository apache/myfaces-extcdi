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

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.DefaultWindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.BookmarkAwareWindowHandler;
import org.apache.myfaces.extensions.cdi.jsf2.impl.windowhandler.Jsf2WindowHandlerServlet;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContextConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * WindowHandler with JSF2 features
 */
@Alternative
@ApplicationScoped
public class ClientAwareWindowHandler extends DefaultWindowHandler implements BookmarkAwareWindowHandler
{
    private static final long serialVersionUID = 5293942986187078113L;

    protected ClientAwareWindowHandler()
    {
        // default ct is needed for proxying 
    }

    @Inject
    protected ClientAwareWindowHandler(WindowContextConfig config)
    {
        super(config);
    }

    @Override
    public void sendRedirect(ExternalContext externalContext, String url, boolean addRequestParameter)
            throws IOException
    {
        PartialViewContext partialViewContext = FacesContext.getCurrentInstance().getPartialViewContext();

        if (partialViewContext != null && partialViewContext.isPartialRequest())
        {
            super.sendRedirect(externalContext, url, addRequestParameter);
            return;
        }

        if (url != null && url.startsWith(Jsf2WindowHandlerServlet.WINDOWHANDLER_URL))
        {
            externalContext.redirect(url);
            return;
        }

        super.sendRedirect(externalContext, url, addRequestParameter);
    }

    public String encodeBookmarkableURL(ExternalContext externalContext, String url, Map<String, List<String>> params)
    {
        Map<String, List<String>> newparms = new HashMap<String, List<String>>();
        List<String> urlParam= new ArrayList<String>();
        urlParam.add(externalContext.encodeBookmarkableURL(url, params));
        newparms.put(Jsf2WindowHandlerServlet.URL_PARAM, urlParam);
        return externalContext.encodeBookmarkableURL(getWindowHandlerPath(externalContext), newparms);
    }

    private String getWindowHandlerPath(ExternalContext externalContext)
    {
        String contextPath = externalContext.getRequestContextPath();

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
