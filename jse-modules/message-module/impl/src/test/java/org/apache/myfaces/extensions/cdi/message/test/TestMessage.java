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
import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.impl.DefaultMessage;

import java.util.Set;

class TestMessage extends DefaultMessage
{
    private static final long serialVersionUID = 4209087914276552447L;

    public TestMessage(Message message)
    {
        super(message);
    }

    public TestMessage(String messageDescriptor)
    {
        super(messageDescriptor);
    }

    public TestMessage(String messageDescriptor, Set<NamedArgument> namedArguments)
    {
        super(messageDescriptor, namedArguments);
    }

    public TestMessage(String messageDescriptor, MessagePayload severity, Object... arguments)
    {
        super(messageDescriptor, severity, arguments);
    }

    public TestMessage(String messageDescriptor, MessagePayload severity, Set<NamedArgument> namedArguments)
    {
        super(messageDescriptor, severity, namedArguments);
    }
}
