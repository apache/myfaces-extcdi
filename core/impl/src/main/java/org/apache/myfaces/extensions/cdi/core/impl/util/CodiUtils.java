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
package org.apache.myfaces.extensions.cdi.core.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.Advanced;
import org.apache.myfaces.extensions.cdi.core.api.Aggregatable;
import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.Typed;
import javax.enterprise.util.Nonbinding;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Arrays;

/**
 * This is a collection of a few useful static helper functions.
 * <p/>
 */
@Typed()
public abstract class CodiUtils
{
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private CodiUtils()
    {
        // prevent instantiation
    }

    /**
     * Creates an instance for the given {@link Bean} and {@link CreationalContext}
     * @param creationalContext current context
     * @param bean current bean
     * @param <T> current type
     * @return created instance
     */
    public static <T> T createNewInstanceOfBean(CreationalContext<T> creationalContext, Bean<T> bean)
    {
        return bean.create(creationalContext);
    }

    /**
     * Creates a scoped instance (a proxy for normal scoped beans) for the given bean-name and class
     * @param beanManager current bean-manager
     * @param beanName name of the bean
     * @param targetClass class of the bean
     * @param <T> target type
     * @return created or resolved instance
     */
    public static <T> T getContextualReferenceByName(
            BeanManager beanManager, String beanName, Class<T> targetClass)
    {
        Set<Bean<?>> foundBeans = beanManager.getBeans(beanName);

        Bean<?> bean = beanManager.resolve(foundBeans); 

        //noinspection unchecked
        return (T) getContextualReference(beanManager, targetClass, bean);
    }

    /**
     * Creates a scoped instance (a proxy for normal scoped beans) for the given bean-class and qualifiers
     * @param targetClass class of the bean
     * @param qualifier optional qualifiers
     * @param <T> target type
     * @return created or resolved instance
     */
    public static <T> T getContextualReferenceByClass(Class<T> targetClass, Annotation... qualifier)
    {
        return getContextualReferenceByClass(BeanManagerProvider.getInstance().getBeanManager(),
                targetClass, qualifier);
    }

    /**
     * Creates a scoped instance (a proxy for normal scoped beans) for the given bean-class and qualifiers
     * @param beanManager current bean-manager
     * @param targetClass class of the bean
     * @param qualifier optional qualifiers
     * @param <T> target type
     * @return created or resolved instance
     */
    public static <T> T getContextualReferenceByClass(
            BeanManager beanManager, Class<T> targetClass, Annotation... qualifier)
    {
        return getContextualReferenceByClass(beanManager, targetClass, false, qualifier);
    }

    /**
     * Creates a scoped instance (a proxy for normal scoped beans) for the given bean-class and qualifiers.
     * Compared to the other util methods it allows optional beans.
     * @param targetClass class of the bean
     * @param optionalBeanAllowed flag which indicates if it's an optional bean
     * @param qualifier optional qualifiers
     * @param <T> target type
     * @return created or resolved instance if such a bean exists, null otherwise
     */
    public static <T> T getContextualReferenceByClass(Class<T> targetClass,
                                                      boolean optionalBeanAllowed,
                                                      Annotation... qualifier)
    {
        return getContextualReferenceByClass(BeanManagerProvider.getInstance().getBeanManager(),
                targetClass, optionalBeanAllowed, qualifier);
    }

    /**
     * Creates a scoped instance (a proxy for normal scoped beans) for the given bean-class and qualifiers.
     * Compared to the other util methods it allows optional beans.
     * @param beanManager current bean-manager
     * @param targetClass class of the bean
     * @param optionalBeanAllowed flag which indicates if it's an optional bean
     * @param qualifier optional qualifiers
     * @param <T> target type
     * @return created or resolved instance if such a bean exists, null otherwise
     */
    public static <T> T getContextualReferenceByClass(BeanManager beanManager,
                                                      Class<T> targetClass,
                                                      boolean optionalBeanAllowed,
                                                      Annotation... qualifier)
    {
        Bean<?> foundBean = getOrCreateBeanByClass(beanManager, targetClass, optionalBeanAllowed, qualifier);

        if(foundBean != null)
        {
            //noinspection unchecked
            return (T) getContextualReference(beanManager, targetClass, foundBean);
        }
        return null;
    }

    private static <T> Bean<T> getOrCreateBeanByClass(BeanManager beanManager,
                                                      Class<T> targetClass,
                                                      boolean optionalBeanAllowed,
                                                      Annotation... qualifier)
    {
        Set<? extends Bean> foundBeans = beanManager.getBeans(targetClass, qualifier);

        if(foundBeans.size() >= 1)
        {
            return (Bean<T>) beanManager.resolve((Set<Bean<? extends Object>>) foundBeans);
        }

        if(!optionalBeanAllowed)
        {
            throw new IllegalStateException("No bean found for type: " + targetClass.getName());
        }
        return null;
    }

