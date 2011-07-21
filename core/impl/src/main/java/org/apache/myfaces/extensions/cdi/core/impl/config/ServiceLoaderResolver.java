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
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.impl.provider.DefaultServiceProvider;
import org.apache.myfaces.extensions.cdi.core.impl.provider.DefaultServiceProviderContext;

import javax.enterprise.inject.Typed;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Modifier;

/**
 * @author Gerhard Petracek
 */
@Typed()
@InvocationOrder(200)
public class ServiceLoaderResolver extends AbstractConfiguredValueResolver
{
    /**
     * {@inheritDoc}
     */
    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        List<T> result = new ArrayList<T>();
        Class targetType = descriptor.getTargetType();
        if(Modifier.isAbstract(targetType.getModifiers()) || Modifier.isInterface(targetType.getModifiers()))
        {
            @SuppressWarnings({"unchecked"})
            List<T> services = DefaultServiceProvider.loadServices(targetType, new DefaultServiceProviderContext()
            {
                @Override
                public boolean filterService(Class serviceClass)
                {
                    //do nothing
                    return false;
                }
            });

            for(T currentInstance : services)
            {
                result.add(currentInstance);
            }
        }
        return result;
    }
}