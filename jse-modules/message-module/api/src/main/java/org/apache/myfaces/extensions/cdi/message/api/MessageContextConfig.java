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
     * create a new context based on the default context - default context won't get modified
     *
     * @return
     */
    MessageContextBuilder use();

    /**
     * change the default context
     *
     * @return
     */
    MessageContextBuilder change();

    MessageInterpolator getMessageInterpolator();

    MessageResolver getMessageResolver();

    LocaleResolver getLocaleResolver();

    //TODO
    MessageHandler getMessageHandler();

    FormatterFactory getFormatterFactory();

    interface MessageContextBuilder
    {
        MessageContextBuilder messageInterpolator(MessageInterpolator messageInterpolator);

        MessageContextBuilder messageResolver(MessageResolver messageResolver);

        MessageContextBuilder addFormatter(Formatter formatter);

        MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config);

        MessageContextBuilder addFormatterConfig(Class<?> type, GenericConfig config, Locale locale);

        MessageContextBuilder formatterFactory(FormatterFactory formatterFactory);

        MessageContextBuilder addMessageHandler(MessageHandler messageHandler);

        MessageContextBuilder localeResolver(LocaleResolver localeResolver);

        MessageContextBuilder reset();

        @Deprecated
        MessageContextBuilder clear();

        MessageContext create();
    }
}