    /**
     * Creates a scoped instance (a proxy for normal scoped beans) for the given bean-descriptor.
     *
     * @param beanManager current bean-manager
     * @param t type of the bean
     * @param bean bean-descriptor
     * @param <T> target type
     * @return created or resolved instance if such a bean exists, null otherwise
     */
    public static <T> T getContextualReference(BeanManager beanManager, Type t, Bean<T> bean)
    {
        CreationalContext<T> cc = beanManager.createCreationalContext(bean);
        return  (T) beanManager.getReference(bean, t, cc);
    }

    /**
     * Allows to perform dependency injection for instances which aren't managed by CDI
     * @param instance current instance
     * @param <T> current type
     * @return instance with injected fields (if possible)
     */
    public static <T> T injectFields(T instance)
    {
        CodiCoreConfig codiCoreConfig = getContextualReferenceByClass(CodiCoreConfig.class);

        return injectFields(instance, codiCoreConfig.isAdvancedQualifierRequiredForDependencyInjection());
    }

    /**
     * Allows to perform dependency injection for instances which aren't managed by CDI
     * @param instance current instance
     * @param requiresAdvancedQualifier flag which indicates if an instance has to be annotated with {@link Advanced}
     * to be eligible for dependency injection.
     * @param <T> current type
     * @return instance with injected fields (if possible)
     */
    public static <T> T injectFields(T instance, boolean requiresAdvancedQualifier)
    {
        if(instance == null)
        {
            return null;
        }

        if(requiresAdvancedQualifier && instance.getClass().isAnnotationPresent(Advanced.class))
        {
            return tryToInjectFields(instance);
        }
        else if(!requiresAdvancedQualifier)
        {
            return tryToInjectFields(instance);
        }
        return instance;
    }

    /**
     * Performes dependency injection for objects which aren't know as bean
     *
     * @param instance the target instance
     * @param <T> generic type
     * @return an instance produced by the {@link javax.enterprise.inject.spi.BeanManager} or
     * a manually injected instance (or null if the given instance is null)
     */
    @SuppressWarnings({"unchecked"})
    private static <T> T tryToInjectFields(T instance)
    {
        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

        T foundBean = (T) getContextualReferenceByClass(beanManager, instance.getClass(), true);

        if(foundBean != null)
        {
            return foundBean;
        }

        CreationalContext creationalContext = beanManager.createCreationalContext(null);

        AnnotatedType annotatedType = beanManager.createAnnotatedType(instance.getClass());
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(instance, creationalContext);
        return instance;
    }

    /**
     * Checks if the given qualifiers are equal.
     *
     * Qualifiers are equal if they have the same annotationType and all their
     * methods, except those annotated with @Nonbinding, return the same value.
     *
     * @param qualifier1 first qualifier
     * @param qualifier2 second qualifier
     * @return true if both qualifiers are equal, false otherwise
     */
    public static boolean isQualifierEqual(Annotation qualifier1, Annotation qualifier2)
    {
        Class<? extends Annotation> qualifier1AnnotationType = qualifier1.annotationType();

        // check if the annotationTypes are equal
        if (qualifier1AnnotationType == null || !qualifier1AnnotationType.equals(qualifier2.annotationType()))
        {
            return false;
        }

        // check the values of all qualifier-methods
        // except those annotated with @Nonbinding
        List<Method> bindingQualifierMethods = getBindingQualifierMethods(qualifier1AnnotationType);

        for (Method method : bindingQualifierMethods)
        {
            Object value1 = callMethod(qualifier1, method);
            Object value2 = callMethod(qualifier2, method);

            if (!checkEquality(value1, value2))
            {
                return false;
            }
        }

        return true;
    }

