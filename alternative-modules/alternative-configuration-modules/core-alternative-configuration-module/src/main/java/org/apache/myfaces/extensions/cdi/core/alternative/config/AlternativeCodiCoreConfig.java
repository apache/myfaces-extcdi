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
package org.apache.myfaces.extensions.cdi.core.alternative.config;

import org.apache.myfaces.extensions.cdi.alternative.implementation.api.AlternativeImplementation;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
@AlternativeImplementation
public class AlternativeCodiCoreConfig extends CodiCoreConfig
{
    private static final long serialVersionUID = -1471628272055334673L;

    /**
     * Logs the activation of the config
     */
    @PostConstruct
    protected void init()
    {
        Class configClass = AlternativeCodiCoreConfig.class; //don't use getClass - would lead to a proxy

        String moduleVersion = detectModuleVersion(configClass);

        StringBuilder info = new StringBuilder("[Started] MyFaces CODI (Extensions CDI) alternative config ");
        info.append(configClass.getName());
        info.append(" is active (");
        info.append(moduleVersion);
        info.append(")");
        info.append(System.getProperty("line.separator"));

        Logger logger = Logger.getLogger(configClass.getName());

        if(logger.isLoggable(Level.INFO))
        {
            logger.info(info.toString());
        }
    }

    private String detectModuleVersion(Class configClass)
    {
        String version = ClassUtils.getJarVersion(configClass);

        if(version != null && !version.startsWith("null"))
        {
            return "v" + version;
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdvancedQualifierRequiredForDependencyInjection()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                super.isAdvancedQualifierRequiredForDependencyInjection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigurationLoggingEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                super.isConfigurationLoggingEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvalidBeanCreationEventEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                super.isInvalidBeanCreationEventEnabled());
    }
}
