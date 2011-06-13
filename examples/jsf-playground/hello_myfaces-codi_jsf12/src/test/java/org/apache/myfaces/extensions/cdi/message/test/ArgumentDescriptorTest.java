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

import org.apache.myfaces.extensions.cdi.message.impl.NumberedArgumentAwareMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.ELAwareMessageInterpolator;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gerhard Petracek
 */
public class ArgumentDescriptorTest extends AbstractMessageTest
{
    @Test
    public void lazyNumberedArgumentsTest()
    {
        TestArgument dynArgument = new TestArgument("brand_key");

        Assert.assertEquals("{brand_key}", dynArgument.getKey());

        String messageText = this.messageContext.config().use().messageInterpolator(new NumberedArgumentAwareMessageInterpolator()).create()
                .message().text("{info}").argument(dynArgument.getKey()).toText();

        Assert.assertEquals("jCar", dynArgument.toString(this.messageContext));
        assertEquals("value: jCar", messageText);
    }

    @Test
    public void lazyNamedArgumentsTest()
    {
        TestArgument dynArgument = new TestArgument("brand_key");

        Assert.assertEquals("{brand_key}", dynArgument.getKey());

        String messageText = this.messageContext.config().use().messageInterpolator(new ELAwareMessageInterpolator(new TestELProvider())).create()
                .message().text("{brand_info}").namedArgument("brand", dynArgument.getKey()).toText();

        Assert.assertEquals("jCar", dynArgument.toString(this.messageContext));
        assertEquals("value: jCar", messageText);
    }

    @Test
    public void normalTextAsNumberedArgumentsTest()
    {
        TestArgument dynArgument = new TestArgument("brand-value") {
            private static final long serialVersionUID = -5398006578422304127L;

            @Override
            public String getKey()
            {
                return this.key;
            }
        };

        Assert.assertEquals("brand-value", dynArgument.getKey());

        String messageText = this.messageContext.config().use().messageInterpolator(new NumberedArgumentAwareMessageInterpolator()).create()
                .message().text("{info}").argument(dynArgument.getKey()).toText();

        assertEquals("value: brand-value", messageText);
    }

    @Test
    public void normalTextAsNamedArgumentsTest()
    {
        TestArgument dynArgument = new TestArgument("brand-value") {
            private static final long serialVersionUID = -5398006578422304127L;

            @Override
            public String getKey()
            {
                return this.key;
            }
        };

        Assert.assertEquals("brand-value", dynArgument.getKey());

        String messageText = this.messageContext.config().use().messageInterpolator(new ELAwareMessageInterpolator(new TestELProvider())).create()
                .message().text("{brand_info}").namedArgument("brand", dynArgument.getKey()).toText();

        assertEquals("value: brand-value", messageText);
    }
}