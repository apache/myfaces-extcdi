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
 * @author Gerhard Petracek
 */
public interface MessageSeverity
{
    Info INFO = new Info();
    Warn WARN = new Warn();
    Error ERROR = new Error();
    Fatal FATAL = new Fatal();

    @MessagePayloadKey(MessageSeverity.class)
    final class Info  extends AbstractMessagePayload
    {
        private static final long serialVersionUID = -8366105004121496310L;

        private Info()
        {
        }
    }

    @MessagePayloadKey(MessageSeverity.class)
    final class Warn extends AbstractMessagePayload
    {
        private static final long serialVersionUID = -8656172186651851576L;

        private Warn()
        {
        }
    }

    @MessagePayloadKey(MessageSeverity.class)
    final class Error extends AbstractMessagePayload
    {
        private static final long serialVersionUID = -1825994085836261242L;

        private Error()
        {
        }
    }

    @MessagePayloadKey(MessageSeverity.class)
    final class Fatal extends AbstractMessagePayload
    {
        private static final long serialVersionUID = 1323372922553756526L;

        private Fatal()
        {
        }
    }
}
