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
import org.apache.myfaces.extensions.cdi.core.api.security.DefaultErrorView;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO move exceptions to util class
 *
 * @author Gerhard Petracek
 */
public class ViewConfigCache
{
    //we don't need a ConcurrentHashMap - write access is only allowed during the startup (by one thread)
    private static Map<String, ViewConfigEntry> viewIdToViewDefinitionEntryMapping
            = new HashMap<String, ViewConfigEntry>();

    private static Map<Class<? extends ViewConfig>, ViewConfigEntry> viewDefinitionToViewDefinitionEntryMapping
            = new HashMap<Class<? extends ViewConfig>, ViewConfigEntry>();

    private static ViewConfigEntry defaultErrorView;

    static void addViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry)
    {
        tryToStorePageAsDefaultErrorPage(viewDefinitionEntry);

        if(viewIdToViewDefinitionEntryMapping.containsKey(viewId))
        {
            throw new IllegalArgumentException(viewId + " is already mapped to "
                    + viewId + " -> a further view definition (" +
                    viewDefinitionEntry.getViewDefinitionClass().getName() + ") is invalid");
        }
        viewIdToViewDefinitionEntryMapping.put(viewId, viewDefinitionEntry);
        viewDefinitionToViewDefinitionEntryMapping
                .put(viewDefinitionEntry.getViewDefinitionClass(), viewDefinitionEntry);
    }

    private static void tryToStorePageAsDefaultErrorPage(ViewConfigEntry viewDefinitionEntry)
    {
        if(DefaultErrorView.class.isAssignableFrom(viewDefinitionEntry.getViewDefinitionClass()))
        {
            if(defaultErrorView != null)
            {
                throw new IllegalStateException("multiple error pages found " +
                        defaultErrorView.getViewDefinitionClass().getClass().getName() + " and " +
                        viewDefinitionEntry.getViewDefinitionClass().getName());
            }

            defaultErrorView = viewDefinitionEntry;
        }
    }

    public static ViewConfigEntry getViewDefinition(String viewId)
    {
        return viewIdToViewDefinitionEntryMapping.get(viewId);
    }

    public static ViewConfigEntry getViewDefinition(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return viewDefinitionToViewDefinitionEntryMapping.get(viewDefinitionClass);
    }

    public static ViewConfigEntry getDefaultErrorView()
    {
        return defaultErrorView;
    }
}
