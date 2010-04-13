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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.grouped;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationContextConfig;

import javax.enterprise.context.SessionScoped;
import java.util.Map;

/**
 * TODO
 * @author Gerhard Petracek
 */
@SessionScoped
public class JsfConversationContext implements ConversationContext
{
    private static final long serialVersionUID = 5272798129165017829L;

    private Long id;
    private ConversationContextConfig config;

    public JsfConversationContext(Long id, ConversationContextConfig config)
    {
        this.id = id;
        this.config = config;
    }

    public Long getId()
    {
        return this.id;
    }

    public void invalidate()
    {
        //TODO impl. it
        throw new IllegalStateException("not implemented");
    }

    public Conversation getConversation(Class conversationGroup)
    {
        throw new IllegalStateException("not implemented");
    }

    public Conversation createConversation(Class conversationGroup)
    {
        throw new IllegalStateException("not implemented");
    }

    public Map<Class /*conversation group*/, Conversation> getConversations()
    {
        throw new IllegalStateException("not implemented");
    }

    public ConversationContextConfig getConfig()
    {
        return this.config;
    }
}
