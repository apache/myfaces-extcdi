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
    MessageBuilder message();

    MessageContextConfig config();

    <T extends MessageContext> T typed(Class<T> contextType);

    /*
    * convenient methods
    */

    MessageContext cloneContext();

    String getMessageText(Message message);

    void addMessage(Message message);

    interface MessageBuilder
    {
        MessageBuilder payload(Class<? extends MessagePayload>... messagePayload);

        MessageBuilder text(String messageTemplate);

        MessageBuilder argument(Serializable... arguments);

        MessageBuilder namedArgument(String name, Serializable value);

        //see MessageContext for add(Message)

        Message add();

        Message create();

        String toText();

        String toText(Message message);
    }
}
