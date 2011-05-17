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
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.api.tools.InvocationOrderComparator;

import javax.enterprise.inject.Typed;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ServiceLoader;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
@Typed()
public abstract class ConfiguredArtifactUtils
{
    private static Map<ClassLoader, Map<ArtifactCacheKey<String>, Set<Serializable>>> apiToImplCache
            = new ConcurrentHashMap<ClassLoader, Map<ArtifactCacheKey<String>, Set<Serializable>>>();

    private static Map<ClassLoader, Map<String, Set<String>>> configuredValueCache
            = new ConcurrentHashMap<ClassLoader, Map<String, Set<String>>>();

    //just for testing
    protected ConfiguredArtifactUtils()
    {
        // prevent instantiation
    }

    protected void reset()
    {
        apiToImplCache.clear();
        configuredValueCache.clear();
    }

    static <T extends Serializable> List<T> getCachedArtifact(String key, Class<T> targetClass)
    {
        ClassLoader classLoader = ClassUtils.getClassLoader(null);
        if(String.class.isAssignableFrom(targetClass))
        {
            Map<String, Set<String>> cachedValueMap = configuredValueCache.get(classLoader);

            if(cachedValueMap != null)
            {
                List<String> result = new ArrayList<String>();
                Set<String> cachedValues = cachedValueMap.get(key);
                if (cachedValues != null && !cachedValues.isEmpty())
                {
                    result.addAll(cachedValues);
                }
                if(!result.isEmpty())
                {
                    return (List<T>)result;
                }
            }
            return null;
        }

        Map<ArtifactCacheKey<String>, Set<Serializable>> artifactCache = apiToImplCache.get(classLoader);

        if(artifactCache == null)
        {
            return null;
        }

        List<T> result = new ArrayList<T>();

        Set<T> cachedInstances = (Set<T>)artifactCache.get(new ArtifactCacheKey<String>(key, targetClass));

        if(cachedInstances == null)
        {
            return null;
        }
        for(T currentClass : cachedInstances)
        {
            result.add(currentClass);
        }
        Collections.sort(result, new InvocationOrderComparator<T>());
        return result;
    }

