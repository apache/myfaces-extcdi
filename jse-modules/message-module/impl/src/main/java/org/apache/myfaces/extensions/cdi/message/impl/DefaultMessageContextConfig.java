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

import java.util.Locale;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * {@inheritDoc}
 */
class DefaultMessageContextConfig implements MessageContextConfig
{
    private static final long serialVersionUID = 2919944628020782545L;

    private MessageInterpolator messageInterpolator;
    private MessageResolver messageResolver;
    private CopyOnWriteArraySet<MessageHandler> messageHandlers;
    private LocaleResolver localeResolver;
    private FormatterFactory formatterFactory;

    DefaultMessageContextConfig()
    {
        resetMessageContextConfig();
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

    /**
     * {@inheritDoc}
     */
    public MessageContextBuilder use()
    {
        return new MessageContextBuilder()
        {
            private DefaultMessageContextConfig newMessageContextConfig =
                    new DefaultMessageContextConfig(DefaultMessageContextConfig.this);

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder messageInterpolator(MessageInterpolator messageInterpolator)
            {
                newMessageContextConfig.setMessageInterpolator(messageInterpolator);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder messageResolver(MessageResolver messageResolver)
            {
                newMessageContextConfig.setMessageResolver(messageResolver);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addFormatter(Formatter formatter)
            {
                newMessageContextConfig.addNewFormatter(formatter);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config)
            {
                newMessageContextConfig.addNewFormatterConfig(type, config, Locale.getDefault());
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config, Locale locale)
            {
                addNewFormatterConfig(type, config.addProperty(Locale.class.toString(), locale), locale);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder formatterFactory(FormatterFactory formatterFactory)
            {
                newMessageContextConfig.setFormatterFactory(formatterFactory);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addMessageHandler(MessageHandler messageHandler)
            {
                newMessageContextConfig.addNewMessageHandler(messageHandler);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder localeResolver(LocaleResolver localeResolver)
            {
                newMessageContextConfig.setLocaleResolver(localeResolver);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder reset()
            {
                newMessageContextConfig.resetMessageContextConfig();
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder clear()
            {
                newMessageContextConfig.clearMessageContextConfig();
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContext create()
            {
                return new DefaultMessageContext(this.newMessageContextConfig);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public MessageContextBuilder change()
    {
        return new MessageContextBuilder()
        {
            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder messageInterpolator(MessageInterpolator messageInterpolator)
            {
                setMessageInterpolator(messageInterpolator);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder messageResolver(MessageResolver messageResolver)
            {
                setMessageResolver(messageResolver);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addFormatter(Formatter formatter)
            {
                addNewFormatter(formatter);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config)
            {
                addNewFormatterConfig(type, config, Locale.getDefault());
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config, Locale locale)
            {
                addNewFormatterConfig(type, config.addProperty(Locale.class.toString(), locale), locale);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder formatterFactory(FormatterFactory formatterFactory)
            {
                setFormatterFactory(formatterFactory);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder addMessageHandler(MessageHandler messageHandler)
            {
                addNewMessageHandler(messageHandler);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder localeResolver(LocaleResolver localeResolver)
            {
                setLocaleResolver(localeResolver);
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder reset()
            {
                resetMessageContextConfig();
                return this;
            }

            /**
             * {@inheritDoc}
             */
            public MessageContextBuilder clear()
            {
                clearMessageContextConfig();
                return this;
            }

            /**
             * {@inheritDoc}
             */
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
        this.messageHandlers = new CopyOnWriteArraySet<MessageHandler>();
        this.localeResolver = new DefaultLocaleResolver();
        this.formatterFactory = new DefaultFormatterFactory();
    }

    private void clearMessageContextConfig()
    {
    }

    /**
     * {@inheritDoc}
     */
    public MessageInterpolator getMessageInterpolator()
    {
        return this.messageInterpolator;
    }

    /**
     * {@inheritDoc}
     */
    public MessageResolver getMessageResolver()
    {
        return this.messageResolver;
    }

    /**
     * {@inheritDoc}
     */
    public LocaleResolver getLocaleResolver()
    {
        return this.localeResolver;
    }

    /**
     * {@inheritDoc}
     */
    public MessageHandler getMessageHandler()
    {
        return new DefaultCompositeMessageHandler(this.messageHandlers);
    }

    /**
     * {@inheritDoc}
     */
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

    private void addNewFormatterConfig(Class<?> type, GenericConfig config, Locale locale)
    {
        this.formatterFactory.addFormatterConfig(type, config, locale);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String newLine = System.getProperty("line.separator");

        StringBuilder configInfo = new StringBuilder("MessageContextConfig class: ");
        configInfo.append(getClass().getName());
        configInfo.append(newLine);

        if(this.messageInterpolator != null)
        {
            configInfo.append("   MessageInterpolator class: ").append(this.messageInterpolator.getClass());
        }
        else
        {
            configInfo.append("   no MessageInterpolator");
        }
        configInfo.append(newLine);

        if(this.messageResolver != null)
        {
            configInfo.append("   MessageResolver class: ").append(this.messageResolver.getClass());
        }
        else
        {
            configInfo.append("   no MessageResolver");
        }
        configInfo.append(newLine);

        if(this.messageHandlers != null && !this.messageHandlers.isEmpty())
        {
            for(MessageHandler messageHandler : this.messageHandlers)
            {
                configInfo.append("   MessageHandler class: ").append(messageHandler.getClass());
            }
        }
        else
        {
            configInfo.append("   no MessageHandlers");
        }
        configInfo.append(newLine);

        if(this.localeResolver != null)
        {
            configInfo.append("   LocaleResolver class: ").append(this.localeResolver.getClass());
        }
        else
        {
            configInfo.append("   no LocaleResolver");
        }
        configInfo.append(newLine);

        if(this.formatterFactory != null)
        {
            configInfo.append("   FormatterFactory class: ").append(this.formatterFactory.getClass());
            configInfo.append(newLine);
            //TODO
            //configInfo.append("   FormatterFactory details: ").append(this.formatterFactory.toString());
        }
        else
        {
            configInfo.append("   no FormatterFactory");
        }

        return configInfo.toString();
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
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