    private static boolean checkEquality(Object value1, Object value2)
    {
        if ((value1 == null && value2 != null) || (value1 != null && value2 == null))
        {
            return false;
        }

        if (value1 == null && value2 == null)
        {
            return true;
        }

        // now both values are != null

        Class<?> valueClass = value1.getClass();
        
        if (!valueClass.equals(value2.getClass()))
        {
            return false;
        }

        if (valueClass.isPrimitive())
        {
            // primitive types can be checked with ==
            return value1 == value2;
        }
        else if (valueClass.isArray())
        {
            Class<?> arrayType = valueClass.getComponentType();

            if (arrayType.isPrimitive())
            {
                if (Long.TYPE == arrayType)
                {
                    return Arrays.equals(((long[]) value1), (long[]) value2);
                }
                else if (Integer.TYPE == arrayType)
                {
                    return Arrays.equals(((int[]) value1), (int[]) value2);
                }
                else if (Short.TYPE == arrayType)
                {
                    return Arrays.equals(((short[]) value1), (short[]) value2);
                }
                else if (Double.TYPE == arrayType)
                {
                    return Arrays.equals(((double[]) value1), (double[]) value2);
                }
                else if (Float.TYPE == arrayType)
                {
                    return Arrays.equals(((float[]) value1), (float[]) value2);
                }
                else if (Boolean.TYPE == arrayType)
                {
                    return Arrays.equals(((boolean[]) value1), (boolean[]) value2);
                }
                else if (Byte.TYPE == arrayType)
                {
                    return Arrays.equals(((byte[]) value1), (byte[]) value2);
                }
                else if (Character.TYPE == arrayType)
                {
                    return Arrays.equals(((char[]) value1), (char[]) value2);
                }
                return false;
            }
            else
            {
                return Arrays.equals(((Object[]) value1), (Object[]) value2);
            }
        }
        else
        {
            return value1.equals(value2);
        }
    }

    /**
     * Calls the given method on the given instance.
     * Used to determine the values of annotation instances.
     *
     * @param instance current instance
     * @param method method which should be invoked
     * @return result of the called method
     */
    private static Object callMethod(Object instance, Method method)
    {
        boolean accessible = method.isAccessible();

        try
        {
            if (!accessible)
            {
                method.setAccessible(true);
            }

            return method.invoke(instance, EMPTY_OBJECT_ARRAY);
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new UnhandledException("Exception in method call : " + method.getName(), e);
        }
        finally
        {
            // reset accessible value
            method.setAccessible(accessible);
        }
    }

    /**
     * Return a List of all methods of the qualifier,
     * which are not annotated with {@link Nonbinding}.
     * 
     * @param qualifierAnnotationType annotation class which has to be inspected
     * @return methods which aren't annotated with Nonbinding
     */
    private static List<Method> getBindingQualifierMethods(Class<? extends Annotation> qualifierAnnotationType)
    {
        Method[] qualifierMethods = qualifierAnnotationType.getDeclaredMethods();
        if (qualifierMethods.length > 0)
        {
            List<Method> bindingMethods = new ArrayList<Method>();

            for (Method qualifierMethod : qualifierMethods)
            {
                Annotation[] qualifierMethodAnnotations = qualifierMethod.getDeclaredAnnotations();

                if (qualifierMethodAnnotations.length > 0)
                {
                    // look for @Nonbinding
                    boolean nonbinding = false;

                    for (Annotation qualifierMethodAnnotation : qualifierMethodAnnotations)
                    {
                        if (Nonbinding.class.equals(qualifierMethodAnnotation.annotationType()))
                        {
                            nonbinding = true;
                            break;
                        }
                    }

                    if (!nonbinding)
                    {
                        // no @Nonbinding found - add to list
                        bindingMethods.add(qualifierMethod);
                    }
                }
                else
                {
                    // no method-annotations - add to list
                    bindingMethods.add(qualifierMethod);
                }
            }

            return bindingMethods;
        }

        // annotation has no methods
        return Collections.emptyList();
    }

    /**
     * Resolves resources outside of CDI for the given class.
     * @param targetType target type
     * @param defaultImplementation default implementation
     * @param <T> current type
     * @return configured artifact or null if there is no result
     */
    public static <T extends Serializable> T lookupFromEnvironment(Class<T> targetType, T... defaultImplementation)
    {
        return lookupFromEnvironment(targetType, null, defaultImplementation);
    }

    /**
     * Resolves resources outside of CDI for the given class.
     * @param targetType target type which is also used as key (the simple name of it)
     * @param aggregatable allows to aggregate multiple results
     * @param defaultImplementation default implementation
     * @param <T> current type
     * @return configured artifact or an aggregated instance if there are multiple results or null if there is no result
     */
    public static <T extends Serializable> T lookupFromEnvironment(Class<T> targetType,
                                                                   Aggregatable<T> aggregatable,
                                                                   T... defaultImplementation)
    {
        return lookupFromEnvironment(targetType.getSimpleName(), targetType, aggregatable, defaultImplementation);
    }

    /**
     * Resolves resources outside of CDI for the given key and class.
     * @param key key for identifying the resource which has to be resolved
     * @param targetType target type
     * @param defaultImplementation default implementation
     * @param <T> current type
     * @return configured artifact or null if there is no result
     */
    public static <T extends Serializable> T lookupFromEnvironment(String key,
                                                                   Class<T> targetType,
                                                                   T... defaultImplementation)
    {
        return lookupFromEnvironment(key, targetType, null, defaultImplementation);
    }

