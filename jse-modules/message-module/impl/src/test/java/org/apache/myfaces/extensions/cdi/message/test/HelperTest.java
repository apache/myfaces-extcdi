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
import org.apache.myfaces.extensions.cdi.message.impl.DefaultMessage;
import org.apache.myfaces.extensions.cdi.message.impl.ELAwareMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.NamedArguments;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class HelperTest extends AbstractTest
{
    @Test
    public void namedArgumentBuilderTest()
    {
        this.messageContext.config().change().messageInterpolator(new ELAwareMessageInterpolator(new TestELProvider()));

        Number arg1 = new BigDecimal("123.4567");
        Date arg2 = new Date();
        String text = "#1: {arg1} and #2: {arg2}";
        Message message1 = this.messageContext.message().text(text)
                .namedArgument("arg1", arg1).namedArgument("arg2", arg2).create();

        Message message2 = new DefaultMessage(text, NamedArguments.add("arg1", arg1).add("arg2", arg2).create());

        assertEquals(message1, message2);
        assertEquals(message1.toString(), message2.toString());
        assertEquals(message1.toString(this.messageContext), message2.toString(this.messageContext));
    }

    @Test
    public void convertNamedArgumentTest()
    {
        this.messageContext.config().change().messageInterpolator(new ELAwareMessageInterpolator(new TestELProvider()));

        Number arg1 = new BigDecimal("123.4567");
        Date arg2 = new Date();
        String text = "#1: {arg1} and #2: {arg2}";
        Message message1 = this.messageContext.message().text(text)
                .namedArgument("arg1", arg1).namedArgument("arg2", arg2).create();

        Map<String, Serializable> arguments = new HashMap<String, Serializable>();
        arguments.put("arg1", arg1);
        arguments.put("arg2", arg2);
        Message message2 = new DefaultMessage(text, NamedArguments.convert(arguments));

        assertEquals(message1, message2);
        assertEquals(message1.toString(), message2.toString());
        assertEquals(message1.toString(this.messageContext), message2.toString(this.messageContext));
    }
}