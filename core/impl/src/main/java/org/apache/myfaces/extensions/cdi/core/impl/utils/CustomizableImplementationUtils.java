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
package org.apache.myfaces.extensions.cdi.core.impl.utils;

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.Custom;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.ClassDeactivation.isClassActivated;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.context.NormalScope;
import javax.inject.Singleton;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
public class CustomizableImplementationUtils
{
    private static final Custom CUSTOM = new CustomLiteral();

    private static Map<ClassLoader, Map<Class, Bean<?>>> beanCache
            = new ConcurrentHashMap<ClassLoader, Map<Class, Bean<?>>>();

    public static <T> T resolveCustomizableImplementation(Class<T> targetType)
    {
        Bean<T> foundBean = resolveCustomizableBean(targetType);

        if(foundBean == null)
        {
            return null;
        }
        return CodiUtils.getOrCreateScopedInstanceOfBean(foundBean);
    }

    public static <T> Bean<T> resolveCustomizableBean(Class<T> targetType)
    {
        Bean<T> bean = getCachedBean(targetType);

        if(bean != null)
        {
            return bean;
        }

        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

        Set<?> customBeans = beanManager.getBeans(targetType, CUSTOM);

        if (customBeans.isEmpty())
        {
            customBeans = getDefaultImplementation(beanManager, targetType);
        }

        if(customBeans.size() > 1)
        {
            for(Object foundBean : customBeans)
            {
                if(foundBean instanceof Bean)
                {
                    if(isClassActivated(((Bean)foundBean).getBeanClass()))
                    {
                        //noinspection unchecked
                        bean = (Bean<T>)foundBean;
                        break;
                    }
                }
            }
        }

        if (customBeans.size() == 1)
        {
            //noinspection unchecked
            bean = (Bean<T>) customBeans.iterator().next();
        }

        if(bean != null)
        {
            if(bean.getScope().isAnnotationPresent(NormalScope.class) || Singleton.class.equals(bean.getScope()))
            {
                cacheBean(targetType, bean);
            }
        }
        else
        {
            //TODO log warning
        }
        return bean;
    }

    public static <T> Bean<T> getCachedBean(Class<T> targetType)
    {
        Map<Class, Bean<?>> beanMap = beanCache.get(getClassLoader());

        if(beanMap == null)
        {
            return null;
        }
        //noinspection unchecked
        return (Bean<T>)beanMap.get(targetType);
    }

    private static <T> void cacheBean(Class<T> targetType, Bean<T> bean)
    {
        Map<Class, Bean<?>> beanMap = beanCache.get(getClassLoader());

        if(beanMap == null)
        {
            beanMap = new ConcurrentHashMap<Class, Bean<?>>();
            beanCache.put(getClassLoader(), beanMap);
        }
        beanMap.put(targetType, bean);
    }

    private static Set<Bean<?>> getDefaultImplementation(BeanManager beanManager, Class defaultImplementation)
    {
        return beanManager.getBeans(defaultImplementation);
    }

    private static ClassLoader getClassLoader()
    {
        return ClassUtils.getClassLoader(null);
    }
}
