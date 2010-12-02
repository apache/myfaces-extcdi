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

import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToLoadClassForName;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_ID_PARAMETER_KEY;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.DefaultWindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils;

import javax.enterprise.inject.Alternative;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;

/**
 * use this implementation to avoid tokens in the url.
 * attention: e.g. manual window refreshes aren't supported due to browser restrictions
 *
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
@Alternative
public class ServerSideWindowHandler extends DefaultWindowHandler
{
    private static final long serialVersionUID = 4040116087475343221L;

    //workaround for mojarra
    private final boolean useFallback;

    @Inject
    protected ServerSideWindowHandler(WindowContextConfig config)
    {
        super(config);
        this.useFallback = tryToLoadClassForName("org.apache.myfaces.context.FacesContextFactoryImpl") == null;
    }

    @Override
    public void sendRedirect(ExternalContext externalContext, String url, boolean addRequestParameter)
            throws IOException
    {
        if(FacesContext.getCurrentInstance().getResponseComplete())
        {
            return;
        }

        String windowId = getCurrentWindowId();
        if(this.useWindowAwareUrlEncoding || this.useFallback ||
                //here we have an ajax nav. - currently it doesn't work in combination with the flash scope
                FacesContext.getCurrentInstance().getPartialViewContext().isPartialRequest())
        {
            super.sendRedirect(externalContext, url, addRequestParameter);
            return;
        }
        
        if (windowId != null)
        {
            externalContext.getRequestMap().put(WINDOW_CONTEXT_ID_PARAMETER_KEY, windowId);
            externalContext.getFlash().keep(WINDOW_CONTEXT_ID_PARAMETER_KEY);
        }

        if(addRequestParameter)
        {
            url = JsfUtils.addRequestParameter(externalContext, url);
        }

        externalContext.redirect(url);
    }

    @Override
    public String restoreWindowId(ExternalContext externalContext)
    {
        if(this.useWindowAwareUrlEncoding || this.useFallback)
        {
            return super.restoreWindowId(externalContext);
        }

        return (String)externalContext.getFlash().remove(WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }
}
