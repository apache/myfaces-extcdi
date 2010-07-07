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

import org.apache.myfaces.extensions.cdi.message.api.MessageBuilder;
import org.apache.myfaces.extensions.cdi.message.impl.SimpleMessageBuilder;

/**
 * @author Gerhard Petracek
 */
class TestMessageBuilder extends SimpleMessageBuilder
{
    private static final long serialVersionUID = -92376642213127412L;

    private TestMessageBuilder()
    {
    }

    public static MessageBuilder message()
    {
        return new TestMessageBuilder();
    }

    public static MessageBuilder technicalMessage()
    {
        return new TestMessageBuilder().payload(TechnicalMessage.PAYLOAD);
    }

    public static MessageBuilder label()
    {
        return new TestMessageBuilder().payload(Label.PAYLOAD);
    }
}
