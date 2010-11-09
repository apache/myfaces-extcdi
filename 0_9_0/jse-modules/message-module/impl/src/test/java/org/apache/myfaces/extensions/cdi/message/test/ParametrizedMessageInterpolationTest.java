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

import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.CompositeMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.ELAwareMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.NumberedArgumentAwareMessageInterpolator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
public class ParametrizedMessageInterpolationTest extends AbstractTest
{
    @Test
    public void createElAwareMessageTest()
    {
        String messageText = this.messageContext.config().change().messageInterpolator(createELAwareInterpolator()).create()
                .message().text("hello {person.name}").namedArgument("person", new TestPerson()).toText();

        assertEquals("hello Thomas", messageText);
    }

    @Test
    public void createNumberedArgumentAwareMessageTest()
    {
        MessageInterpolator messageInterpolator = new CompositeMessageInterpolator(createELAwareInterpolator(), new NumberedArgumentAwareMessageInterpolator());
        String messageText = this.messageContext.config().change().messageInterpolator(messageInterpolator).create()
                .message().text("\\{0} greets {1}")
                .argument("Gerhard", "Manfred").argument(new TestPerson()).toText();

        assertEquals("Gerhard greets Manfred", messageText);
    }

    @Test
    public void createNamedArgumentAwareMessageTest()
    {
        String messageText = this.messageContext.config().change().messageInterpolator(createELAwareInterpolator()).create()
                .message().text("\\{p1} and {p2} greet {p3.name}")
                .namedArgument("p2", "Gerhard").namedArgument("p1", "Manfred").namedArgument("p3", new TestPerson())
                .toText();

        assertEquals("Manfred and Gerhard greet Thomas", messageText);
    }

    @Test
    public void overrideNamedArgumentTest()
    {
        try
        {
            this.messageContext.config().change().messageInterpolator(createELAwareInterpolator()).create()
                    .message().text("\\{p1} and {p2} greet {p3.name}")
                    .namedArgument("p2", "Gerhard").namedArgument("p2", "Manfred").namedArgument("p3", new TestPerson())
                    .toText();
        }
        catch (UnsupportedOperationException e)
        {
            return;
        }

        fail();
    }

    private ELAwareMessageInterpolator createELAwareInterpolator()
    {
        return new ELAwareMessageInterpolator(new TestELProvider());
    }

    @Test
    public void createMixedArgumentAwareMessageTest()
    {
        MessageInterpolator messageInterpolator = new CompositeMessageInterpolator(createELAwareInterpolator(), new NumberedArgumentAwareMessageInterpolator());
        String messageText = this.messageContext.config().change().messageInterpolator(messageInterpolator).create()
                .message().text("[hello] {0} and {firstName} ({1} and {lastName}) greet {person.name}")
                .argument("Gerhard", "Petracek")
                .namedArgument("person", new TestPerson())
                .namedArgument("lastName", "Geiler").namedArgument("firstName", "Manfred").toText();

        assertEquals("[hello] Gerhard and Manfred (Petracek and Geiler) greet Thomas", messageText);
    }

    private class TestPerson implements Serializable
    {
        private static final long serialVersionUID = 1505396194005924707L;

        public String getName()
        {
            return "Thomas";
        }
    }
}