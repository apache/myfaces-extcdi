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
package org.apache.myfaces.extensions.cdi.example.jsf20.view;

import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Model
public class ErrorPageBean
{
    @Inject
    private @Jsf MessageContext messageContext;

    @PostConstruct
    protected void init()
    {
        Object e = FacesContext.getCurrentInstance().getExternalContext().getFlash()
                .get(ViewExpiredException.class.getName());

        if(e instanceof ViewExpiredException)
        {
            this.messageContext.message()
                    .text(((ViewExpiredException)e).getMessage())
                    .payload(MessageSeverity.ERROR)
                    .add();
        }
    }

    /**
     * @return title of the error page
     */
    //just as demo
    public String getPageTitle()
    {
        return this.messageContext.message().text("{errorPageTitle}").toText();
    }
}
