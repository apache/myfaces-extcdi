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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContextConfig;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author Gerhard Petracek
 */
public class JsfWindowContext implements WindowContext
{
    private static final long serialVersionUID = 5272798129165017829L;

    private final Long id;

    private final WindowContextConfig config;

    private final Map<Class, Conversation> groupedConversations = new ConcurrentHashMap<Class, Conversation>();

    public JsfWindowContext(Long conversationContextId, WindowContextConfig config)
    {
        this.id = conversationContextId;
        this.config = config;
    }

    public Long getId()
    {
        return this.id;
    }

    public synchronized void endConversations()
    {
        for (Map.Entry<Class, Conversation> conversationEntry : this.groupedConversations.entrySet())
        {
            endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue());
        }
    }

    public Conversation getConversation(Class conversationGroupKey)
    {
        Conversation conversation = this.groupedConversations.get(conversationGroupKey);

        if (conversation != null && !conversation.isActive())
        {
            endAndRemoveConversation(conversationGroupKey, conversation);
            conversation = null;
        }

        if (conversation == null)
        {
            conversation = createConversation(conversationGroupKey);
            this.groupedConversations.put(conversationGroupKey, conversation);
        }
        return conversation;
    }

    public Conversation endConversation(Class conversationGroupKey)
    {
        Conversation conversation = this.groupedConversations.get(conversationGroupKey);
        return endAndRemoveConversation(conversationGroupKey, conversation);
    }

    public Conversation endAndRemoveConversation(Class conversationGroupKey, Conversation conversation)
    {
        if (conversation.isActive())
        {
            conversation.end();
        }

        return this.groupedConversations.remove(conversationGroupKey);
    }

    public Conversation createConversation(Class conversationGroup)
    {
        return new DefaultConversation(conversationGroup, this.config.getConversationTimeoutInMinutes());
    }

    public Map<Class /*conversation group*/, Conversation> getConversations()
    {
        return Collections.unmodifiableMap(this.groupedConversations);
    }

    public WindowContextConfig getConfig()
    {
        return this.config;
    }

    public void cleanup()
    {
        Iterator<Conversation> conversations = this.groupedConversations.values().iterator();

        Conversation conversation;
        while (conversations.hasNext())
        {
            conversation = conversations.next();

            if (!conversation.isActive())
            {
                conversations.remove();
            }
        }
    }

    public boolean setAttribute(String name, Object value)
    {
        throw new IllegalStateException("not implemented");
    }

    public boolean setAttribute(String name, Object value, boolean forceOverride)
    {
        throw new IllegalStateException("not implemented");
    }

    public boolean containsAttribute(String name)
    {
        throw new IllegalStateException("not implemented");
    }

    public <T> T getAttribute(String name, Class<T> targetType)
    {
        throw new IllegalStateException("not implemented");
    }
}
