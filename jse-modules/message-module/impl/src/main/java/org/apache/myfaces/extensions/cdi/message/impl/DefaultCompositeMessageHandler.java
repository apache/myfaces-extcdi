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
package org.apache.myfaces.extensions.cdi.message.impl;

import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageHandler;
import org.apache.myfaces.extensions.cdi.message.api.MessageFilter;
import org.apache.myfaces.extensions.cdi.message.api.CompositeMessageHandler;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
class DefaultCompositeMessageHandler implements CompositeMessageHandler, Serializable
{
    private static final long serialVersionUID = 3553885372006874180L;

    private List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();

    DefaultCompositeMessageHandler(Iterable<MessageHandler> messageHandlerIterable)
    {
        for (MessageHandler messageHandler : messageHandlerIterable)
        {
            messageHandlers.add(messageHandler);
        }
    }

    public void addMessage(MessageContext messageContext, Message message)
    {
        for (MessageHandler messageHandler : this.messageHandlers)
        {
            messageHandler.addMessage(messageContext, message);
        }
    }

    public void addMessageFilter(MessageFilter... messageFilters)
    {
        for (MessageHandler messageHandler : this.messageHandlers)
        {
            messageHandler.addMessageFilter(messageFilters);
        }
    }

    public Set<MessageFilter> getMessageFilters()
    {
        Set<MessageFilter> result = new HashSet<MessageFilter>();
        for (MessageHandler messageHandler : this.messageHandlers)
        {
            result.addAll(messageHandler.getMessageFilters());
        }
        return Collections.unmodifiableSet(result);
    }

    public void removeMessage(Message message)
    {
        for (MessageHandler messageHandler : this.messageHandlers)
        {
            messageHandler.removeMessage(message);
        }
    }

    public void removeAllMessages()
    {
        for (MessageHandler messageHandler : this.messageHandlers)
        {
            messageHandler.removeAllMessages();
        }
    }

    public List<Message> getMessages()
    {
        List<Message> result = new ArrayList<Message>();

        for (MessageHandler messageHandler : this.messageHandlers)
        {
            for (Message message : messageHandler.getMessages())
            {
                if (!result.contains(message))
                {
                    result.add(message);
                }
            }
        }

        return result;
    }

    public List<MessageHandler> getMessageHandlers()
    {
        return Collections.unmodifiableList(this.messageHandlers);
    }
}
