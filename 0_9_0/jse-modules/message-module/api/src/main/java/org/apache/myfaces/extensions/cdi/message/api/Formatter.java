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

import java.io.Serializable;

/**
 * usually a formatter is responsible for one type and allows to format
 * this type as string
 *
 * @author Gerhard Petracek
 * @author Manfred Geiler
 */
public interface Formatter<T> extends Serializable
{
    /**
     * answers the question if the instance is able to format an object of the given type
     * @param type the type of the instance which has to be formatted
     * @return true to signal that the formatter is responsible for formatting the given type
     */
    boolean isResponsibleFor(Class<?> type);

    /**
     * @return true if it is allowed to cache the instance of the formatter
     */
    boolean isStateless();

    /**
     * formats the given instance as string
     *
     * @param messageContext current message context
     * @param valueToFormat an instance which should be formatted
     * @return the formatted result as string
     */
    String format(MessageContext messageContext, T valueToFormat);
}
