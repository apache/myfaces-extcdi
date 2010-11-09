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

import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageFilter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Gerhard Petracek
 */
public class MessageFilterTest extends AbstractTest
{
    @Test
    public void messageFilterTest()
    {
        this.messageContext.config().change().addMessageHandler(new TestInMemoryMessageHandler()).create()
                .message().text("hello open message").add();

        assertEquals(1, this.messageContext.config().getMessageHandler().getMessages().size());

        this.messageContext.removeAllMessages();

        assertEquals(0, this.messageContext.config().getMessageHandler().getMessages().size());

        //TODO
        this.messageContext.config().getMessageHandler().addMessageFilter(createBlockingMessageFilter());
        this.messageContext.message().text("hello open message").add();

        assertEquals(0, this.messageContext.config().getMessageHandler().getMessages().size());
    }

    private MessageFilter createBlockingMessageFilter()
    {
        return new MessageFilter()
        {
            public boolean processMessage(MessageContext messageContext, Message message)
            {
                return false;
            }
        };
    }
}