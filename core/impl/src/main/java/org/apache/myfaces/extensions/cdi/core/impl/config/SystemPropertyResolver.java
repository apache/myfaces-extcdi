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
package org.apache.myfaces.extensions.cdi.core.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;

import javax.enterprise.inject.Typed;
import java.util.List;
import java.util.Collections;

/**
 * {@link org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver} for system properties
 */
@Typed()
@InvocationOrder(100)
public class SystemPropertyResolver extends AbstractConfiguredValueResolver
{
    private static final String BASE_NAME = "org.apache.myfaces.extensions.cdi.";

    /**
     * {@inheritDoc}
     */
    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        String key = "" + descriptor.getKey();

        if(!key.contains("."))
        {
            key = BASE_NAME + key;
        }

        String configuredValue = System.getProperty(key);

        if(configuredValue == null)
        {
            return Collections.emptyList();
        }
        
        if(!String.class.isAssignableFrom(descriptor.getTargetType()))
        {
            if(Boolean.class.isAssignableFrom(descriptor.getTargetType()))
            {
                add(descriptor.getTargetType().cast(Boolean.parseBoolean(configuredValue)));
            }
            else if(Integer.class.isAssignableFrom(descriptor.getTargetType()))
            {
                add(descriptor.getTargetType().cast(Integer.parseInt(configuredValue)));
            }
            else
            {
                Class<T> targetClass = ClassUtils.tryToLoadClassForName(configuredValue, descriptor.getTargetType());

                if(targetClass != null)
                {
                    add(targetClass);
                }
            }
        }
        else
        {
            add(configuredValue);
        }

        return getConfiguredValues(descriptor.getTargetType());
    }
}
