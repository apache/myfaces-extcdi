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

import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;

/**
 * @author Gerhard Petracek
 */
@Alternative
public class AlternativeCodiCoreConfigProxy extends CodiCoreConfig
{
    private static final long serialVersionUID = -1471628272055334671L;

    private CodiCoreConfig wrapped;

    @PostConstruct
    protected void init()
    {
        this.wrapped = CodiUtils.lookupAlternativeConfig(CodiCoreConfig.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdvancedQualifierRequiredForDependencyInjection()
    {
        return wrapped.isAdvancedQualifierRequiredForDependencyInjection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConfigurationLoggingEnabled()
    {
        return wrapped.isConfigurationLoggingEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvalidBeanCreationEventEnabled()
    {
        return wrapped.isInvalidBeanCreationEventEnabled();
    }
}
