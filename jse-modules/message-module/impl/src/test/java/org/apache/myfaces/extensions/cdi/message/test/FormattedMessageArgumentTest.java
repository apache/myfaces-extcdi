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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public class FormattedMessageArgumentTest extends AbstractMessageContextAwareTest
{
    @Test
    public void createFormattedMessageGermanTest()
    {
        String messageText = this.messageContext.config().use().messageInterpolator(new NumberedArgumentAwareMessageInterpolator())
                .localeResolver(new TestGermanLocaleResolver())
                .addFormatterConfig(Number.class, new TestGermanNumberConfig()).create()
                .message().text("{formatted_number}").argument(new BigDecimal("7654.3210")).toText();

        assertEquals("value: 7.654,321", messageText);
    }

    @Test
    public void createFormattedMessageEnglishTest()
    {
        String messageText = this.messageContext.config().use().messageInterpolator(new NumberedArgumentAwareMessageInterpolator())
                .localeResolver(new TestEnglishLocaleResolver())
                .addFormatterConfig(Number.class, new TestGermanNumberConfig(), Locale.GERMAN)
                .addFormatterConfig(Number.class, new TestEnglishNumberConfig(), Locale.ENGLISH)
                .create()
                .message().text("{formatted_number}").argument(new BigDecimal("7654.3210")).toText();

        assertEquals("value: 7,654.321", messageText);
    }
}