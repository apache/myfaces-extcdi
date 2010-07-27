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
package org.apache.myfaces.extensions.cdi.core.impl.utils;

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
public class ApplicationCache
{
    private static Map<Class<? extends CodiConfig>, CodiConfig> configCache
            = new ConcurrentHashMap<Class<? extends CodiConfig>, CodiConfig>();

    public static CodiConfig getConfig(Class<? extends CodiConfig> config)
    {
        return configCache.get(config);
    }

    public static void setConfig(Class<? extends CodiConfig> configKey, CodiConfig config)
    {
        configCache.put(configKey, config);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void resetConfigs()
    {
        configCache.clear();
    }
}
