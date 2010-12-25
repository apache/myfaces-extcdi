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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigEntry;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
class ViewConfigCacheStore
{
    //we don't need a ConcurrentHashMap - write access is only allowed during the startup (by one thread)

    private static Map<ClassLoader, Map<String, ViewConfigEntry>> viewIdToViewDefinitionEntryMapping
            = new HashMap<ClassLoader, Map<String, ViewConfigEntry>>();

    private static Map<ClassLoader, Map<Class<? extends ViewConfig>, ViewConfigEntry>>
            viewDefinitionToViewDefinitionEntryMapping =
            new HashMap<ClassLoader, Map<Class<? extends ViewConfig>, ViewConfigEntry>>();

    private static Map<ClassLoader, ViewConfigEntry> defaultErrorView = new HashMap<ClassLoader, ViewConfigEntry>();

    static Map<String, ViewConfigEntry> getViewIdToViewDefinitionEntryMapping()
    {
        Map<String, ViewConfigEntry> result = viewIdToViewDefinitionEntryMapping.get(getClassloader());

        if(result == null)
        {
            result = new HashMap<String, ViewConfigEntry>();
            viewIdToViewDefinitionEntryMapping.put(getClassloader(), result);
        }

        return result;
    }

    static Map<Class<? extends ViewConfig>, ViewConfigEntry> getViewDefinitionToViewDefinitionEntryMapping()
    {
        Map<Class<? extends ViewConfig>, ViewConfigEntry> result =
                viewDefinitionToViewDefinitionEntryMapping.get(getClassloader());

        if(result == null)
        {
            result = new HashMap<Class<? extends ViewConfig>, ViewConfigEntry>();
            viewDefinitionToViewDefinitionEntryMapping.put(getClassloader(), result);
        }
        return result;
    }

    static ViewConfigEntry getDefaultErrorViewForApplication()
    {
        return defaultErrorView.get(getClassloader());
    }

    static void clear()
    {
        getViewIdToViewDefinitionEntryMapping().clear();
        getViewDefinitionToViewDefinitionEntryMapping().clear();
    }

    public static void setDefaultErrorView(ViewConfigEntry viewDefinitionEntry)
    {
        defaultErrorView.put(getClassloader(), viewDefinitionEntry);
    }

    private static ClassLoader getClassloader()
    {
        return ClassUtils.getClassLoader(null);
    }
}
