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

import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.core.api.tools.InvocationOrderComparator;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.util.ConfigUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Replacement for the service-loader to support java 5 and to provide additional features like
 * sorting and a basic version of {@link org.apache.myfaces.extensions.cdi.core.api.activation.ExpressionActivated}
 * and injection as soon as it is available
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
        Class<? extends ServiceProvider> serviceProviderClass = null;
        Class<? extends ServiceProviderContext> serviceProviderContextClass = null;
        try
        {
            serviceProviderClass = initServiceProvider();
            serviceProviderContextClass = initServiceProviderContext();
        }
        catch (Exception e)
        {
            //fallback - TODO log a warning
            try
            {
                if (serviceProviderClass == null)
                {
                    serviceProviderClass = ClassUtils.loadClassForName(DEFAULT_SERVICE_PROVIDER_NAME);
                }

                serviceProviderContextClass = ClassUtils.loadClassForName(DEFAULT_SERVICE_PROVIDER_CONTEXT_NAME);
            }
            catch (Exception exception)
            {
                throw new UnhandledException(exception);
            }
        }

        SERVICE_PROVIDER_CLASS = serviceProviderClass;
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

            if (logger.isLoggable(Level.WARNING))
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


    /*
     * private
     */
    private static Class<? extends ServiceProvider> initServiceProvider()
    {
        List<String> serviceProviderClassNames = new ArrayList<String>();
        serviceProviderClassNames.add(CUSTOM_SERVICE_PROVIDER_NAME);

        List<String> configuredServiceProviders =
                ConfigUtils.getConfiguredValue("ServiceProvider." + ServiceProvider.class.getName());

        if (configuredServiceProviders != null)
        {
            serviceProviderClassNames.addAll(configuredServiceProviders);
        }

        List<Class<? extends ServiceProvider>> serviceProviderClassList =
                new ArrayList<Class<? extends ServiceProvider>>(serviceProviderClassNames.size());

        Class<? extends ServiceProvider> currentServiceProviderClass = null;
        for (String currentServiceProviderName : serviceProviderClassNames)
        {
            try
            {
                currentServiceProviderClass =
                        ClassUtils.tryToLoadClassForName(currentServiceProviderName);
            }
            catch (Exception e)
            {
                //TODO logging
            }

            if (currentServiceProviderClass != null)
            {
                serviceProviderClassList.add(currentServiceProviderClass);
            }
        }

        Class serviceProviderClass = null;
        if (!serviceProviderClassList.isEmpty())
        {
            Collections.sort(serviceProviderClassList, new InvocationOrderComparator<Object>());
            serviceProviderClass = serviceProviderClassList.iterator().next();
        }

        if (serviceProviderClass == null)
        {
            serviceProviderClass = ClassUtils.tryToLoadClassForName(DEFAULT_SERVICE_PROVIDER_NAME);
        }
        return serviceProviderClass;
    }

    private static Class<? extends ServiceProviderContext> initServiceProviderContext()
    {
        List<String> serviceProviderContextClassNames = new ArrayList<String>();
        serviceProviderContextClassNames.add(CUSTOM_SERVICE_PROVIDER_CONTEXT_NAME);

        List<String> configuredServiceProviderContextList =
                ConfigUtils.getConfiguredValue("ServiceProviderContext." + ServiceProviderContext.class.getName());

        if (configuredServiceProviderContextList != null)
        {
            serviceProviderContextClassNames.addAll(configuredServiceProviderContextList);
        }

        List<Class<? extends ServiceProviderContext>> serviceProviderContextClassList =
                new ArrayList<Class<? extends ServiceProviderContext>>(serviceProviderContextClassNames.size());

        Class<? extends ServiceProviderContext> currentServiceProviderContextClass = null;
        for (String currentServiceProviderContextName : serviceProviderContextClassNames)
        {
            try
            {
                currentServiceProviderContextClass =
                        ClassUtils.tryToLoadClassForName(currentServiceProviderContextName);
            }
            catch (Exception e)
            {
                //TODO logging
            }

            if (currentServiceProviderContextClass != null)
            {
                serviceProviderContextClassList.add(currentServiceProviderContextClass);
            }
        }

        Class serviceProviderContextClass = null;
        if (!serviceProviderContextClassList.isEmpty())
        {
            Collections.sort(serviceProviderContextClassList, new InvocationOrderComparator<Object>());
            serviceProviderContextClass = serviceProviderContextClassList.iterator().next();
        }

        if (serviceProviderContextClass == null)
        {
            serviceProviderContextClass = ClassUtils.tryToLoadClassForName(DEFAULT_SERVICE_PROVIDER_CONTEXT_NAME);
        }
        return serviceProviderContextClass;
    }
}
