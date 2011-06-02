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
package org.apache.myfaces.extensions.cdi.alternative.implementation;

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;
import org.apache.myfaces.extensions.cdi.core.impl.config.ServiceLoaderResolver;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Specializes;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class AlternativeImplementationExtension implements Extension
{
    protected <T> void vetoDefaultImplementation(@Observes ProcessAnnotatedType<T> processAnnotatedType)
    {
        Class<?> beanClass = processAnnotatedType.getAnnotatedType().getJavaClass();

        //only filter default implementations of codi which are beans and don't filter a
        //AlternativeImplementation provided by e.g. the codi alternative config module
        if (!beanClass.getName().startsWith("org.apache.myfaces.extensions.cdi.") ||
                beanClass.isAnnotationPresent(AlternativeImplementation.class) ||
                beanClass.isAnnotationPresent(Specializes.class) ||
                (beanClass.isAnnotationPresent(Typed.class) &&
                        beanClass.getAnnotation(Typed.class).value().length == 0))
        {
            return;
        }

        if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers()))
        {
            return;
        }

        final boolean configClassMode = CodiConfig.class.isAssignableFrom(beanClass);

        List<Class> spiClassCandidates = findSpiClassCandidates(beanClass);

        List customImplementations = new ArrayList();

        Object customImplementation = null;

        for (Class spiClass : spiClassCandidates)
        {
            if (Serializable.class.isAssignableFrom(beanClass))
            {
                if(!CodiConfig.class.getName().equals(spiClass.getName()))
                {
                    customImplementation = CodiUtils.lookupFromEnvironment(spiClass,
                            new ConfiguredClassAggregatable(spiClass, customImplementations, configClassMode));
                }
                else
                {
                    customImplementation = CodiUtils.lookupFromEnvironment(spiClass,
                            new ConfiguredClassAggregatable(beanClass, customImplementations, configClassMode));
                }
            }
            else
            {
                //noinspection unchecked
                customImplementation = tryToLoadCustomClassViaServiceLoader(spiClass);
            }

            if (customImplementation != null)
            {
                break;
            }
        }

        if (customImplementation != null)
        {
            //veto default implementation of codi
            processAnnotatedType.veto();
        }
    }

    private Object tryToLoadCustomClassViaServiceLoader(Class spiClass)
    {
        List customImplementations = new ServiceLoaderResolver()
                .resolveInstances(new ConfiguredClassDescriptor(spiClass));

        if(customImplementations.isEmpty())
        {
            return null;
        }

        if (customImplementations.size() > 1)
        {
            //TODO
            //here we have a spi which is used for other purposes
            return null;
        }

        return customImplementations.iterator().next();
    }

    private <T> List<Class> findSpiClassCandidates(Class<T> beanClass)
    {
        List<Class> result = new ArrayList<Class>();

        Class<?> currentClass = beanClass;
        while (currentClass != null && !Object.class.getName().equals(currentClass.getName()))
        {
            //only codi spis are supported
            if(currentClass.getName().startsWith("org.apache.myfaces.extensions.cdi."))
            {
                if ((Modifier.isAbstract(currentClass.getModifiers()) ||
                        Modifier.isInterface(currentClass.getModifiers())))
                {
                    result.add(currentClass);
                }
            }

            //scan interfaces
            for (Class interfaceClass : currentClass.getInterfaces())
            {
                result.addAll(findSpiClassCandidates(interfaceClass));
            }

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }
}
