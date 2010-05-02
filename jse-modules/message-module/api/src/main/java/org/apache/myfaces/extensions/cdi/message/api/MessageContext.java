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
     * @param message a message which should be added to the current context (message handlers)
     */
    void addMessage(Message message);
}
