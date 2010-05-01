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
public interface MessageBuilder extends Serializable
{
    /**
     * @param messagePayload payload for the current message
     * @return the current instance of the message builder to allow a fluent api
     */
    MessageBuilder payload(Class<? extends MessagePayload>... messagePayload);

    /**
     * @param messageDescriptor message key (or inline-text) for the current message
     * @return the current instance of the message builder to allow a fluent api
     */
    MessageBuilder text(String messageDescriptor);

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
