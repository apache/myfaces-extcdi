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

import java.util.Map;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
public interface MessageResolver extends Serializable
{
    String MISSING_RESOURCE_MARKER = "???";

    /**
     * @param messageContext the current {@link org.apache.myfaces.extensions.cdi.message.api.MessageContext}
     * @param messageDescriptor the message key (or in-lined text) of the current message
     * @param payload the payload of the message e.g. to use different message sources
     * @return the final but not interpolated message text
     */
    String getMessage(MessageContext messageContext,
                      String messageDescriptor,
                      Map<Class, MessagePayload> payload);
}
