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
package org.apache.myfaces.extensions.cdi.core.api.tools;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>A small helper class to create an Annotation instance of the given annotation class
 * via {@link java.lang.reflect.Proxy}. The annotation literal gets filled with the default values.</p>
 * <p/>
 * <p>usage:</p>
 * <pre>
 * Class<? extends annotation> annotationClass = ...;
 * Annotation a = DefaultAnnotation.of(annotationClass)
 * </pre>
 *
 */
public class DefaultAnnotation implements Annotation, InvocationHandler, Serializable
{
    private static final long serialVersionUID = -2345068201195886173L;
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private static volatile Map<Class<? extends Annotation>, Annotation> annotationCache
            = new ConcurrentHashMap<Class<? extends Annotation>, Annotation>();

    public static <T extends Annotation> T of(Class<T> annotationClass)
    {
        Annotation annotation = annotationCache.get(annotationClass);

        if (annotation == null)
        {
            // switch into paranoia mode
            synchronized (annotationCache)
            {
                annotation = annotationCache.get(annotationClass);
                if (annotation == null)
                {
                    annotation = (Annotation) Proxy.newProxyInstance(
                            annotationClass.getClassLoader(),
                            new Class[]{annotationClass},
                            new DefaultAnnotation(annotationClass));
                    
                    annotationCache.put(annotationClass, annotation);
                }
            }
        }

        return (T)annotation;
    }

    private Class<? extends Annotation> annotationClass;

    /**
     * Required to use the result of the factory instead of a default implementation
     * of {@link javax.enterprise.util.AnnotationLiteral}.
     *
     * @param annotationClass class of the target annotation
     */
    private DefaultAnnotation(Class<? extends Annotation> annotationClass)
    {
        this.annotationClass = annotationClass;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if ("hashCode".equals(method.getName()))
        {
            return hashCode();
        }
        else if ("equals".equals(method.getName()))
        {
            return equals(args[0]);
        }
        else if ("annotationType".equals(method.getName()))
        {
            return annotationType();
        }
        else if ("toString".equals(method.getName()))
        {
            return toString();
        }

        return method.getDefaultValue();
    }

    public Class<? extends Annotation> annotationType()
    {
        return annotationClass;
    }

    /**
     * Copied from javax.enterprise.util.AnnotationLiteral#toString()
     * with minor changes. 
     *
     * @return
     */
    @Override
    public String toString()
    {
        Method[] methods = this.annotationClass.getDeclaredMethods();

        StringBuilder sb = new StringBuilder("@" + annotationType().getName() + "(");
        int length = methods.length;

        for (int i = 0; i < length; i++)
        {
            // Member name
            sb.append(methods[i].getName()).append("=");

            // Member value
            Object memberValue;
            try
            {
                memberValue = invoke(this, methods[i], EMPTY_OBJECT_ARRAY);
            }
            catch (Throwable throwable)
            {
                memberValue = "";
            }
            sb.append(memberValue);

            if (i < length - 1)
            {
                sb.append(",");
            }
        }

        sb.append(")");

        return sb.toString();
    }

    //don't change these methods!

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DefaultAnnotation))
        {
            return false;
        }

        DefaultAnnotation that = (DefaultAnnotation) o;

        if (!annotationClass.equals(that.annotationClass))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return annotationClass.hashCode();
    }

}
