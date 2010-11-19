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
import org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation.spi.Jsf2ModuleConfig;
import org.apache.myfaces.extensions.cdi.jsf2.impl.windowhandler.Jsf2WindowHandlerServlet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.inject.Inject;
import java.io.IOException;

/**
 * WindowHandler with JSF2 features
 */
@ApplicationScoped
@Specializes
public class Jsf2WindowHandler extends DefaultWindowHandler
{
    private static final long serialVersionUID = 5293942986187078113L;

    private boolean isClientSideWindowHandler;

    protected Jsf2WindowHandler()
    {
        // default ct is needed for proxying 
    }

    @Inject
    protected Jsf2WindowHandler(Jsf2ModuleConfig config)
    {
        super(config);
        this.isClientSideWindowHandler = config.isClientSideWindowHandlerEnabled();
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
}
