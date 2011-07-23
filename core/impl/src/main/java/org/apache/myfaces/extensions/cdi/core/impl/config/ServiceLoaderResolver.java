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
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProvider;
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProviderContext;
import org.apache.myfaces.extensions.cdi.core.impl.provider.DefaultServiceProviderContext;
import org.apache.myfaces.extensions.cdi.core.impl.provider.spi.EditableServiceProviderContext;

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
        final Class targetType = descriptor.getTargetType();
        if(Modifier.isAbstract(targetType.getModifiers()) || Modifier.isInterface(targetType.getModifiers()))
        {
            @SuppressWarnings({"unchecked"})
            List<T> services = ServiceProvider.loadServices(targetType, new EditableServiceProviderContext<T>()
            {
                private ServiceProviderContext serviceProviderContext =
                        ServiceProvider.createServiceProviderContext(targetType);

                public T postConstruct(T instance)
                {
                    if(serviceProviderContext instanceof EditableServiceProviderContext)
                    {
                        return ((EditableServiceProviderContext<T>)serviceProviderContext)
                                .postConstruct(instance);
                    }
                    return instance;
                }

                public boolean filterService(Class serviceClass)
                {
                    if(serviceProviderContext instanceof EditableServiceProviderContext &&
                            !(serviceProviderContext instanceof DefaultServiceProviderContext))
                    {
                        return ((EditableServiceProviderContext<T>)serviceProviderContext)
                                .filterService(serviceClass);
                    }
                    //do nothing
                    return false;
                }

                public void preInstallServices(List<Class<?>> foundServiceClasses)
                {
                    if(serviceProviderContext instanceof EditableServiceProviderContext)
                    {
                        ((EditableServiceProviderContext<T>)serviceProviderContext)
                                .preInstallServices(foundServiceClasses);
                    }
                }

                public ClassLoader getClassLoader()
                {
                    return serviceProviderContext.getClassLoader();
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