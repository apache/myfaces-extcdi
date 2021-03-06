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
package org.apache.myfaces.extensions.cdi.test.webapp.viewaccess.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * View-access scoped page-bean
 */
@Named
@ViewAccessScoped
public class ViewAccessBean1 implements Serializable
{

    public static final String INPUT_DEFAULT_VALUE = "no input";

    private String _input;

    @Inject
    private WindowContext _windowContext;

    @Inject
    private Conversation _conversation;

    @PostConstruct
    public void init()
    {
        // to ensure invocation of @PostConstruct
        _input = INPUT_DEFAULT_VALUE;
    }

    public String closeConversation()
    {
        _conversation.close();

        return null;
    }

    public String restartConversation()
    {
        _conversation.restart();

        return null;
    }

    public String closeWindowContext()
    {
        _windowContext.close();

        return null;
    }

    public String closeConversationsWindowContext()
    {
        _windowContext.closeConversations();

        return null;
    }

    public String closeViewAccessScopedGroup()
    {
        // this should not work
        _windowContext.closeConversationGroup(ViewAccessScoped.class);

        return null;
    }

    public String getInput()
    {
        return _input;
    }

    public void setInput(String input)
    {
        _input = input;
    }
}
