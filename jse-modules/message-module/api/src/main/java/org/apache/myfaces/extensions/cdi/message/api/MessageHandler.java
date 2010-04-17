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

/**
 * an implementation is allowed to store messages directly or to forward messages
 *
 * @author Gerhard Petracek
 */
public interface MessageHandler
{
    void addMessage(MessageContext messageContext, Message message);

    void addMessageFilter(MessageFilter... messageFilters);

    Set<MessageFilter> getMessageFilters();

    //TODO move to a separated interface?

    void removeMessage(Message message);

    //TODO move to a separated interface?

    void removeAllMessages();

    //TODO move to a separated interface?

    List<Message> getMessages();
}
