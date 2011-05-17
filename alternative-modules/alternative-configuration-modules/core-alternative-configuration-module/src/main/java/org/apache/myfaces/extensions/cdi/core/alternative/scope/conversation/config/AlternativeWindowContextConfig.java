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
package org.apache.myfaces.extensions.cdi.core.alternative.scope.conversation.config;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.inject.Typed;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class AlternativeWindowContextConfig extends WindowContextConfig
{
    private static final long serialVersionUID = 8616591700809645827L;

    /**
     * Logs the activation of the config
     */
    public AlternativeWindowContextConfig()
    {
        Class configClass = AlternativeWindowContextConfig.class; //don't use getClass - would lead to a proxy

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
    public boolean isUrlParameterSupported()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                super.isUrlParameterSupported());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnknownWindowIdsAllowed()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                        super.isUnknownWindowIdsAllowed());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAddWindowIdToActionUrlsEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                        super.isAddWindowIdToActionUrlsEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWindowContextTimeoutInMinutes()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Integer.class,
                        super.getWindowContextTimeoutInMinutes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxWindowContextCount()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Integer.class,
                        super.getMaxWindowContextCount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloseEmptyWindowContextsEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                        super.isCloseEmptyWindowContextsEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEagerWindowContextDetectionEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                        super.isEagerWindowContextDetectionEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCreateWindowContextEventEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                        super.isCreateWindowContextEventEnabled());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCloseWindowContextEventEnabled()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, Boolean.class,
                        super.isCloseWindowContextEventEnabled());
    }
}