    public static <T extends Serializable> T lookupConfigFromEnvironment(String key,
                                                                         Class<T> targetType,
                                                                         T defaultValue)
    {
        if(key == null)
        {
            @SuppressWarnings({"ThrowableInstanceNeverThrown"})
            RuntimeException runtimeException = new RuntimeException();

            String baseKey = runtimeException.getStackTrace()[1].getMethodName();

            if(baseKey.startsWith("get"))
            {
                baseKey = baseKey.substring(3);
            }
            else if(baseKey.startsWith("is"))
            {
                baseKey = baseKey.substring(2);
            }

            baseKey = baseKey.substring(0, 1).toLowerCase() + baseKey.substring(1);

            StringBuilder dynamicKey = new StringBuilder(baseKey.length());

            Character current;
            for(int i = 0; i < baseKey.length(); i++)
            {
                current = baseKey.charAt(i);
                if(Character.isUpperCase(current))
                {
                    dynamicKey.append("_");
                    dynamicKey.append(Character.toLowerCase(current));
                }
                else
                {
                    dynamicKey.append(current);
                }
            }

            String className = runtimeException.getStackTrace()[1].getClassName();

            Class configClass = ClassUtils.tryToLoadClassForName(className);

            if(configClass != null && CodiConfig.class.isAssignableFrom(configClass.getSuperclass()))
            {
                //config class extends the default impl. -> use the name of the default impl.
                className = configClass.getSuperclass().getSimpleName();
            }
            else
            {
                className = className.substring(className.lastIndexOf(".") + 1);
            }

            key = className + "." + dynamicKey.toString();
        }

        String result = lookupFromEnvironment(key, String.class, null, null);

        if(result == null)
        {
            return defaultValue != null ? defaultValue : null;
        }

        if(String.class.isAssignableFrom(targetType))
        {
            return targetType.cast(result);
        }
        if(Boolean.class.isAssignableFrom(targetType))
        {
            return targetType.cast(Boolean.parseBoolean(result));
        }
        if(Integer.class.isAssignableFrom(targetType))
        {
            return targetType.cast(Integer.parseInt(result));
        }

        throw new IllegalArgumentException(targetType.getName() + " isn't supported");
    }

    /**
     * Resolves resources outside of CDI for the given key and class.
     * @param key key for identifying the resource which has to be resolved
     * @param targetType target type
     * @param aggregatable allows to aggregate multiple results
     * @param defaultImplementation default implementation
     * @param <T> current type
     * @return configured artifact or an aggregated instance if there are multiple results or null if there is no result
     */
    public static <T extends Serializable> T lookupFromEnvironment(String key,
                                                                   Class<T> targetType,
                                                                   Aggregatable<T> aggregatable,
                                                                   T... defaultImplementation)
    {
        checkDefaultImplementation(defaultImplementation);

        List<T> results = ConfiguredArtifactUtils.getCachedArtifact(key, targetType);

        if(results == null)
        {
            T defaultInstance = null;

            if(defaultImplementation != null && defaultImplementation.length == 1)
            {
                //only one is supported
                defaultInstance = defaultImplementation[0];
            }
            results = ConfiguredArtifactUtils
                    .resolveFromEnvironment(key, targetType, aggregatable != null, defaultInstance);

            if(String.class.isAssignableFrom(targetType))
            {
                ConfiguredArtifactUtils.processConfiguredArtifact(key, (List<String>)results);
            }
            else
            {
                ConfiguredArtifactUtils.processFoundArtifact(key, targetType, results);
            }
        }

        if(results.isEmpty())
        {
            return null;
        }

        if(aggregatable != null)
        {
            for(T currentEntry : results)
            {
                aggregatable.add(currentEntry);
            }
            return aggregatable.create();
        }
        else
        {
            return results.iterator().next();
        }
    }

    private static void checkDefaultImplementation(Object[] defaultImplementation)
    {
        if(defaultImplementation != null && defaultImplementation.length > 1)
        {
            StringBuilder foundDefaultImplementations = new StringBuilder();

            for(Object o : defaultImplementation)
            {
                foundDefaultImplementations.append(o.getClass()).append("\n");
            }
            throw new IllegalStateException(defaultImplementation.length + " default implementations are provided. " +
                    "CodiUtils#lookupFromEnvironment only allows one default implementation. Found implementations: " +
                    foundDefaultImplementations.toString());
        }
    }

    /**
     * Checks if CDI is up and running
     * @return true if CDI was bootstrapped, false otherwise
     */
    public static boolean isCdiInitialized()
    {
        return BeanManagerProvider.isActive();
    }
}
