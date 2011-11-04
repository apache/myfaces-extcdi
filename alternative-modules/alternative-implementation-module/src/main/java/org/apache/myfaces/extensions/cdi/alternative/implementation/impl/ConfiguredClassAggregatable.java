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
package org.apache.myfaces.extensions.cdi.alternative.implementation.impl;

import org.apache.myfaces.extensions.cdi.alternative.implementation.api.AlternativeImplementation;
import org.apache.myfaces.extensions.cdi.core.api.Aggregatable;

import javax.enterprise.inject.Typed;
import java.util.List;

/**
 * Helper for collecting multiple implementations
 */
@Typed()
class ConfiguredClassAggregatable<T> implements Aggregatable<T>
{
    private Class spiClass;
    private List<T> customImplementations;
    private boolean configClassMode;

    ConfiguredClassAggregatable()
    {
    }

    ConfiguredClassAggregatable(Class spiClass, List customImplementations, boolean configClassMode)
    {
        this.spiClass = spiClass;
        this.customImplementations = customImplementations;
        this.configClassMode = configClassMode;
    }

    /**
     * {@inheritDoc}
     */
    public void add(T o)
    {
        if (o.getClass().isAnnotationPresent(AlternativeImplementation.class))
        {
            this.customImplementations.add(o);
        }
    }

    /**
     * {@inheritDoc}
     */
    public T create()
    {
        if(!this.configClassMode)
        {
            if (this.customImplementations.size() > 1)
            {
                //TODO
                //here we have a spi which is used for other purposes
                return null;
            }

            if (this.customImplementations.size() == 1)
            {
                return this.customImplementations.iterator().next();
            }
        }
        else if(!this.customImplementations.isEmpty())
        {
            return filterCustomCodiConfigImplementation();
        }
        return null;
    }

    private T filterCustomCodiConfigImplementation()
    {
        Class configClass = this.spiClass;
        for(T customImplementation : this.customImplementations)
        {
            if(configClass.isAssignableFrom(customImplementation.getClass()))
            {
                return customImplementation;
            }
        }
        return null;
    }
}
