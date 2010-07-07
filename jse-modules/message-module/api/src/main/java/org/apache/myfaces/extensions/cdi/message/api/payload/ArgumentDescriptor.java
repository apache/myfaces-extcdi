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
package org.apache.myfaces.extensions.cdi.message.api.payload;

/**
 * marker for {@link org.apache.myfaces.extensions.cdi.message.api.Localizable} arguments which were stored as
 * message-key (format {key}) usually such lazy arguments are e.g. arguments for messages which should be resolved
 * as soon as the message should be resolved
 *
 * example:
 * message-descriptor: info
 * message-argument (instance of {@link org.apache.myfaces.extensions.cdi.message.api.Localizable}: additional_info
 *
 * as soon as we save the argument e.g. as string we lose the information if it is
 * {@link org.apache.myfaces.extensions.cdi.message.api.Localizable} or if it is just a string-value
 * so we need the key-syntax here as well
 *
 * info is e.g.:
 * info=hello: {0}
 *
 * additional_info is e.g.:
 * additional_info=codi
 *
 * if we store additional_info as string we have to store: {additional_info}
 * if we restore and resolve the final text the result should be: hello: codi
 *
 * this interface is a marker payload
 *
 * @author Gerhard Petracek
 */
public final class ArgumentDescriptor extends AbstractMessagePayload
{
    private static final long serialVersionUID = -8923485139364779354L;

    public static final ArgumentDescriptor PAYLOAD = new ArgumentDescriptor();

    private ArgumentDescriptor()
    {
    }
}
