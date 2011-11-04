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
package org.apache.myfaces.extensions.cdi.message.test;

import org.apache.myfaces.extensions.cdi.message.api.AbstractMessageHandler;
import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@inheritDoc}
 */
class TestInMemoryMessageHandler extends AbstractMessageHandler
{
    private List<Message> messages = new ArrayList<Message>();

    protected void processMessage(MessageContext messageContext, Message message)
    {
        this.messages.add(message);
    }

    public void removeMessage(Message message)
    {
        this.messages.remove(message);
    }

    public void removeAllMessages()
    {
        this.messages.clear();
    }

    public List<Message> getMessages()
    {
        return Collections.unmodifiableList(this.messages);
    }
}
