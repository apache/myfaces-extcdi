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
package org.apache.myfaces.examples.codi.jsf12.listener.phase;

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;
import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.config.CodiWebConfig12;

import javax.enterprise.inject.Model;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.Set;

//just an internal demo
@Model
@Typed(ConfigDemoBean.class)
public class ConfigDemoBean extends CodiWebConfig12
{
    private static final long serialVersionUID = -6915243682321970384L;

    @Inject
    private Set<CodiConfig> codiConfig;

    @Inject
    private ConfigResolver configResolver;

    public ConfigDemoBean()
    {
    }

    @Inject
    public ConfigDemoBean(Set<CodiConfig> codiConfig, ConfigResolver configResolver)
    {
        this.codiConfig = codiConfig;
        this.configResolver = configResolver;

        if(this.codiConfig.isEmpty() || this.configResolver.resolve(CodiWebConfig12.class) == null)
        {
            throw new IllegalStateException("invalid config");
        }
    }

    public boolean isTransactionTokenEnabled()
    {
        return this.configResolver.resolve(CodiWebConfig12.class).isTransactionTokenEnabled();
    }
}
