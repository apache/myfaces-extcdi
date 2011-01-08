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
import org.apache.myfaces.extensions.cdi.message.impl.DefaultMessageContext;
import org.apache.myfaces.extensions.cdi.message.impl.SimpleMessageBuilder;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public class ExtensibilityTest extends AbstractMessageContextAwareTest
{
    @Test
    public void messageHandlerTest()
    {
        this.messageContext.config()
                .change().addMessageHandler(new TestInMemoryMessageHandler())
                .create().message().text("{hello}").add();

        assertEquals(1, this.messageContext.getMessages().size());
        assertEquals(this.messageContext.message().text("{hello}").create(),
                this.messageContext.getMessages().iterator().next());
    }

    @Test
    public void customMessageTypeViaCustomMessageBuilder1Test()
    {
        Message message = TestMessageBuilder.label().text("{hello}").create();

        assertEquals("test label", message.toString(this.messageContext));
    }

    @Test
    public void customMessageTypeViaCustomMessageBuilder2Test()
    {
        Message message = TestMessageBuilder.technicalMessage().text("{hello}").create();

        assertEquals("hello codi", message.toString(this.messageContext));
    }

    @Test
    public void customMessageTypeTest()
    {
        String messageText = this.messageContext.message().text("{hello}").payload(Label.PAYLOAD).toText();

        assertEquals("test label", messageText);
    }

    @Test
    public void newMessageFactoryTestViaMessageContext()
    {
        Message message = DefaultMessageContext.create(new TestMessageFactory()).message().text("{hello}").create();

        assertEquals(TestMessage.class, message.getClass());
    }

    @Test
    public void newMessageFactoryTestViaMessageBuilder()
    {
        Message message = SimpleMessageBuilder.message(new TestMessageFactory()).text("{hello}").create();

        assertEquals(TestMessage.class, message.getClass());
    }

    @Test
    public void newContextTest()
    {
        MessageContext newMessageContext = this.messageContext.cloneContext();

        this.messageContext.config().change().localeResolver(new TestGermanLocaleResolver());
        newMessageContext.config().change().localeResolver(new TestEnglishLocaleResolver());

        if (newMessageContext.equals(this.messageContext))
        {
            fail("different context expected - old context: " + this.messageContext.toString() + " new context: " + newMessageContext.toString());
        }

        assertEquals(Locale.GERMAN, this.messageContext.config().getLocaleResolver().getLocale());
        assertEquals(Locale.ENGLISH, newMessageContext.config().getLocaleResolver().getLocale());
    }

    @Test
    public void usedMessageResolverTest()
    {
        MessageContext newMessageContext = this.messageContext.config().use().localeResolver(new TestGermanLocaleResolver()).create();

        if (newMessageContext.equals(this.messageContext))
        {
            fail("different context expected - old context: " + this.messageContext.toString() + " new context: " + newMessageContext.toString());
        }
    }

    @Test
    public void changedMessageResolverTest()
    {
        MessageContext messageContext = this.messageContext.config().change().localeResolver(new TestGermanLocaleResolver()).create();

        assertEquals(this.messageContext, messageContext);
    }

    @Test
    public void cloneMessageContextTest()
    {
        MessageContext messageContext = this.messageContext.cloneContext();
        messageContext.message().text("{hello}").create();

        this.messageContext.config().change().addMessageHandler(new TestInMemoryMessageHandler());

        assertFalse(messageContext.config().getMessageHandler().equals(this.messageContext.config().getMessageHandler()));
    }

    @Test
    public void customMessageContextTest()
    {
        MessageContext messageContext = new TestCustomMessageContext();

        assertTrue(messageContext.typed(TestCustomMessageContext.class).isReachable());
    }
}
