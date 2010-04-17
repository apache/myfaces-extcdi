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

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
class UnmodifiableMessageContext implements MessageContext
{
    private static final long serialVersionUID = -4730350864157813259L;
    private MessageContext messageContext;

    UnmodifiableMessageContext(MessageContext messageContext)
    {
        this.messageContext = messageContext;
    }

    public MessageContextConfig config()
    {
        return new UnmodifiableMessageContextConfig(messageContext.config());
    }

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

    public MessageBuilder message()
    {
        return messageContext.message();
    }

    public MessageContext cloneContext()
    {
        return messageContext.cloneContext();
    }

    public String getMessageText(Message message)
    {
        return messageContext.getMessageText(message);
    }

    public void addMessage(Message message)
    {
        messageContext.addMessage(messageContext, message);
    }

    public Locale getLocale()
    {
        return messageContext.getLocale();
    }

    public void addMessage(MessageContext messageContext, Message message)
    {
        this.messageContext.addMessage(messageContext, message);
    }

    public void addMessageFilter(MessageFilter... messageFilters)
    {
        messageContext.addMessageFilter(messageFilters);
    }

    public Set<MessageFilter> getMessageFilters()
    {
        return messageContext.getMessageFilters();
    }

    public void removeMessage(Message message)
    {
        messageContext.removeMessage(message);
    }

    public void removeAllMessages()
    {
        messageContext.removeAllMessages();
    }

    public List<Message> getMessages()
    {
        return messageContext.getMessages();
    }
}
