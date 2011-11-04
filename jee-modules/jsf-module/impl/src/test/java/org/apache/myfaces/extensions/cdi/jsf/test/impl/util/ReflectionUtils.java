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
package org.apache.myfaces.extensions.cdi.jsf.test.impl.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods using reflection
 */
public class ReflectionUtils
{
    private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getName());

    public static Method tryToGetMethod(Class targetClass, String targetMethodName)
    {
        return tryToGetMethod(targetClass, targetMethodName, null);
    }

    public static Method tryToGetMethod(Class targetClass, String targetMethodName, Class... parameterTypes)
    {
        try
        {
            return getMethod(targetClass, targetMethodName, parameterTypes);
        }
        catch (Exception e)
        {
            //do nothing - it's just a try
            return null;
        }
    }

    public static Method getMethod(Class targetClass, String targetMethodName)
        throws NoSuchMethodException
    {
        return getMethod(targetClass, targetMethodName, null);
    }

    public static Method getMethod(Class targetClass, String targetMethodName, Class... parameterTypes)
        throws NoSuchMethodException
    {
        Class currentClass = targetClass;
        Method targetMethod = null;

        while (!Object.class.getName().equals(currentClass.getName()))
        {
            try
            {
                targetMethod = currentClass.getDeclaredMethod(targetMethodName, parameterTypes);
                break;
            }
            catch (NoSuchMethodException e)
            {
                currentClass = currentClass.getSuperclass();
            }
        }

        if(targetMethod == null)
        {
            for (Class currentInterface : targetClass.getInterfaces())
            {
                currentClass = currentInterface;

                while (currentClass != null)
                {
                    try
                    {
                        targetMethod = currentClass.getDeclaredMethod(targetMethodName, parameterTypes);
                        break;
                    }
                    catch (NoSuchMethodException e)
                    {
                        currentClass = currentClass.getSuperclass();
                    }
                }
            }
        }

        if(targetMethod != null)
        {
            return targetMethod;
        }

        throw new NoSuchMethodException("there is no method with the name '" + targetMethodName + "'" +
                " class: " + targetClass.getName());
    }

    public static Object tryToInvokeMethod(Object target, Method method)
    {
        return tryToInvokeMethod(target, method, null);
    }

    public static Object tryToInvokeMethodOfClass(Class target, Method method)
    {
        return tryToInvokeMethodOfClass(target, method, null);
    }

    public static Object tryToInvokeMethodOfClass(Class target, Method method, Object[] args)
    {
        try
        {
            return invokeMethodOfClass(target, method, args);
        }
        catch (Exception e)
        {
            //do nothing - it's just a try
            return null;
        }
    }

    public static Object invokeMethodOfClass(Class target, Method method)
        throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        return invokeMethod(target.newInstance(), method, null);
    }

    public static Object invokeMethodOfClass(Class target, Method method, Object... args)
        throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        return invokeMethod(target.newInstance(), method, args);
    }

    public static Object tryToInvokeMethod(Object target, Method method, Object... args)
    {
        try
        {
            return invokeMethod(target, method, args);
        }
        catch (Exception e)
        {
            //do nothing - it's just a try
            return null;
        }
    }

    public static Object invokeMethod(Object target, Method method)
        throws InvocationTargetException, IllegalAccessException
    {
        return invokeMethod(target, method, null);
    }

    public static Object invokeMethod(Object target, Method method, Object... args)
        throws InvocationTargetException, IllegalAccessException
    {
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static Method tryToGetReadMethodManually(Class entity, String property)
    {
        property = property.substring(0, 1).toUpperCase() + property.substring(1);

        try
        {
            //changed to official bean spec. due to caching there is no performance issue any more
            return entity.getDeclaredMethod("is" + property);
        }
        catch (NoSuchMethodException e)
        {
            try
            {
                return entity.getDeclaredMethod("get" + property);
            }
            catch (NoSuchMethodException e1)
            {
                LOGGER.finest("method not found - class: " + entity.getName()
                        + " - methods: " + "get" + property + " " + "is" + property);

                return null;
            }
        }
    }
}
