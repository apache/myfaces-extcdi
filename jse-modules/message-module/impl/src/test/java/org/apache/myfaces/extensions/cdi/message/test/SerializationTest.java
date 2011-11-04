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

import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfig;
import org.apache.myfaces.extensions.cdi.message.impl.DefaultFormatterFactory;
import org.apache.myfaces.extensions.cdi.message.impl.NumberedArgumentAwareMessageInterpolator;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;

import java.util.Locale;

/**
 * Tests for the serializable parts
 */
public class SerializationTest extends AbstractMessageContextAwareTest
{
    @Test
    public void messageContextConfigSerializationTest()
    {
        this.messageContext.config()
                .change()
                .messageResolver(new TestMessageResolver())
                .messageInterpolator(new NumberedArgumentAwareMessageInterpolator())
                .localeResolver(new TestCustomMessageContext())
                .addMessageHandler(new TestInMemoryMessageHandler())
                .formatterFactory(new DefaultFormatterFactory())
                .addFormatter(new TestFormatter(String.class))
                .addFormatterConfig(Number.class, new TestEnglishNumberConfig(), Locale.ENGLISH);

        MessageContextConfig clonedConfig = TestDeepObjectCloner.clone(this.messageContext.config(), MessageContextConfig.class);

        assertNotSame(this.messageContext.config(), clonedConfig);
    }

}