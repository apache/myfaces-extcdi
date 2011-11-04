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
package org.apache.myfaces.extensions.cdi.core.impl.resource.bundle;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.resource.bundle.Bundle;
import org.apache.myfaces.extensions.cdi.core.api.resource.bundle.BundleKey;
import org.apache.myfaces.extensions.cdi.core.api.resource.bundle.BundleValue;
import org.apache.myfaces.extensions.cdi.core.api.resource.bundle.ResourceBundle;
import org.apache.myfaces.extensions.cdi.core.api.util.ConfigUtils;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.StringUtils;

import javax.enterprise.inject.Typed;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation for type-safe {@link ResourceBundle}
 */
@Typed()
class DefaultResourceBundle implements ResourceBundle
{
    private static final long serialVersionUID = 117890966460274247L;

    private String bundleName;
    private Locale locale;

    /**
     * {@inheritDoc}
     */
    public ResourceBundle useBundle(String name)
    {
        this.bundleName = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceBundle useBundle(Class<?> bundleClass)
    {
        Bundle bundleName = bundleClass.getAnnotation(Bundle.class);
        if(bundleName != null)
        {
            this.bundleName = bundleName.name();
        }

        if(this.bundleName == null || "".equals(this.bundleName))
        {
            String className = bundleClass.getSimpleName();
            className = className.substring(0, 1).toLowerCase() + className.substring(1);
            className = StringUtils.replaceUpperCaseCharactersWithUnderscores(className);
            this.bundleName = bundleClass.getPackage().getName() + "." + className;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceBundle useLocale(Locale locale)
    {
        this.locale = locale;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue(Class<? extends BundleKey> key)
    {
        //in case of an inner class
        if("".equals(key.getSimpleName()) && BundleKey.class.isAssignableFrom(key.getSuperclass()))
        {
            key = (Class<? extends BundleKey>) key.getSuperclass();
        }

        Named named = key.getAnnotation(Named.class);

        String resourceKey = null;

        if(named != null)
        {
            resourceKey = named.value();
        }

        if(resourceKey == null)
        {
            resourceKey = StringUtils.replaceUpperCaseCharactersWithUnderscores(
                    key.getSimpleName().substring(0, 1).toLowerCase() + key.getSimpleName().substring(1));
        }

        Class<?> bundleClass = key.getSuperclass();
        if(this.bundleName == null &&
                !Object.class.getName().equals(bundleClass.getName()) &&
                !BundleValue.class.isAssignableFrom(bundleClass))
        {
            useBundle(bundleClass);
        }
        else
        {
            List<Class> bundleClassCandidates = new ArrayList<Class>();

            for(Class interfaceClass : key.getInterfaces())
            {
                if(interfaceClass.isAnnotationPresent(Bundle.class))
                {
                    useBundle(interfaceClass);
                    break;
                }
                if(!BundleKey.class.isAssignableFrom(interfaceClass))
                {
                    bundleClassCandidates.add(interfaceClass);
                }
            }

            if(this.bundleName == null && bundleClassCandidates.size() == 1)
            {
                useBundle(bundleClassCandidates.iterator().next());
            }
            else if(this.bundleName == null && bundleClassCandidates.size() > 1)
            {
                throw new IllegalStateException(key.getName() + " implements multiple custom interfaces and " +
                        "non of them is annotated with @" + Bundle.class);
            }
        }
        return getValue(resourceKey);
    }

    /**
     * {@inheritDoc}
     */
    public String getValue(String key)
    {
        if(key == null)
        {
            return null;
        }

        if(this.locale == null)
        {
            this.locale = Locale.getDefault();
        }

        if(this.bundleName == null)
        {
            if(ProjectStageProducer.getInstance().getProjectStage() == ProjectStage.Development)
            {
                Logger logger = Logger.getLogger(DefaultResourceBundle.class.getName());
                if(logger.isLoggable(Level.WARNING))
                {
                    logger.warning("no custom bundle name provided - the codi properties file " +
                            "META-INF/myfaces-extcdi.properties is used as fallback");
                }
            }
            this.bundleName = "META-INF/myfaces-extcdi.properties";
        }

        if(this.bundleName.contains("/"))
        {
            Properties properties = ConfigUtils.getProperties(this.bundleName);

            if(properties == null)
            {
                return null;
            }
            return properties.getProperty(key);
        }
        try
        {
            if(this.locale == null)
            {
                return java.util.ResourceBundle.getBundle(this.bundleName).getString(key);
            }
            return java.util.ResourceBundle.getBundle(this.bundleName, this.locale).getString(key);
        }
        catch (MissingResourceException e)
        {
            return null;
        }
    }
}
