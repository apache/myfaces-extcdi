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
import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfig;
import org.apache.myfaces.extensions.cdi.message.api.LocaleResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.MessageHandler;
import org.apache.myfaces.extensions.cdi.message.api.CompositeMessageHandler;
import org.apache.myfaces.extensions.cdi.message.api.FormatterFactory;
import org.apache.myfaces.extensions.cdi.message.api.Formatter;
import org.apache.myfaces.extensions.cdi.message.api.GenericConfig;
import org.apache.myfaces.extensions.cdi.message.impl.formatter.FormatterBuilder;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
class DefaultMessageContextConfig implements MessageContextConfig
{
    private static final long serialVersionUID = 2919944628020782545L;

    private MessageInterpolator messageInterpolator;
    private MessageResolver messageResolver;
    private Set<MessageHandler> messageHandlers;
    private LocaleResolver localeResolver;
    private FormatterFactory formatterFactory;

    DefaultMessageContextConfig()
    {
        resetMessageContextConfig();
        addNewFormatter(FormatterBuilder.createFormatter(Number.class));
    }

    private DefaultMessageContextConfig(MessageContextConfig messageContextConfigTemplate)
    {
        this();
        this.messageInterpolator = messageContextConfigTemplate.getMessageInterpolator();
        this.messageResolver = messageContextConfigTemplate.getMessageResolver();

        MessageHandler newMessageHandler = messageContextConfigTemplate.getMessageHandler();

        if (newMessageHandler instanceof CompositeMessageHandler)
        {
            this.messageHandlers.addAll(((CompositeMessageHandler) newMessageHandler).getMessageHandlers());
        }
        else
        {
            this.messageHandlers.add(newMessageHandler);
        }
        this.localeResolver = messageContextConfigTemplate.getLocaleResolver();
        this.formatterFactory = messageContextConfigTemplate.getFormatterFactory();
    }

    public MessageContextBuilder use()
    {
        return new MessageContextBuilder()
        {
            private DefaultMessageContextConfig newMessageContextConfig =
                    new DefaultMessageContextConfig(DefaultMessageContextConfig.this);

            public MessageContextBuilder messageInterpolator(MessageInterpolator messageInterpolator)
            {
                newMessageContextConfig.setMessageInterpolator(messageInterpolator);
                return this;
            }

            public MessageContextBuilder messageResolver(MessageResolver messageResolver)
            {
                newMessageContextConfig.setMessageResolver(messageResolver);
                return this;
            }

            public MessageContextBuilder addFormatter(Formatter formatter)
            {
                newMessageContextConfig.addNewFormatter(formatter);
                return this;
            }

            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config)
            {
                newMessageContextConfig.addNewFormatterConfig(type, config);
                return this;
            }

            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config, Locale locale)
            {
                addNewFormatterConfig(type, config.addProperty(Locale.class.toString(), locale));
                return this;
            }

            public MessageContextBuilder formatterFactory(FormatterFactory formatterFactory)
            {
                newMessageContextConfig.setFormatterFactory(formatterFactory);
                return this;
            }

            public MessageContextBuilder addMessageHandler(MessageHandler messageHandler)
            {
                newMessageContextConfig.addNewMessageHandler(messageHandler);
                return this;
            }

            public MessageContextBuilder localeResolver(LocaleResolver localeResolver)
            {
                newMessageContextConfig.setLocaleResolver(localeResolver);
                return this;
            }

            public MessageContextBuilder reset()
            {
                newMessageContextConfig.resetMessageContextConfig();
                return this;
            }

            public MessageContextBuilder clear()
            {
                newMessageContextConfig.clearMessageContextConfig();
                return this;
            }

