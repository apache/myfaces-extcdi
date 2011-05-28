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
package org.apache.myfaces.extensions.cdi.jsf.impl.message;

import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;

import javax.enterprise.inject.Typed;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Typed()
class SimpleNamedArgument implements NamedArgument
{
    private static final long serialVersionUID = -2658137685121716653L;

    private final String name;
    private final Serializable value;

    SimpleNamedArgument(String name, Serializable value)
    {
        this.name = name;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable getValue()
    {
        return value;
    }
}
