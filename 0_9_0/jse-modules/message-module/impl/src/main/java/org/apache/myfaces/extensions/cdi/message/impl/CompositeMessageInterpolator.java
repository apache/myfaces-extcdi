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

import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;

import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
public class CompositeMessageInterpolator implements MessageInterpolator, Serializable
{
    private static final long serialVersionUID = 7138747032627702804L;
    private MessageInterpolator[] messageInterpolators;

    public CompositeMessageInterpolator(MessageInterpolator... messageInterpolators)
    {
        this.messageInterpolators = messageInterpolators;
    }

    public String interpolate(MessageContext messageContext, String messageDescriptor, Serializable... arguments)
    {
        String result = messageDescriptor;
        for (MessageInterpolator messageInterpolator : this.messageInterpolators)
        {
            result = messageInterpolator.interpolate(messageContext, result, arguments);
        }

        return result;
    }
}