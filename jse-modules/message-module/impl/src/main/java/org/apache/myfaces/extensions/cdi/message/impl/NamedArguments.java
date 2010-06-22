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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */

/**
 * @author Gerhard Petracek
 */
public class NamedArguments
{
    private NamedArgumentBuilder namedArgumentBuilder = new NamedArgumentBuilder();

    private NamedArguments()
    {
    }

    public static Serializable[] convert(Map<String, Serializable> arguments)
    {
        if (arguments == null || arguments.size() == 0)
        {
            return new Serializable[]{};
        }

        NamedArguments.NamedArgumentBuilder namedArgumentBuilder = new NamedArguments().getNamedArgumentBuilder();
        for (Map.Entry<String, Serializable> entry : arguments.entrySet())
        {
            namedArgumentBuilder = namedArgumentBuilder.add(entry.getKey(), entry.getValue());
        }

        return namedArgumentBuilder.create();
    }

    public static NamedArgumentBuilder add(String name, Serializable value)
    {
        return new NamedArguments().getNamedArgumentBuilder().add(name, value);
    }

    private NamedArgumentBuilder getNamedArgumentBuilder()
    {
        return namedArgumentBuilder;
    }

    public class NamedArgumentBuilder
    {
        private List<NamedArgument> parameters = new ArrayList<NamedArgument>();

        private NamedArgumentBuilder()
        {
        }

        public NamedArgumentBuilder add(String name, Serializable value)
        {
            this.parameters.add(new DefaultNamedArgument(name, value));
            return this;
        }

        public NamedArgument[] create()
        {
            return this.parameters.toArray(new NamedArgument[this.parameters.size()]);
        }
    }
}
