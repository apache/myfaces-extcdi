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
package org.apache.myfaces.extensions.cdi.message.api;

import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import java.io.Serializable;
import java.util.Set;

/**
 * Adds severities to the basic behaviour of messages
 */
public abstract class AbstractMessageWithSeverity extends AbstractMessage implements MessageWithSeverity
{
    public AbstractMessageWithSeverity(Message message)
    {
        super(message);
    }

    public AbstractMessageWithSeverity(String messageDescriptor,
                                       MessagePayload severity,
                                       Serializable... arguments)
    {
        super(messageDescriptor, arguments);
        addPayload(severity);
    }

    public AbstractMessageWithSeverity(String messageDescriptor,
                                       MessagePayload severity,
                                       Set<NamedArgument> namedArguments)
    {
        super(messageDescriptor, namedArguments);
        addPayload(severity);
    }

    public MessagePayload getSeverity()
    {
        return getPayload().get(MessageSeverity.class);
    }
}
