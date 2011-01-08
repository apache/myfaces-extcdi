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
import org.apache.myfaces.extensions.cdi.message.api.MessageWithSeverity;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Gerhard Petracek
 */
public class MessageSeverityTest extends AbstractMessageContextAwareTest
{
    @Test
    public void createMessageWithDefaultSeverityTest()
    {
        Message message = this.messageContext.message().text("hello open message").create();

        assertNotNull(message);
        assertTrue(message.getPayload().containsKey(MessageSeverity.class));
        assertTrue(message.getPayload().containsValue(MessageSeverity.INFO));

        assertTrue(message instanceof MessageWithSeverity);
        assertTrue(MessageSeverity.INFO.equals(((MessageWithSeverity) message).getSeverity()));
    }

    @Test
    public void createMessageWithSeverityWarnTest()
    {
        Message message = this.messageContext.message().text("hello open message").payload(MessageSeverity.WARN).create();

        assertNotNull(message);
        assertTrue(message.getPayload().containsKey(MessageSeverity.class));
        assertTrue(message.getPayload().containsValue(MessageSeverity.WARN));

        assertTrue(message instanceof MessageWithSeverity);
        assertTrue(MessageSeverity.WARN.equals(((MessageWithSeverity) message).getSeverity()));
    }
}