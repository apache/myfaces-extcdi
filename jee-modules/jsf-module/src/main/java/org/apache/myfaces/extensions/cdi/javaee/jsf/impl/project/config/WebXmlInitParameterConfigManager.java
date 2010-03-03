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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.project.config;

import org.apache.myfaces.extensions.cdi.core.api.project.config.ConfigEntryResolver;
import org.apache.myfaces.extensions.cdi.core.api.project.config.ConfigManager;
import org.apache.myfaces.extensions.cdi.core.api.project.config.InitParameter;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;

import javax.inject.Inject;

//TODO instead of if statements: inject 1-n lookup strategies - ~every strategy is responsible for a different key
//TODO @Jsf @InitParameter doesn't work

@InitParameter(Jsf.class)
public class WebXmlInitParameterConfigManager implements ConfigManager<String, String>
{
    @Inject
    @InitParameter
    private ConfigManager<String, String> wrapped;

    @Inject
    @Jsf
    private ConfigEntryResolver<String, String> configEntryResolver;

    public String getValue(String key)
    {
        String result = this.wrapped.getValue(key);

        if (result != null)
        {
            return result;
        }

        return configEntryResolver.resolveEntry(key);
    }
}