    static <T> List<T> resolveFromEnvironment(final String key,
                                              final Class<T> targetType,
                                              boolean supportOfMultipleArtifacts,
                                              T defaultImplementation)
    {
        List<T> results = new ArrayList<T>();
        List<T> resolverResult = null;

        List<ConfiguredValueResolver> resolvers = getConfiguredValueResolvers();

        for(ConfiguredValueResolver configuredValueResolver : resolvers)
        {
            if(configuredValueResolver.isActivated())
            {
                resolverResult = configuredValueResolver.resolveInstances(new ConfiguredValueDescriptor<String, T>()
                {
                    /**
                     * {@inheritDoc}
                     */
                    public String getKey()
                    {
                        return key;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    public Class<T> getTargetType()
                    {
                        return targetType;
                    }
                });
            }
            else
            {
                resolverResult = null;
            }

            Field defaultInjectionPoint;
            if(resolverResult != null && !resolverResult.isEmpty())
            {
                for(T currentResult : resolverResult)
                {
                    if(defaultImplementation != null && currentResult.getClass().isAnnotationPresent(Advanced.class))
                    {
                        defaultInjectionPoint = findInjectionPointForDefaultImplementation(currentResult, targetType);

                        if(defaultInjectionPoint != null)
                        {
                            try
                            {
                                //TODO
                                defaultInjectionPoint.setAccessible(true);
                                defaultInjectionPoint.set(currentResult, defaultImplementation);
                            }
                            catch (Exception e)
                            {
                                Logger logger = Logger.getLogger(ConfiguredArtifactUtils.class.getName());

                                if(logger.isLoggable(Level.SEVERE))
                                {
                                    logger.log(Level.SEVERE, currentResult.getClass().getName() + " is annotated with" +
                                            Advanced.class.getName() + " but it wasn't possible to inject the default" +
                                            " implementation into " + defaultInjectionPoint.getName() + "." +
                                            " Please contact the community or remove the annotation.", e);

                                }
                            }
                        }
                    }
                    results.add(currentResult);
                }
            }
        }

        checkArtifacts(targetType, results, supportOfMultipleArtifacts);

        return results;
    }

    private static <T> Field findInjectionPointForDefaultImplementation(T instance, Class<T> targetType)
    {
        Class currentParamClass = instance.getClass();
        while (currentParamClass != null && !Object.class.getName().equals(currentParamClass.getName()))
        {
            for(Field currentField : currentParamClass.getDeclaredFields())
            {
                if(currentField.getName().endsWith("default" + targetType.getSimpleName()) &&
                        targetType.isAssignableFrom(currentField.getType()))
                {
                    return currentField;
                }
            }
            currentParamClass = currentParamClass.getSuperclass();
        }

        return null;
    }

    private static List<ConfiguredValueResolver> getConfiguredValueResolvers()
    {
        ServiceLoader<ConfiguredValueResolver> configuredValueResolvers =
                ServiceLoader.load(ConfiguredValueResolver.class, ClassUtils.getClassLoader(null));

        List<ConfiguredValueResolver> resolvers = new ArrayList<ConfiguredValueResolver>();
        Comparator<ConfiguredValueResolver> comparator = new InvocationOrderComparator<ConfiguredValueResolver>();

        //TODO cache the resolvers
        for(ConfiguredValueResolver currentResolver : configuredValueResolvers)
        {
            resolvers.add(currentResolver);
        }

        Collections.sort(resolvers, comparator);
        return resolvers;
    }

    static void processConfiguredArtifact(String key, List<String> results)
    {
        processFoundArtifact(key, String.class, results);
    }

    static <T extends Serializable> void processFoundArtifact(String key, Class<T> targetType, List<T> artifacts)
    {
        for(T currentArtifact : artifacts)
        {
            cacheArtifact(key, currentArtifact);
        }
    }

    private static <T extends Serializable> void cacheArtifact(String key, T artifact)
    {
        ClassLoader classLoader = ClassUtils.getClassLoader(null);

        if(String.class.isAssignableFrom(artifact.getClass()))
        {
            Map<String, Set<String>> configuredValueMapping = configuredValueCache.get(classLoader);

            if(configuredValueMapping == null)
            {
                configuredValueMapping = new HashMap<String, Set<String>>();
                configuredValueCache.put(classLoader, configuredValueMapping);
            }

            Set<String> configuredValues = configuredValueMapping.get(key);

            if(configuredValues == null)
            {
                configuredValues = new HashSet<String>();
                configuredValueMapping.put(key, configuredValues);
            }
            configuredValues.add((String)artifact);
        }
        else
        {
            Map<ArtifactCacheKey<String>, Set<Serializable>> configuredValueMapping = apiToImplCache.get(classLoader);

            if(configuredValueMapping == null)
            {
                configuredValueMapping = new HashMap<ArtifactCacheKey<String>, Set<Serializable>>();
                apiToImplCache.put(classLoader, configuredValueMapping);
            }

            ArtifactCacheKey<String> cacheKey = new ArtifactCacheKey<String>(key, artifact.getClass());
            Set<Serializable> configuredValues = configuredValueMapping.get(cacheKey);

            if(configuredValues == null)
            {
                configuredValues = new HashSet<Serializable>();
                configuredValueMapping.put(cacheKey, configuredValues);
            }
            configuredValues.add(artifact);
        }
    }

    private static <T> void checkArtifacts(Class<T> targetType, List<T> results, boolean supportOfMultipleArtifacts)
    {
        if(!supportOfMultipleArtifacts && results.size() > 1)
        {
            //TODO move to exception utils
            StringBuilder message = new StringBuilder("Multiple implementations for ");
            message.append(targetType.getName());
            message.append(" aren't allowed. Found implementations: \n");

            for(T artifacts : results)
            {
                message.append(artifacts.getClass().getName());
                message.append("\n");
            }

            throw new IllegalStateException(message.toString());
        }
    }
}
