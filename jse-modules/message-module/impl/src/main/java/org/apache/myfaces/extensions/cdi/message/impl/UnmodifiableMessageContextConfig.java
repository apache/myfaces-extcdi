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

import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfig;
import org.apache.myfaces.extensions.cdi.message.api.LocaleResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.MessageHandler;
import org.apache.myfaces.extensions.cdi.message.api.FormatterFactory;

/**
 * generated
 *
 * @author Gerhard Petracek
 */
class UnmodifiableMessageContextConfig implements MessageContextConfig
{
    private static final long serialVersionUID = -3167585556698594193L;
    private MessageContextConfig messageContextConfig;

    UnmodifiableMessageContextConfig(MessageContextConfig messageContextConfig)
    {
        this.messageContextConfig = messageContextConfig;
    }

    /**
     * {@inheritDoc}
     */
    public MessageContextBuilder use()
    {
        //it's ok to delegate - the call of #use creates a new instance of the context - the old context is untouched
        return this.messageContextConfig.use();
    }


    /**
     * {@inheritDoc}
     */
    public MessageContextBuilder change()
    {
        throw new IllegalStateException(MessageContextConfig.class.getName() +
                "is readonly after the call of MessageContext#message");
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public MessageInterpolator getMessageInterpolator()
    {
        return messageContextConfig.getMessageInterpolator();
    }

    /**
     * {@inheritDoc}
     */
    public MessageResolver getMessageResolver()
    {
        return messageContextConfig.getMessageResolver();
    }

    /**
     * {@inheritDoc}
     */
    public LocaleResolver getLocaleResolver()
    {
        return messageContextConfig.getLocaleResolver();
    }

    /**
     * {@inheritDoc}
     */
    public MessageHandler getMessageHandler()
    {
        return messageContextConfig.getMessageHandler();
    }

    /**
     * {@inheritDoc}
     */
    public FormatterFactory getFormatterFactory()
    {
        return messageContextConfig.getFormatterFactory();
    }
}