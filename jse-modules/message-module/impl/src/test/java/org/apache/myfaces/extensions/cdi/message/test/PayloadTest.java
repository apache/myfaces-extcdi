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
import org.apache.myfaces.extensions.cdi.message.api.payload.InternalMessage;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;
import org.apache.myfaces.extensions.cdi.message.impl.DefaultInternalMessage;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests which check message-payload
 */
public class PayloadTest extends AbstractMessageContextAwareTest
{
    @Test
    public void internalMessageAwareMessageHandlerTest()
    {
        this.messageContext.config().change().addMessageHandler(new TestInternalMessageAwareMessageHandler());

        this.messageContext.message().text("test msg 1").add();
        this.messageContext.message().text("test msg 2").payload(InternalMessage.PAYLOAD).add();

        assertEquals(1, this.messageContext.getMessages().size());
        assertEquals(this.messageContext.message().text("test msg 1").create(),
                this.messageContext.getMessages().iterator().next());
    }

    @Test
    public void manuallyCreatedInternalMessageTest()
    {
        this.messageContext.config().change().addMessageHandler(new TestInternalMessageAwareMessageHandler());

        this.messageContext.message().text("test msg 1").add();

        Message internalMessage = new DefaultInternalMessage("test msg 2").setMessageContextConfig(this.messageContext.config());
        this.messageContext.addMessage(internalMessage);

        assertEquals(1, this.messageContext.getMessages().size());
        assertEquals(this.messageContext.message().text("test msg 1").create(),
                this.messageContext.getMessages().iterator().next());
    }

    @Test
    public void forwardedPayloadTest1()
    {
        TestPayloadAwareMessageResolver testResolver = new TestPayloadAwareMessageResolver();
        this.messageContext.config().change().messageResolver(testResolver);

        assertFalse(testResolver.isPayloadAvailable());

        this.messageContext.message().text("test msg").payload(MessageSeverity.WARN).toText();

        assertTrue(testResolver.isPayloadAvailable());
    }

    @Test
    public void forwardedPayloadTest2()
    {
        TestPayloadAwareMessageResolver testResolver = new TestPayloadAwareMessageResolver();
        this.messageContext.config().change().messageResolver(testResolver);

        assertFalse(testResolver.isPayloadAvailable());

        Message message = this.messageContext.message().text("test msg").payload(MessageSeverity.WARN).create();
        message.toString(this.messageContext);

        assertTrue(testResolver.isPayloadAvailable());
    }
}
