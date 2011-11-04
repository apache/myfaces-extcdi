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

import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageBuilder;
import org.apache.myfaces.extensions.cdi.message.api.MessageFactory;
import org.apache.myfaces.extensions.cdi.message.api.Message;

/**
 * just for manual usages (see the test-case)
 */
public class SimpleMessageBuilder extends DefaultMessageBuilder
{
    private static final long serialVersionUID = -3453104710956154815L;

    protected SimpleMessageBuilder()
    {
        super();
        reset();
    }

    protected SimpleMessageBuilder(MessageContext messageContext)
    {
        super(messageContext, null);
    }

    /**
     * Creates a new {@link Message}
     * @return a new message
     */
    public static MessageBuilder message()
    {
        return new SimpleMessageBuilder();
    }

    /**
     * Creates a new {@link MessageBuilder} for the given {@link MessageContext}
     * @param  messageContext current message-context
     * @return a new message
     */
    public static MessageBuilder message(MessageContext messageContext)
    {
        return new SimpleMessageBuilder(messageContext);
    }

    /**
     * Creates a new {@link MessageBuilder} which uses the given {@link MessageFactory}
     * @param  messageFactory current message-factory
     * @return a new message
     */
    public static MessageBuilder message(MessageFactory messageFactory)
    {
        SimpleMessageBuilder messageBuilder = new SimpleMessageBuilder();
        messageBuilder.setMessageFactory(messageFactory);
        return messageBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message add()
    {
        if (getMessageContext() == null)
        {
            throw new UnsupportedOperationException(getClass().getName() + ".add called outside a message context. " +
                    "please use SimpleMessageBuilder.message(messageContext).text(...).add();" +
                    " or messageContext.message().text(...).add();");
        }
        return super.add();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toText()
    {
        if (getMessageContext() == null)
        {
            throw new UnsupportedOperationException(getClass().getName() + ".toText called outside a message context." +
                    " please use SimpleMessageBuilder.message(messageContext).text(...).toText();" +
                    " or messageContext.message().text(...).toText();");
        }
        return super.toText();
    }
}
