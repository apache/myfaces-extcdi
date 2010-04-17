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
package org.apache.myfaces.extensions.cdi.message.impl;

import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;
import org.apache.myfaces.extensions.cdi.message.api.payload.InternalMessage;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class DefaultInternalMessage extends DefaultMessage
{
    private static final long serialVersionUID = 7940762016110497679L;

    public DefaultInternalMessage(String messageTemplate, Serializable... arguments)
    {
        super(messageTemplate, MessageSeverity.Info.class, arguments);
        addPayload(InternalMessage.class);
    }

    public DefaultInternalMessage(String messageTemplate, Set<NamedArgument> namedArguments)
    {
        super(messageTemplate, MessageSeverity.Info.class, namedArguments);
        addPayload(InternalMessage.class);
    }

    public DefaultInternalMessage(String messageTemplate,
                                  Class<? extends MessagePayload> severity,
                                  Serializable... arguments)
    {
        super(messageTemplate, severity, arguments);
        addPayload(InternalMessage.class);
    }

    public DefaultInternalMessage(String messageTemplate,
                                  Class<? extends MessagePayload> severity,
                                  Set<NamedArgument> namedArguments)
    {
        super(messageTemplate, severity, namedArguments);
        addPayload(InternalMessage.class);
    }
}