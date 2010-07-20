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
package org.apache.myfaces.extensions.cdi.javaee.jsf2.impl.request;

import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.UUID_ID_KEY;
import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToLoadClassForName;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 */
public class DefaultRedirectHandler extends
        org.apache.myfaces.extensions.cdi.javaee.jsf.impl.request.DefaultRedirectHandler
{
    //workaround for myfaces core2
    private final boolean useFallback;

    public DefaultRedirectHandler()
    {
        this.useFallback = tryToLoadClassForName("org.apache.myfaces.context.FacesContextFactoryImpl") == null;
    }

    @Override
    public void sendRedirect(ExternalContext externalContext, String url, String requestIdKey) throws IOException
    {
        if(this.useFallback ||
                //here we have an ajax nav. - currently it doesn't work in combination with the flash scope
                FacesContext.getCurrentInstance().getPartialViewContext().isPartialRequest())
        {
            super.sendRedirect(externalContext, url, requestIdKey);
            return;
        }
        
        if (requestIdKey != null)
        {
            externalContext.getRequestMap().put(UUID_ID_KEY, requestIdKey);
            externalContext.getFlash().keep(UUID_ID_KEY);
        }

        externalContext.redirect(url);
    }

    @Override
    public String restoreRequestIdKey(ExternalContext externalContext)
    {
        if(this.useFallback)
        {
            return super.restoreRequestIdKey(externalContext);
        }

        return (String)externalContext.getFlash().remove(UUID_ID_KEY);
    }
}
