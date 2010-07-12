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
package org.apache.myfaces.extensions.cdi.core.api.scope.conversation;

import java.io.Serializable;

/**
 * A conversation is started automatically with the first access
 *
 * @author Gerhard Petracek
 */
public interface Conversation extends Serializable
{
    /**
     * Deactivates the conversation and un-scopes all bean instances immediately.<br/>
     * At the next cleanup the whole conversation will be destroyed.
     * (If an inactive {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation}
     * gets resolved before the cleanup, the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext} has to destroy it.
     * -> A new conversation will be created immediately.
     */
    void end();

    /**
     * Un-scopes all bean instances immediately.
     * Instead of destroying the whole conversation the conversation stays active.
     * (The conversation will be marked as used.)<br/>
     * As soon as an instance of a bean is requested,
     * the instance will be created based on the original bean descriptor.
     * This approach allows a better performance, if the conversation is needed immediately.
     */
    void restart();

    /**
     * @param key class of the requested bean
     * @param <T> type of the requested bean
     * @return an instance of the requested bean if the conversation is active - null otherwise
     */
    <T> T getBean(Class<T> key);
}

