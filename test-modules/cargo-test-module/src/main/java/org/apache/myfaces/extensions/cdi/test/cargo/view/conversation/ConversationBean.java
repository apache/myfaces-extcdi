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
package org.apache.myfaces.extensions.cdi.test.cargo.view.conversation;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.test.cargo.view.config.Pages;

import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Conversation scoped page bean
 */
@Named
@ConversationScoped
public class ConversationBean implements Serializable
{
    private static final long serialVersionUID = -3165721387186644462L;

    private String value1;
    private String value2;
    private String value3;

    @Inject
    private Conversation conversation;

    public Class<? extends ViewConfig> step1()
    {
        return Pages.Conversation.Conversation1.class;
    }

    public Class<? extends ViewConfig> step2()
    {
        return Pages.Conversation.Conversation2.class;
    }

    public Class<? extends ViewConfig> step3()
    {
        return Pages.Conversation.Conversation3.class;
    }

    public Class<? extends ViewConfig> finish()
    {
        return Pages.Conversation.Result.class;
    }

    public void closeConversation(ActionEvent e)
    {
        conversation.close();
    }

    public void restartConversation(ActionEvent e)
    {
        conversation.restart();
    }

    public String getValue1()
    {
        return value1;
    }

    public void setValue1(String value1)
    {
        this.value1 = value1;
    }

    public String getValue2()
    {
        return value2;
    }

    public void setValue2(String value2)
    {
        this.value2 = value2;
    }

    public String getValue3()
    {
        return value3;
    }

    public void setValue3(String value3)
    {
        this.value3 = value3;
    }
}
