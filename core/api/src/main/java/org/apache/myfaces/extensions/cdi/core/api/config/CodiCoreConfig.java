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
package org.apache.myfaces.extensions.cdi.core.api.config;

import javax.enterprise.context.ApplicationScoped;

/**
 * Configuration for the core of CODI - it's customizable via the @Alternative or @Specializes mechanism of CDI.
 *
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class CodiCoreConfig extends AbstractAttributeAware implements CodiConfig
{
    private static final long serialVersionUID = -3332668819111106748L;

    protected CodiCoreConfig()
    {
    }

    /**
     * Per default several artifacts which aren't managed by CDI have to be annotated
     * with {@link org.apache.myfaces.extensions.cdi.core.api.Advanced} as marker for
     * performing manual dependency injection. It isn't performed per default because the
     * majority of those artifacts don't require dependency injection.
     * @return true if the usage of {@link org.apache.myfaces.extensions.cdi.core.api.Advanced}, false otherwise
     */
    public boolean isAdvancedQualifierRequiredForDependencyInjection()
    {
        return true;
    }

    /**
     * Allows to disable the logging of the current configuration during the bootstrapping process.
     * @return true if the configuration should be logged, false otherwise
     */
    public boolean isConfigurationLoggingEnabled()
    {
        return true;
    }
}
