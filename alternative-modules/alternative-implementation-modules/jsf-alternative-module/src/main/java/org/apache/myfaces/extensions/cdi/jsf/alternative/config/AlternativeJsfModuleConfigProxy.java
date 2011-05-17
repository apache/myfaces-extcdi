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
package org.apache.myfaces.extensions.cdi.jsf.alternative.config;

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.JsfModuleConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;

/**
 * @author Gerhard Petracek
 */
@Alternative
public class AlternativeJsfModuleConfigProxy extends JsfModuleConfig
{
    private static final long serialVersionUID = 2385134740850201121L;

    private JsfModuleConfig wrapped;

    @PostConstruct
    protected void init()
    {
        this.wrapped = CodiUtils.lookupAlternativeConfig(JsfModuleConfig.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInitialRedirectEnabled()
    {
        return wrapped.isInitialRedirectEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseViewConfigsAsNavigationCasesEnabled()
    {
        return wrapped.isUseViewConfigsAsNavigationCasesEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvalidValueAwareMessageInterpolatorEnabled()
    {
        return wrapped.isInvalidValueAwareMessageInterpolatorEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAlwaysKeepMessages()
    {
        return wrapped.isAlwaysKeepMessages();
    }
}
