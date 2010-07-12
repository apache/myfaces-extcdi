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
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.DeactivationAwareWindowContext;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationKey;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.annotation.Annotation;

/**
 * TODO
 *
 * @author Gerhard Petracek
 */
public class JsfWindowContext implements WindowContext, DeactivationAwareWindowContext
{
    private static final long serialVersionUID = 5272798129165017829L;

    private final Long id;

    private final WindowContextConfig config;

    private final Map<ConversationKey, Conversation> groupedConversations
            = new ConcurrentHashMap<ConversationKey, Conversation>();

    public JsfWindowContext(Long windowContextId, WindowContextConfig config)
    {
        this.id = windowContextId;
        this.config = config;
    }

    public Long getId()
    {
        return this.id;
    }

    public synchronized void endConversations()
    {
        for (Map.Entry<ConversationKey, Conversation> conversationEntry : this.groupedConversations.entrySet())
        {
            endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue());
        }
    }

    public Conversation getConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        ConversationKey conversationKey = new DefaultConversationKey(conversationGroupKey, qualifiers);

        Conversation conversation = this.groupedConversations.get(conversationKey);

        //TODO
        if (conversation != null && !((EditableConversation)conversation).isActive())
        {
            endAndRemoveConversation(conversationKey, conversation);
            conversation = null;
        }

        if (conversation == null)
        {
            conversation = createConversation(conversationGroupKey, qualifiers);
            this.groupedConversations.put(conversationKey, conversation);
        }
        return conversation;
    }

    public Conversation endConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        ConversationKey conversationKey = new DefaultConversationKey(conversationGroupKey, qualifiers);

        Conversation conversation = this.groupedConversations.get(conversationKey);
        return endAndRemoveConversation(conversationKey, conversation);
    }

    public Set<Conversation> endConversationGroup(Class conversationGroupKey)
    {
        Set<Conversation> removedConversations = new HashSet<Conversation>();
        for(Map.Entry<ConversationKey, Conversation> conversationEntry : this.groupedConversations.entrySet())
        {
            if(conversationGroupKey.isAssignableFrom(conversationEntry.getKey().getConversationGroup()))
            {
                removedConversations.add(
                        endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue()));
            }
        }
        return removedConversations;
    }

    private Conversation endAndRemoveConversation(ConversationKey conversationKey, Conversation conversation)
    {
        //TODO
        if (((EditableConversation)conversation).isActive())
        {
            conversation.end();
        }

        return this.groupedConversations.remove(conversationKey);
    }

    public Conversation createConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        ConversationKey conversationKey = new DefaultConversationKey(conversationGroupKey, qualifiers);

        return new DefaultConversation(conversationKey, this.config.getConversationTimeoutInMinutes());
    }

    public Map<ConversationKey /*conversation group*/, Conversation> getConversations()
    {
        return Collections.unmodifiableMap(this.groupedConversations);
    }

    public WindowContextConfig getConfig()
    {
        return this.config;
    }

    public void removeInactiveConversations()
    {
        Iterator<Conversation> conversations = this.groupedConversations.values().iterator();

        Conversation conversation;
        while (conversations.hasNext())
        {
            conversation = conversations.next();

            //TODO
            if (!((EditableConversation)conversation).isActive())
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
