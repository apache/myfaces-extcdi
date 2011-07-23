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

import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProvider;
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProviderContext;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.inject.Typed;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class SimpleServiceProvider<T> extends ServiceProvider<T>
{
    protected List<Class<?>> foundServiceClasses = new ArrayList<Class<?>>();

    protected SimpleServiceProvider(Class<T> serviceType, ServiceProviderContext serviceProviderContext)
    {
        super(serviceType, serviceProviderContext);
    }

    /**
     * {@inheritDoc}
     */
    protected List<T> loadServiceImplementations()
    {
        List<Class<?>> result = resolveServiceImplementations();

        if(result == null)
        {
            return Collections.emptyList();
        }

        List<T> foundServices = new ArrayList<T>();

        for(Class<?> serviceClass : result)
        {
            foundServices.add(createInstance(serviceClass));
        }

        return foundServices;
    }

    protected List<Class<?>> resolveServiceImplementations()
    {
        for (URL configFile : getConfigFileList())
        {
            loadConfiguredServices(configFile);
        }

        return this.foundServiceClasses;
    }

    protected List<URL> getConfigFileList()
    {
        List<URL> serviceFiles = new ArrayList<URL>();

        try
        {
            Enumeration<URL> serviceFileEnumerator =
                    this.serviceProviderContext.getClassLoader().getResources(getConfigFileLocation());

            while (serviceFileEnumerator.hasMoreElements())
            {
                serviceFiles.add(serviceFileEnumerator.nextElement());
            }
        }
        catch (Exception e)
        {
            throw new UnhandledException(
                    "Failed to load " + this.serviceType.getName() + " configured in " + getConfigFileLocation(), e);
        }
        return serviceFiles;
    }

    protected String getConfigFileLocation()
    {
        return SERVICE_CONFIG + this.serviceType.getName();
    }

    protected void loadConfiguredServices(URL serviceFile)
    {
        InputStream inputStream = null;

        try
        {
            String serviceClassName;
            inputStream = serviceFile.openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, FILE_ENCODING));

            while ((serviceClassName = bufferedReader.readLine()) != null)
            {
                serviceClassName = extractConfiguredServiceClassName(serviceClassName);
                if (!"".equals(serviceClassName))
                {
                    loadService(serviceClassName);
                }
            }
        }
        catch (Exception e)
        {
            throw new UnhandledException("Failed to process service-config: " + serviceFile, e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (Exception e)
                {
                    throw new UnhandledException("Failed to close " + serviceFile, e);
                }
            }
        }
    }

    protected String extractConfiguredServiceClassName(String currentConfigLine)
    {
        int startOfComment = currentConfigLine.indexOf('#');

        if (startOfComment > -1)
        {
            currentConfigLine = currentConfigLine.substring(0, startOfComment);
        }
        return currentConfigLine.trim();
    }

    protected void loadService(String serviceClassName)
    {
        Class<T> serviceClass = (Class<T>) loadClass(serviceClassName);

        if (serviceClass != null &&
                !this.foundServiceClasses.contains(serviceClass) &&
                isServiceSupported(serviceClass))
        {
            this.foundServiceClasses.add(serviceClass);
        }
        else if(serviceClass == null)
        {
            throw new IllegalStateException(serviceClassName + " couldn't be loaded. " +
                    "Please ensure that this class is in the classpath or remove the entry from "
                    + getConfigFileLocation() + ". Or mark it as optional.");
        }
    }

    protected boolean isServiceSupported(Class<T> serviceClass)
    {
        return true; //TODO
    }

    protected Class<? extends T> loadClass(String serviceClassName)
    {
        Class<?> targetClass = ClassUtils.tryToLoadClassForName(serviceClassName);

        if(targetClass == null)
        {
            targetClass = loadClassForName(serviceClassName, this.serviceProviderContext.getClassLoader());

            if(targetClass == null)
            {
                targetClass = loadClassForName(serviceClassName, ClassUtils.getClassLoader(null));

                if(targetClass == null)
                {
                    return null;
                }
            }
        }

        return targetClass.asSubclass(this.serviceType);
    }

    protected static Class<?> loadClassForName(String serviceClassName, ClassLoader classLoader)
    {
        if(classLoader == null)
        {
            return null;
        }

        try
        {
            return classLoader.loadClass(serviceClassName);
        }
        catch (Exception e)
        {
            return loadClassForName(serviceClassName, classLoader.getParent());
        }
    }

    protected T createInstance(Class<?> serviceClass)
    {
        try
        {
            Constructor<?> constructor = serviceClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T)constructor.newInstance();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Config file: " + getConfigFileLocation();
    }
}
