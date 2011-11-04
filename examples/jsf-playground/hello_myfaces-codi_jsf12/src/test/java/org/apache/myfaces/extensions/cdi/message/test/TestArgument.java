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

import org.apache.myfaces.extensions.cdi.message.api.Localizable;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import java.io.Serializable;

/**
 * Localizable message argument
 */
class TestArgument implements Localizable, Serializable
{
    private static final long serialVersionUID = -4503818588255027507L;
    protected String key;

    TestArgument(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return "{" + key + "}";
    }

    public String toString(MessageContext messageContext)
    {
        return messageContext.message().text(getKey()).toText();
    }

    @Override
    public String toString()
    {
        return "???" + key + "???";
    }
}
