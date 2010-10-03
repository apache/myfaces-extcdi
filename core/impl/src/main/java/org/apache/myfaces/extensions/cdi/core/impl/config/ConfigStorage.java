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
package org.apache.myfaces.extensions.cdi.core.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO add test-cases and refactor afterwards
 *
 * @author Gerhard Petracek
 */
class ConfigStorage
{
    private static Map<ClassLoader, Boolean> configInitialized =
            new ConcurrentHashMap<ClassLoader, Boolean>();

    private static Map<ClassLoader, Set<CodiConfig>> configSetCache =
            new ConcurrentHashMap<ClassLoader, Set<CodiConfig>>();

    static Set<CodiConfig> getCodiConfig(ClassLoader classLoader)
    {
        return configSetCache.get(classLoader);
    }

    static synchronized boolean isConfigInitialized(ClassLoader classLoader)
    {
        Boolean initializedMarker = configInitialized.get(classLoader);
        return initializedMarker != null;
    }

    static void setConfigInitialized(ClassLoader classLoader)
    {
        configInitialized.put(classLoader, Boolean.TRUE);
    }

    static void initConfigCache(int size, ClassLoader classLoader)
    {
        configSetCache.put(classLoader, new HashSet<CodiConfig>(size));
    }

    static void addCodiConfig(CodiConfig currentCodiConfig, ClassLoader classLoader)
    {
        Set<CodiConfig> codiConfig = configSetCache.get(classLoader);
        codiConfig.add(currentCodiConfig);
    }
}
