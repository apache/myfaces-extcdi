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

import java.io.Serializable;

/**
 * allows to customize the creation of a message.
 * only use it if you would like to create a different type of message via the fluent api
 * ({@link org.apache.myfaces.extensions.cdi.message.api.MessageBuilder})
 * in all other cases you can also use {@link org.apache.myfaces.extensions.cdi.message.api.MessageResolver}
 * e.g. to customize messages e.g. based on payload
 *
 * @author Gerhard Petracek
 */
public interface MessageFactory extends Serializable
{
    /**
     * @param messageDescriptor the message key (or inline-message) for the message
     * @param messagePayload the initial payload of the message
     * @return a new message instance
     */
    Message create(String messageDescriptor,  Class<? extends MessagePayload> messagePayload);
}
