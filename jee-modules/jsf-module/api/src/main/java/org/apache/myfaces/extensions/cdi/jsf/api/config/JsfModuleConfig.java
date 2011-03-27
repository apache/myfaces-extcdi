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
package org.apache.myfaces.extensions.cdi.jsf.api.config;

import org.apache.myfaces.extensions.cdi.core.api.config.ConfigEntry;
import org.apache.myfaces.extensions.cdi.core.api.config.AbstractAttributeAware;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;

import javax.enterprise.context.ApplicationScoped;

/**
 * Config for all JSF specific configurations.
 *
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class JsfModuleConfig extends AbstractAttributeAware implements CodiConfig
{
    private static final long serialVersionUID = 595393008764879504L;

    protected JsfModuleConfig()
    {
    }

    /**
     * If the initial redirect is enabled, a redirect will be performed for adding the current window-id to the url.
     *
     * @return true for activating it, false otherwise
     */
    @ConfigEntry
    public boolean isInitialRedirectEnabled()
    {
        return true;
    }

    /**
     * esp useful for JSF 2.0
     * @return true for creating additional navigation-cases based on view configs for
     * ConfigurableNavigationHandler#getNavigationCases
     */
    @ConfigEntry
    public boolean isUseViewConfigsAsNavigationCasesEnabled()
    {
        return true;
    }

    /**
     * Allows to use e.g.:
     * "Validation error: {invalidValue} isn't a valid value."
     * @return true if the InvalidValueAwareMessageInterpolator should be used, false otherwise
     */
    @ConfigEntry
    public boolean isInvalidValueAwareMessageInterpolatorEnabled()
    {
        return true;
    }

    /**
     * Per default all faces-messages are preserved for the next rendering process
     * @return true if the messages should be preserved automatically, false otherwise
     */
    @ConfigEntry
    public boolean isAlwaysKeepMessages()
    {
        return true;
    }
}
