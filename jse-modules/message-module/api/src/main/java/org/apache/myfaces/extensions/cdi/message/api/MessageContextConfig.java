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
package org.apache.myfaces.extensions.cdi.message.api;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public interface MessageContextConfig extends Serializable
{
    /**
     * create a new context based on the default context - the default context won't get modified
     *
     * @return a message context builder based on the current config
     */
    MessageContextBuilder use();

    /**
     * change the default context
     *
     * @return a message context builder to change the current config
     */
    MessageContextBuilder change();

    /**
     * @return the current message interpolator
     */
    MessageInterpolator getMessageInterpolator();

    /**
     * @return the current message resolver
     */
    MessageResolver getMessageResolver();

    /**
     * @return the current locale resolver
     */
    LocaleResolver getLocaleResolver();

    /**
     * @return the current message handler
     */
    MessageHandler getMessageHandler();

    /**
     * @return the current formatter factory
     */
    FormatterFactory getFormatterFactory();

    interface MessageContextBuilder
    {
        /**
         * @param messageInterpolator a new message interpolator
         * @return the instance of the current message context builder
         */
        MessageContextBuilder messageInterpolator(MessageInterpolator messageInterpolator);

        /**
         * @param messageResolver a new message resolver
         * @return the instance of the current message context builder
         */
        MessageContextBuilder messageResolver(MessageResolver messageResolver);

        /**
         * @param formatter an additional argument formatter
         * @return the instance of the current message context builder
         */
        MessageContextBuilder addFormatter(Formatter formatter);

        /**
         * @param type the type the config belongs to
         * @param formatterConfig config for the formatter of the given type
         * @return the instance of the current message context builder
         */
        MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig formatterConfig);

        /**
         * @param type the type the config belongs to
         * @param formatterConfig config for the formatter of the given type
         * @param locale the locale the config belongs to
         * @return the instance of the current message context builder
         */
        MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig formatterConfig, Locale locale);

        /**
         * @param formatterFactory a new argument formatter factory
         * @return the instance of the current message context builder
         */
        MessageContextBuilder formatterFactory(FormatterFactory formatterFactory);

        /**
         * @param messageHandler an additional message handler
         * @return the instance of the current message context builder
         */
        MessageContextBuilder addMessageHandler(MessageHandler messageHandler);

        /**
         * @param localeResolver a new locale resolver
         * @return the instance of the current message context builder
         */
        MessageContextBuilder localeResolver(LocaleResolver localeResolver);

        /**
         * resets the current builder to the initial state
         * @return the instance of the current message context builder
         */
        MessageContextBuilder reset();

        /**
         * @deprecated
         * @return the instance of the current message context builder
         */
        @Deprecated //currently not implemented
        MessageContextBuilder clear();

        /**
         * @return a new message context based on the current config
         */
        MessageContext create();
    }
}
