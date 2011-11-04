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

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * {@link MessageContext} which doesn't support changes
 */
class UnmodifiableMessageContext implements MessageContext
{
    private static final long serialVersionUID = -4730350864157813259L;
    private MessageContext messageContext;

    UnmodifiableMessageContext(MessageContext messageContext)
    {
        this.messageContext = messageContext;
    }

    /**
     * {@inheritDoc}
     */
    public MessageContextConfig config()
    {
        return new UnmodifiableMessageContextConfig(messageContext.config());
    }

    /**
     * {@inheritDoc}
     */
    public <T extends MessageContext> T typed(Class<T> contextType)
    {
        throw new IllegalStateException(UnmodifiableMessageContext.class.getName() +
                "is readonly after the call of MessageContext#message");

        //alternative:
        /*
        if(this.messageContext instanceof UnmodifiableMessageContext)
        {
            return (T)((UnmodifiableMessageContext)this.messageContext).getWrappedMessageContext();
        }
        return (T)this.messageContext;
        */
    }

    /*
    MessageContext getWrappedMessageContext()
    {
        if(this.messageContext instanceof UnmodifiableMessageContext) {
            return ((UnmodifiableMessageContext)this.messageContext).getWrappedMessageContext();
        }
        return messageContext;
    }
    */

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public MessageBuilder message()
    {
        return messageContext.message();
    }

    /**
     * {@inheritDoc}
     */
    public MessageContext cloneContext()
    {
        return messageContext.cloneContext();
    }

    /**
     * {@inheritDoc}
     */
    public void addMessage(Message message)
    {
        messageContext.addMessage(messageContext, message);
    }

    /**
     * {@inheritDoc}
     */
    public Locale getLocale()
    {
        return messageContext.getLocale();
    }

    /**
     * {@inheritDoc}
     */
    public void addMessage(MessageContext messageContext, Message message)
    {
        this.messageContext.addMessage(messageContext, message);
    }

    /**
     * {@inheritDoc}
     */
    public void addMessageFilter(MessageFilter... messageFilters)
    {
        messageContext.addMessageFilter(messageFilters);
    }

    /**
     * {@inheritDoc}
     */
    public Set<MessageFilter> getMessageFilters()
    {
        return messageContext.getMessageFilters();
    }

    /**
     * {@inheritDoc}
     */
    public void removeMessage(Message message)
    {
        messageContext.removeMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    public void removeAllMessages()
    {
        messageContext.removeAllMessages();
    }

    /**
     * {@inheritDoc}
     */
    public List<Message> getMessages()
    {
        return messageContext.getMessages();
    }
}
