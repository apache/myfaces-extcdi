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
import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfig;
import org.apache.myfaces.extensions.cdi.message.api.MessageFilter;
import org.apache.myfaces.extensions.cdi.message.api.MessageBuilder;
import org.apache.myfaces.extensions.cdi.message.api.MessageFactory;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * to create a new instance inject the singleton and call cloneContext -
 * so it's possible to use it e.g. in producer methods
 *
 * @author Gerhard Petracek
 */
public class DefaultMessageContext implements MessageContext
{
    private static final long serialVersionUID = -110779217295211303L;

    private MessageContextConfig config = new DefaultMessageContextConfig();
    private MessageFactory messageFactory;

    public DefaultMessageContext()
    {
    }

    public DefaultMessageContext(MessageFactory messageFactory)
    {
        this.messageFactory = messageFactory;
    }

    DefaultMessageContext(MessageContextConfig config)
    {
        this.config = config;
    }

    DefaultMessageContext(MessageContextConfig config, MessageFactory messageFactory)
    {
        this.config = config;
        this.messageFactory = messageFactory;
    }

    public MessageBuilder message()
    {
        return new DefaultMessageBuilder(this, this.messageFactory);
    }

    public MessageContextConfig config()
    {
        return config;
    }

    public <T extends MessageContext> T typed(Class<T> contextType)
    {
        //noinspection unchecked
        return (T) this;
    }

    public MessageContext cloneContext()
    {
        return config().use().create();
    }

    public String getMessageText(Message message)
    {
        return message().toText(message);
    }

    public void addMessage(Message message)
    {
        addMessage(this, message);
    }

    public void addMessage(MessageContext messageContext, Message message)
    {
        config().getMessageHandler().addMessage(messageContext, message);
    }

    public void addMessageFilter(MessageFilter... messageFilters)
    {
        config().getMessageHandler().addMessageFilter(messageFilters);
    }

    public Set<MessageFilter> getMessageFilters()
    {
        return config().getMessageHandler().getMessageFilters();
    }

    public void removeMessage(Message message)
    {
        config().getMessageHandler().removeMessage(message);
    }

    public void removeAllMessages()
    {
        config().getMessageHandler().removeAllMessages();
    }

    public List<Message> getMessages()
    {
        return config().getMessageHandler().getMessages();
    }

    public Locale getLocale()
    {
        return config().getLocaleResolver().getLocale();
    }

    /*
     * generated
     */

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DefaultMessageContext))
        {
            return false;
        }

        DefaultMessageContext that = (DefaultMessageContext) o;

        //noinspection RedundantIfStatement
        if (!config.equals(that.config))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return config.hashCode();
    }
}
