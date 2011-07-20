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
package org.apache.myfaces.extensions.cdi.core.api.provider;

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Replacement for the service-loader to support java 5 and to provide additional features like
 * sorting and a basic version of {@link org.apache.myfaces.extensions.cdi.core.api.activation.ExpressionActivated}
 * and injection as soon as it is available and demo.MyClass:optional
 *
 * @author Gerhard Petracek
 */
public abstract class ServiceProvider<T>
{
    protected static final String SERVICE_CONFIG = "META-INF/services/";
    protected static final String FILE_ENCODING = "UTF-8";

    protected Class<T> serviceType;
    protected ServiceProviderContext serviceProviderContext;

    private static final String CUSTOM_SERVICE_PROVIDER_NAME =
            ServiceProvider.class.getName().replace(".api.", ".custom.");

    private static final String DEFAULT_SERVICE_PROVIDER_NAME =
            ServiceProvider.class.getName().replace(".api.", ".impl.");

    private static final String CUSTOM_SERVICE_PROVIDER_CONTEXT_NAME =
            ServiceProviderContext.class.getName().replace(".api.", ".custom.");

    private static final String DEFAULT_SERVICE_PROVIDER_CONTEXT_NAME =
            ServiceProviderContext.class.getName().replace(".api.", ".impl.");

    protected static final Class<? extends ServiceProvider> SERVICE_PROVIDER_CLASS;

    protected static final Class<? extends ServiceProviderContext> SERVICE_PROVIDER_CONTEXT_CLASS;

    static
    {
        Class serviceProviderClass = ClassUtils.tryToLoadClassForName(CUSTOM_SERVICE_PROVIDER_NAME);

        if(serviceProviderClass == null)
        {
            serviceProviderClass = ClassUtils.tryToLoadClassForName(DEFAULT_SERVICE_PROVIDER_NAME);
        }

        //TODO add null check
        SERVICE_PROVIDER_CLASS = serviceProviderClass;

        Class serviceProviderContextClass = ClassUtils.tryToLoadClassForName(CUSTOM_SERVICE_PROVIDER_CONTEXT_NAME);

        if(serviceProviderContextClass == null)
        {
            serviceProviderContextClass = ClassUtils.tryToLoadClassForName(DEFAULT_SERVICE_PROVIDER_CONTEXT_NAME);
        }

        //TODO add null check
        SERVICE_PROVIDER_CONTEXT_CLASS = serviceProviderContextClass;
    }

    public static <S> List<S> loadServices(Class<S> serviceType)
    {
        ServiceProviderContext<S> serviceProviderContext =
                ClassUtils.tryToInstantiateClass(SERVICE_PROVIDER_CONTEXT_CLASS);

        return loadServices(serviceType, serviceProviderContext);
    }

    public static <S> List<S> loadServices(Class<S> serviceType, ServiceProviderContext<S> serviceProviderContext)
    {
        //no fallback - would be possible via an add-on and a custom ServiceProvider
        ServiceProvider<S> serviceProvider = getServiceProvider(serviceType, serviceProviderContext);

        return serviceProvider.loadServiceImplementations();
    }

    private static <S> ServiceProvider<S> getServiceProvider(
            Class<S> serviceType, ServiceProviderContext serviceProviderContext)
    {
        try
        {
            Constructor constructor =
                    SERVICE_PROVIDER_CLASS.getDeclaredConstructor(Class.class, ServiceProviderContext.class);

            constructor.setAccessible(true);

            ServiceProvider<S> customServiceProvider =
                    (ServiceProvider<S>) constructor.newInstance(serviceType, serviceProviderContext);

            return customServiceProvider;
        }
        catch (Exception e)
        {
            Logger logger = Logger.getLogger(SERVICE_PROVIDER_CLASS.getName());

            if(logger.isLoggable(Level.WARNING))
            {
                logger.log(Level.WARNING, "Can't instantiate " + SERVICE_PROVIDER_CLASS.getName(), e);
            }
        }

        //TODO
        return null;
    }

    protected ServiceProvider(Class<T> serviceType, ServiceProviderContext serviceProviderContext)
    {
        this.serviceType = serviceType;
        this.serviceProviderContext = serviceProviderContext;
    }

    protected abstract List<T> loadServiceImplementations();
}
