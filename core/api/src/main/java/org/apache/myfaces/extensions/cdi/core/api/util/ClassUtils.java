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
package org.apache.myfaces.extensions.cdi.core.api.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.net.URL;

/**
 * @author Gerhard Petracek
 */
public class ClassUtils
{
        /**
     * Detect the right ClassLoader.
     * The lookup order is determined by:
     * <ol>
     *  <li>ContextClassLoader of the current Thread</li>
     *  <li>ClassLoader of the given Object 'o'</li>
     *  <li>ClassLoader of this very CodiUtils class</li>
     * </ol>
     *
     * @param o if not <code>null</code> it may get used to detect the classloader.
     * @return The {@link ClassLoader} which should get used to create new instances
     */
    public static ClassLoader getClassLoader(Object o)
    {
        ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
            {
                public ClassLoader run()
                {
                    try
                    {
                        return Thread.currentThread().getContextClassLoader();
                    }
                    catch (Exception e)
                    {
                        return null;
                    }
                }
            }
        );

        if (loader == null && o != null)
        {
            loader = o.getClass().getClassLoader();
        }

        if (loader == null)
        {
            loader = ClassUtils.class.getClassLoader();
        }

        return loader;
    }

    public static Class tryToLoadClassForName(String name)
    {
        try
        {
            return loadClassForName(name);
        }
        catch (ClassNotFoundException e)
        {
            //do nothing - it's just a try
            return null;
        }
    }

    public static Class loadClassForName(String name) throws ClassNotFoundException
    {
        try
        {
            // Try WebApp ClassLoader first
            return Class.forName(name, false, // do not initialize for faster startup
               getClassLoader(null));
        }
        catch (ClassNotFoundException ignore)
        {
            // fallback: Try ClassLoader for ClassUtils (i.e. the myfaces.jar lib)
            return Class.forName(name, false, // do not initialize for faster startup
                ClassUtils.class.getClassLoader());
        }
    }

    public static <T> T tryToInstantiateClass(Class<T> targetClass)
    {
        try
        {
            return targetClass.newInstance();
        }
        catch (Throwable t)
        {
            //do nothing - it was just a try
        }
        return null;
    }

    public static <T> T tryToInstantiateClass(Class targetClass, Class<T> type)
    {
        try
        {
            return (T)targetClass.newInstance();
        }
        catch (Throwable t)
        {
            //do nothing - it was just a try
        }
        return null;
    }

    public static Object tryToInstantiateClassForName(String className)
    {
        try
        {
            return instantiateClassForName(className);
        }
        catch (Throwable t)
        {
            //do nothing - it was just a try
        }
        return null;
    }

    public static Object instantiateClassForName(String className)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        return loadClassForName(className).newInstance();
    }

    public static String getJarVersion(Class targetClass)
    {
        String classFilePath = targetClass.getCanonicalName().replace('.', '/') + ".class";
        String manifestFilePath = "/META-INF/MANIFEST.MF";

        String classLocation = targetClass.getResource(targetClass.getSimpleName() + ".class").toString();
        String manifestFileLocation = classLocation
                .substring(0, classLocation.indexOf(classFilePath) - 1) + manifestFilePath;

        try
        {
            return new Manifest(new URL(manifestFileLocation).openStream())
                    .getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        }
        catch (Throwable t)
        {
            return null;
        }
    }
}
