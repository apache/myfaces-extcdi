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
import org.junit.Test;

/**
 * @author Gerhard Petracek
 */
public class ArgumentDescriptorTest extends AbstractTest
{
    @Test
    public void lazyNumberedAttributeTest()
    {
        TestArgument dynArgument = new TestArgument("brand");

        assertEquals("{brand}", dynArgument.getKey());

        String messageText = this.messageContext.config().use().messageInterpolator(new NumberedArgumentAwareMessageInterpolator()).create()
                .message().text("{info}").argument(dynArgument.getKey()).toText();

        assertEquals("jCar", dynArgument.toString(this.messageContext));
        assertEquals("value: jCar", messageText);
    }

    @Test
    public void lazyNamedAttributeTest()
    {
        TestArgument dynArgument = new TestArgument("brand");

        assertEquals("{brand}", dynArgument.getKey());

        String messageText = this.messageContext.config().use().messageInterpolator(new ELAwareMessageInterpolator(new TestELProvider())).create()
                .message().text("{brand_info}").namedArgument("brand", dynArgument.getKey()).toText();

        assertEquals("jCar", dynArgument.toString(this.messageContext));
        assertEquals("value: jCar", messageText);
    }
}