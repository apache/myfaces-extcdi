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

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
public class NamedArgumentBuilder
{
    private List<NamedArgument> parameters = new ArrayList<NamedArgument>();

    NamedArgumentBuilder()
    {
    }

    /**
     * Allows to add further arguments which are mapped to a name.
     * @param name name of the argument
     * @param value value of the argument
     * @return the current instance of the builder
     */
    public NamedArgumentBuilder add(String name, Serializable value)
    {
        this.parameters.add(new DefaultNamedArgument(name, value));
        return this;
    }

    /**
     * Creates an array of {@link NamedArgument}s for the stored arguments
     * @return array of the added arguments
     */
    public NamedArgument[] create()
    {
        return this.parameters.toArray(new NamedArgument[this.parameters.size()]);
    }
}
