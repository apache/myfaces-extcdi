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
package org.apache.myfaces.extensions.cdi.core.test.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.api.util.ConfigUtils;
import org.apache.myfaces.extensions.cdi.core.impl.config.AbstractConfiguredValueResolver;

import javax.enterprise.inject.Typed;
import java.util.Collections;
import java.util.List;

/**
 * Test resolver for configured values
 */
@Typed()
@InvocationOrder(600)
public class PropertyFileResolverForProjectStage extends AbstractConfiguredValueResolver
{
    //ONLY needed for our unit tests to avoid that other tests are influenced
    private static ThreadLocal<Boolean> active = new ThreadLocal<Boolean>();

    public static void activate()
    {
        active.set(Boolean.TRUE);
    }

    public static void deactivate()
    {
        active.set(null);
        active.remove();
    }

    @Override
    public boolean isActivated()
    {
        return Boolean.TRUE.equals(active.get());
    }

    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        String key = "" + descriptor.getKey();

        if(!"ProjectStage".endsWith(key))
        {
            return Collections.emptyList();
        }

        for(String configuredValue : ConfigUtils.getConfiguredValue(key))
        {
            add(configuredValue);
        }

        return getConfiguredValues(descriptor.getTargetType());
    }
}
