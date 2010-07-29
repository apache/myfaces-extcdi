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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.view;

import org.apache.myfaces.extensions.cdi.core.api.view.definition.ViewDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class ViewDefinitionCache
{
    //we don't need a ConcurrentHashMap - write access is only allowed during the startup (by one thread)
    private static Map<String, ViewDefinitionEntry> viewIdToViewDefinitionEntryMapping
            = new HashMap<String, ViewDefinitionEntry>();

    private static Map<Class<? extends ViewDefinition>, ViewDefinitionEntry> viewDefinitionToViewDefinitionEntryMapping
            = new HashMap<Class<? extends ViewDefinition>, ViewDefinitionEntry>();

    static void addViewDefinition(String viewId, ViewDefinitionEntry viewDefinitionEntry)
    {
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

    public static ViewDefinitionEntry getViewDefinition(String viewId)
    {
        return viewIdToViewDefinitionEntryMapping.get(viewId);
    }

    public static ViewDefinitionEntry getViewDefinition(Class<? extends ViewDefinition> viewDefinitionClass)
    {
        return viewDefinitionToViewDefinitionEntryMapping.get(viewDefinitionClass);
    }
}
