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

import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * an implementation is allowed to store messages directly or to forward messages
 *
 * @author Gerhard Petracek
 */
public interface MessageHandler extends Serializable
{
    /**
     * called to add a message to a special target (known by the implementations)
     *
     * @param messageContext current message context
     * @param message the new message to add
     */
    void addMessage(MessageContext messageContext, Message message);

    /**
     * @param messageFilters message filters which should be added to the current message handler
     */
    void addMessageFilter(MessageFilter... messageFilters);

    /**
     * @return the registered message filter(s)
     */
    Set<MessageFilter> getMessageFilters();

    //TODO move to a separated interface?
    /**
     * removes an added message (if possible)
     * @param message to be removed
     */
    void removeMessage(Message message);

    //TODO move to a separated interface?
    /**
     * removes all added messages (if possible)
     */
    void removeAllMessages();

    //TODO move to a separated interface?
    /**
     * @return all added messages (if they are available)
     */
    List<Message> getMessages();
}
