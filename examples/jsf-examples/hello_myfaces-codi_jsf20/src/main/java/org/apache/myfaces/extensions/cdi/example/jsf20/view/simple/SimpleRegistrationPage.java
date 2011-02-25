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
package org.apache.myfaces.extensions.cdi.example.jsf20.view.simple;

import org.apache.myfaces.extensions.cdi.example.jsf20.domain.User;
import org.apache.myfaces.extensions.cdi.example.jsf20.view.config.Pages;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.CloseConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewAccessScoped
public class SimpleRegistrationPage implements Serializable
{
    private static final long serialVersionUID = -3760946096396420540L;

    @Inject
    @New
    private User user;

    @Inject
    @Jsf
    private MessageContext messageContext;

    private String repeatedPassword;

    public Class<? extends ViewConfig> registerUser()
    {
        this.messageContext.message()
                .text("{msgUserRegistered}")
                .argument(this.user.getLoginName())
                .add();
        
        return Pages.Simple.Summary.class;
    }

    @CloseConversationGroup
    public Class<? extends Pages.Simple> registerUserAndRestart()
    {
        registerUser();
        return null; //stay on the same page
    }

    /*
     * generated
     */
    public User getUser()
    {
        return user;
    }

    public String getRepeatedPassword()
    {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword)
    {
        this.repeatedPassword = repeatedPassword;
    }
}
