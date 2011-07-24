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
package org.apache.myfaces.extensions.cdi.core.impl.provider;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProviderContext;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.provider.spi.EditableServiceProviderContext;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
@Typed()
@SuppressWarnings({"unchecked"})
@InvocationOrder(200)
public class DefaultServiceProvider<T> extends SimpleServiceProvider<T>
{
    private static Map<ClassLoader, Boolean> deploymentFinished = new ConcurrentHashMap<ClassLoader, Boolean>();
    private static Map<Class<?>, List<Class<?>>> serviceCache = new ConcurrentHashMap<Class<?>, List<Class<?>>>();

    protected DefaultServiceProvider(Class<T> serviceType, ServiceProviderContext serviceProviderContext)
    {
        super(serviceType, serviceProviderContext);

        //TODO
        if(serviceProviderContext instanceof DefaultServiceProviderContext)
        {
            ((DefaultServiceProviderContext)serviceProviderContext).setDeploymentFinished(isDeploymentFinished());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected List<T> loadServiceImplementations()
    {
        List<Class<?>> result = serviceCache.get(this.serviceType);

        if(result != null)
        {
            List<T> foundServices = new ArrayList<T>();

            for(Class<?> serviceClass : result)
            {
                foundServices.add(createInstance(serviceClass));
            }

            return foundServices;
        }

        return super.loadServiceImplementations();
    }

    @Override
    protected List<Class<?>> resolveServiceImplementations()
    {
        super.resolveServiceImplementations();

        if(this.serviceProviderContext instanceof EditableServiceProviderContext)
        {
            ((EditableServiceProviderContext)this.serviceProviderContext)
                    .preInstallServices(this.foundServiceClasses);
        }

        serviceCache.put(this.serviceType, this.foundServiceClasses);
        return this.foundServiceClasses;
    }

    protected boolean isServiceSupported(Class<T> serviceClass)
    {
        //noinspection SimplifiableIfStatement
        if(this.serviceProviderContext instanceof EditableServiceProviderContext)
        {
            return !((EditableServiceProviderContext)this.serviceProviderContext)
                    .filterService(serviceClass);
        }
        return true;
    }

    @Override
    protected T createInstance(Class<?> serviceClass)
    {
        T instance = super.createInstance(serviceClass);

        if(this.serviceProviderContext instanceof EditableServiceProviderContext)
        {
            return ((EditableServiceProviderContext<T>)this.serviceProviderContext).postConstruct(instance);
        }

        return instance;
    }

    protected void activateInjectionSupport(@Observes AfterDeploymentValidation afterDeploymentValidation)
    {
        deploymentFinished.put(ClassUtils.getClassLoader(null), Boolean.TRUE);
    }

    //deactivated by default - register this class as cdi extension to activate it
    private boolean isDeploymentFinished()
    {
        return Boolean.TRUE.equals(deploymentFinished.get(ClassUtils.getClassLoader(null)));
    }

    protected void reset()
    {
        foundServiceClasses.clear();
        serviceCache.clear();
    }
}
