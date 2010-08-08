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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationFactory;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationConfiguration;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation
        .JsfAwareConversationFactory.ConversationPropertyKeys.TIMEOUT;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.RequestCache;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils;

import javax.enterprise.inject.Typed;
import java.util.Date;
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
@Typed()
public class JsfWindowContext implements EditableWindowContext
{
    private static final long serialVersionUID = 5272798129165017829L;

    private final String id;

    private final WindowContextConfig config;

    private final boolean projectStageDevelopment;

    private Map<ConversationKey, Conversation> groupedConversations
            = new ConcurrentHashMap<ConversationKey, Conversation>();

    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private final TimeoutExpirationEvaluator expirationEvaluator;

    protected JsfWindowContext(String windowContextId, WindowContextConfig config, boolean projectStageDevelopment)
    {
        this.id = windowContextId;
        this.config = config;
        this.expirationEvaluator = new TimeoutExpirationEvaluator(this.config.getWindowContextTimeoutInMinutes());

        this.projectStageDevelopment = projectStageDevelopment;
    }

    public String getId()
    {
        return this.id;
    }

    public void endConversations()
    {
        endConversations(false);
    }

    public void end()
    {
        endConversations(true);
        this.attributes.clear();
    }

    public synchronized void endConversations(boolean forceEnd)
    {
        for (Map.Entry<ConversationKey, Conversation> conversationEntry : this.groupedConversations.entrySet())
        {
            endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue(), forceEnd);
        }
        JsfUtils.resetConversationCache();
    }

    public Conversation getConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        ConversationKey conversationKey =
                new DefaultConversationKey(conversationGroupKey, this.projectStageDevelopment, qualifiers);

        Conversation conversation = RequestCache.getConversation(conversationKey);

        if(conversation == null)
        {
            conversation = this.groupedConversations.get(conversationKey);

            //TODO
            if (conversation != null && !((EditableConversation)conversation).isActive())
            {
                endAndRemoveConversation(conversationKey, conversation, true);
                conversation = null;
            }

            if (conversation == null)
            {
                conversation = createConversation(conversationGroupKey, qualifiers);
                this.groupedConversations.put(conversationKey, conversation);
            }

            RequestCache.setConversation(conversationKey, conversation);
        }
        return conversation;
    }

    public Conversation endConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        ConversationKey conversationKey =
                new DefaultConversationKey(conversationGroupKey, this.projectStageDevelopment, qualifiers);

        Conversation conversation = this.groupedConversations.get(conversationKey);
        return endAndRemoveConversation(conversationKey, conversation, true);
    }

    public Set<Conversation> endConversationGroup(Class conversationGroupKey)
    {
        Set<Conversation> removedConversations = new HashSet<Conversation>();
        for(Map.Entry<ConversationKey, Conversation> conversationEntry : this.groupedConversations.entrySet())
        {
            if(conversationGroupKey.isAssignableFrom(conversationEntry.getKey().getConversationGroup()))
            {
                removedConversations.add(
                        endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue(), true));
            }
        }
        return removedConversations;
    }

    private Conversation endAndRemoveConversation(ConversationKey conversationKey,
                                                  Conversation conversation,
                                                  boolean forceEnd)
    {
        if (forceEnd)
        {
            conversation.end();
            return this.groupedConversations.remove(conversationKey);
        }
        else if(conversation instanceof EditableConversation)
        {
            ((EditableConversation)conversation).deactivate();

            if(!((EditableConversation)conversation).isActive())
            {
                conversation.end();
                return this.groupedConversations.remove(conversationKey);
            }
        }

        return null;
    }

    public Conversation createConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        ConversationKey conversationKey =
                new DefaultConversationKey(conversationGroupKey, this.projectStageDevelopment, qualifiers);

        ConversationFactory conversationFactory =  ((JsfAwareWindowContextConfig)this.config).getConversationFactory();

        return conversationFactory.createConversation(conversationKey, transformConfiguration(this.config));
    }

    private ConversationConfiguration transformConfiguration(final WindowContextConfig config)
    {
        return new ConversationConfiguration()
        {
            public <T> T getValue(String key, Class<T> targetType)
            {
                if(TIMEOUT.getKey().equals(key))
                {
                    return (T)(Integer)config.getConversationTimeoutInMinutes();
                }
                throw new IllegalArgumentException(key + " isn't a supported key");
            }
        };
    }

    public Map<ConversationKey /*conversation group*/, Conversation> getConversations()
    {
        return Collections.unmodifiableMap(this.groupedConversations);
    }

    public WindowContextConfig getConfig()
    {
        return this.config;
    }

    public boolean isActive()
    {
        return !this.expirationEvaluator.isExpired();
    }

    public Date getLastAccess()
    {
        return this.expirationEvaluator.getLastAccess();
    }

    public void touch()
    {
        this.expirationEvaluator.touch();
    }

    public void removeInactiveConversations()
    {
        Iterator<Conversation> conversations = this.groupedConversations.values().iterator();

        Conversation conversation;
        while (conversations.hasNext())
        {
            conversation = conversations.next();

            //TODO
            if (!((EditableConversation)conversation).getActiveState())
            {
                conversations.remove();
            }
        }
    }

    public boolean setAttribute(String name, Object value)
    {
        return setAttribute(name, value, true);
    }

    public boolean setAttribute(String name, Object value, boolean forceOverride)
    {
        if(value == null || (!forceOverride && containsAttribute(name)))
        {
            return false;
        }
        this.attributes.put(name, value);
        return true;
    }

    public boolean containsAttribute(String name)
    {
        return this.attributes.containsKey(name);
    }

    public <T> T getAttribute(String name, Class<T> targetType)
    {
        //noinspection unchecked
        return (T)this.attributes.get(name);
    }
}
