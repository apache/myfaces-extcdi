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
package org.apache.myfaces.extensions.cdi.core.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.inject.Typed;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
@Typed()
@InvocationOrder(400)
public class PropertyFileResolver extends AbstractConfiguredValueResolver
{
    private static final String BASE_NAME = "org.apache.myfaces.extensions.cdi.";

    private static final String FILE_NAME = "myfaces-extcdi";

    private static Map<ClassLoader, Map<String, String>> propertyCache =
            new ConcurrentHashMap<ClassLoader, Map<String, String>>();

    /**
     * {@inheritDoc}
     */
    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        String key = "" + descriptor.getKey();

        if (!(String.class.isAssignableFrom(descriptor.getTargetType()) &&
                key.contains(".") && key.contains("_")))
        {
            return Collections.emptyList();
        }

        //TODO
        if (key.endsWith("_"))
        {
            key = key.substring(0, key.length() - 1);
        }

        String configKey = key.substring(key.indexOf(".") + 1);
        String configuredValue = null;

        Map<String, String> cache = getPropertyCache();

        configuredValue = cache.get(key);

        if(configuredValue == null)
        {
            configuredValue = cache.get(configKey);
        }

        if("".equals(configuredValue))
        {
            return Collections.emptyList();
        }

        if(configuredValue != null)
        {
            add(configuredValue);
        }
        else
        {

            String bundleName;

            //TODO
            if (key.contains("@") && key.lastIndexOf("@") < key.indexOf("."))
            {
                bundleName = key.substring(0, key.indexOf("."));
                bundleName = bundleName.replace("@", ".");
            }
            else
            {
                bundleName = BASE_NAME + key.substring(0, key.indexOf("."));
            }

            ResourceBundle resourceBundle;

            try
            {
                try
                {
                    resourceBundle = ResourceBundle
                            .getBundle(bundleName, Locale.getDefault(), ClassUtils.getClassLoader(null));
                }
                catch (MissingResourceException e)
                {
                    resourceBundle = null;
                }

                if (resourceBundle == null)
                {
                    try
                    {
                        resourceBundle = ResourceBundle.getBundle(FILE_NAME);
                    }
                    catch (MissingResourceException e2)
                    {
                        resourceBundle = null;
                    }
                }

                if (resourceBundle != null)
                {
                    try
                    {
                        configuredValue = resourceBundle.getString(configKey);
                        add(configuredValue);
                        cache.put(configKey, configuredValue);
                    }
                    catch (MissingResourceException e)
                    {
                        resourceBundle = null;
                    }
                }

                if (resourceBundle == null)
                {
                    Properties properties = getProperties("META-INF/" + FILE_NAME + ".properties");

                    if (properties != null)
                    {
                        configuredValue = properties.getProperty(configKey);
                        add(configuredValue);
                        cache.put(configKey, configuredValue);
                    }
                }

                if (configuredValue == null)
                {
                    cache.put(configKey, "");
                    return Collections.emptyList();
                }
            }
            catch (Exception e)
            {
                cache.put(configKey, "");
                return Collections.emptyList();
            }
        }

        return getConfiguredValues(descriptor.getTargetType());
    }

    private static Map<String, String> getPropertyCache()
    {
        ClassLoader classLoader = ClassUtils.getClassLoader(null);
        Map<String, String> cache = propertyCache.get(classLoader);

        if(cache == null)
        {
            cache = new ConcurrentHashMap<String, String>();
            propertyCache.put(classLoader, cache);
        }
        return cache;
    }

    /**
     * Load properties from a configuration file with the given resourceName.
     *
     * @param resourceName name of the resource
     * @return Properties or <code>null</code> if the given property file doesn't exist
     */
    //TODO
    private static Properties getProperties(String resourceName)
    {
        Properties properties = null;
        ClassLoader classLoader = ClassUtils.getClassLoader(resourceName);
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);
        if (inputStream != null)
        {
            properties = new Properties();
            try
            {
                properties.load(inputStream);
            }
            catch (IOException e)
            {
                return null;
            }
        }

        return properties;
    }
}