            public MessageContext create()
            {
                return new DefaultMessageContext(this.newMessageContextConfig);
            }
        };
    }

    public MessageContextBuilder change()
    {
        return new MessageContextBuilder()
        {

            public MessageContextBuilder messageInterpolator(MessageInterpolator messageInterpolator)
            {
                setMessageInterpolator(messageInterpolator);
                return this;
            }

            public MessageContextBuilder messageResolver(MessageResolver messageResolver)
            {
                setMessageResolver(messageResolver);
                return this;
            }

            public MessageContextBuilder addFormatter(Formatter formatter)
            {
                addNewFormatter(formatter);
                return this;
            }

            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config)
            {
                addNewFormatterConfig(type, config);
                return this;
            }

            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config, Locale locale)
            {
                addNewFormatterConfig(type, config.addProperty(Locale.class.toString(), locale));
                return this;
            }

            public MessageContextBuilder formatterFactory(FormatterFactory formatterFactory)
            {
                setFormatterFactory(formatterFactory);
                return this;
            }

            public MessageContextBuilder addMessageHandler(MessageHandler messageHandler)
            {
                addNewMessageHandler(messageHandler);
                return this;
            }

            public MessageContextBuilder localeResolver(LocaleResolver localeResolver)
            {
                setLocaleResolver(localeResolver);
                return this;
            }

            public MessageContextBuilder reset()
            {
                resetMessageContextConfig();
                return this;
            }

            public MessageContextBuilder clear()
            {
                clearMessageContextConfig();
                return this;
            }

            public MessageContext create()
            {
                return new DefaultMessageContext(DefaultMessageContextConfig.this);
            }
        };
    }

    private void resetMessageContextConfig()
    {
        this.messageInterpolator = new DefaultMessageInterpolator();
        this.messageResolver = null;
        this.messageHandlers = new HashSet<MessageHandler>();
        this.localeResolver = new org.apache.myfaces.extensions.cdi.message.impl.DefaultLocaleResolver();
        this.formatterFactory = new DefaultFormatterFactory();
    }

    private void clearMessageContextConfig()
    {
    }

    public MessageInterpolator getMessageInterpolator()
    {
        return this.messageInterpolator;
    }

    public MessageResolver getMessageResolver()
    {
        return this.messageResolver;
    }

    public LocaleResolver getLocaleResolver()
    {
        return this.localeResolver;
    }

    public MessageHandler getMessageHandler()
    {
        return new DefaultCompositeMessageHandler(this.messageHandlers);
    }

    public FormatterFactory getFormatterFactory()
    {
        return this.formatterFactory;
    }

    private void setMessageInterpolator(MessageInterpolator messageInterpolator)
    {
        this.messageInterpolator = messageInterpolator;
    }

    private void setMessageResolver(MessageResolver messageResolver)
    {
        this.messageResolver = messageResolver;
    }

    private void addNewFormatter(Formatter formatter)
    {
        this.formatterFactory.add(formatter);
    }

    private void addNewFormatterConfig(Class<?> type, GenericConfig config)
    {
        this.formatterFactory.addFormatterConfig(type, config);
    }

    private void addNewMessageHandler(MessageHandler messageHandler)
    {
        this.messageHandlers.add(messageHandler);
    }

    private void setLocaleResolver(LocaleResolver localeResolver)
    {
        this.localeResolver = localeResolver;
    }

    private void setFormatterFactory(FormatterFactory formatterFactory)
    {
        this.formatterFactory = formatterFactory;
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
        if (!(o instanceof DefaultMessageContextConfig))
        {
            return false;
        }

        DefaultMessageContextConfig that = (DefaultMessageContextConfig) o;

        if (!formatterFactory.equals(that.formatterFactory))
        {
            return false;
        }
        if (!localeResolver.equals(that.localeResolver))
        {
            return false;
        }
        if (!messageHandlers.equals(that.messageHandlers))
        {
            return false;
        }
        if (messageInterpolator != null
                ? !messageInterpolator.equals(that.messageInterpolator) : that.messageInterpolator != null)
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if (messageResolver != null ? !messageResolver.equals(that.messageResolver) : that.messageResolver != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = messageInterpolator != null ? messageInterpolator.hashCode() : 0;
        result = 31 * result + (messageResolver != null ? messageResolver.hashCode() : 0);
        result = 31 * result + messageHandlers.hashCode();
        result = 31 * result + localeResolver.hashCode();
        result = 31 * result + formatterFactory.hashCode();
        return result;
    }
}
