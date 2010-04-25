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

import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;

import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
public interface MessageContext extends LocaleResolver, MessageHandler, Serializable
{
    /**
     * @return message builder to add and/or create a new message based on the current context via a fluent api
     */
    MessageBuilder message();

    /**
     * @return the current config to change it or create a new one base on the current config
     */
    MessageContextConfig config();

    /**
     * @param contextType the type of the custom implementation
     * @return the instance of the current message context to use the api of the concrete implementation
     */
    <T extends MessageContext> T typed(Class<T> contextType);

    /*
    * convenient methods
    */

    /**
     * @return creates a new context based on the current one
     */
    MessageContext cloneContext();

    /**
     * @param message the message which should be converted to the final text
     * @return the final text generated via the current message context
     */
    String getMessageText(Message message);

    /**
     * @param message a message which should be added to the current context (message handlers)
     */
    void addMessage(Message message);

    interface MessageBuilder
    {
        /**
         * @param messagePayload payload for the current message
         * @return the current instance of the message builder to allow a fluent api
         */
        MessageBuilder payload(Class<? extends MessagePayload>... messagePayload);

        /**
         * @param messageTemplate message template (or inline-text) for the current message
         * @return the current instance of the message builder to allow a fluent api
         */
        MessageBuilder text(String messageTemplate);

        /**
         * @param arguments numbered and/or named argument(s) for the current message
         * @return the current instance of the message builder to allow a fluent api
         */
        MessageBuilder argument(Serializable... arguments);

        /**
         * helper method to add named arguments easily
         * @param name the name/key of the named argument
         * @param value the value of the named argument
         * @return the current instance of the message builder to allow a fluent api
         */
        MessageBuilder namedArgument(String name, Serializable value);

        //see MessageContext for add(Message)

        /**
         * adds the message which was built via the fluent api
         * @return the message which was built via the fluent api
         */
        Message add();

        /**
         * @return the message which was built via the fluent api
         */
        Message create();

        /**
         * @return the text of the message which was built via the fluent api
         */
        String toText();

        /**
         * converts a given message to the text via the current context
         * @param message the message which should be converted
         * @return the converted text of the given message
         */
        String toText(Message message);
    }
}
