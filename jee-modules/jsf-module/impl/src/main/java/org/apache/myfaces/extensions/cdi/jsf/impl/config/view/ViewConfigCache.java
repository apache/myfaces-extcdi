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
import static org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCacheStore.*;
import static org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCacheStore.setDefaultErrorView;

/**
 * TODO move exceptions to util class
 *
 * @author Gerhard Petracek
 */
public class ViewConfigCache
{
    static void addViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry)
    {
        storeViewDefinition(viewId, viewDefinitionEntry, false);
    }

    static void replaceViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry)
    {
        storeViewDefinition(viewId, viewDefinitionEntry, true);
    }

    private static void storeViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry, boolean allowReplace)
    {
        tryToStorePageAsDefaultErrorPage(viewDefinitionEntry);

        if(getViewIdToViewDefinitionEntryMapping().containsKey(viewId) && !allowReplace)
        {
            throw new IllegalArgumentException(viewId + " is already mapped to "
                    + viewId + " via " + getViewIdToViewDefinitionEntryMapping().get(viewId).getViewDefinitionClass()
                    + " -> a further view definition (" +
                    viewDefinitionEntry.getViewDefinitionClass().getName() + ") is invalid");
        }
        getViewIdToViewDefinitionEntryMapping().put(viewId, viewDefinitionEntry);
        getViewDefinitionToViewDefinitionEntryMapping()
                .put(viewDefinitionEntry.getViewDefinitionClass(), viewDefinitionEntry);
    }

    private static void tryToStorePageAsDefaultErrorPage(ViewConfigEntry viewDefinitionEntry)
    {
        if(DefaultErrorView.class.isAssignableFrom(viewDefinitionEntry.getViewDefinitionClass()))
        {
            if(getDefaultErrorViewForApplication() != null)
            {
                throw new IllegalStateException("multiple error pages found " +
                        getDefaultErrorViewForApplication().getViewDefinitionClass().getClass().getName() + " and " +
                        viewDefinitionEntry.getViewDefinitionClass().getName());
            }

            setDefaultErrorView(viewDefinitionEntry);
        }
    }

    public static ViewConfigEntry getViewDefinition(String viewId)
    {
        return getViewIdToViewDefinitionEntryMapping().get(viewId);
    }

    public static ViewConfigEntry getViewDefinition(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return getViewDefinitionToViewDefinitionEntryMapping().get(viewDefinitionClass);
    }

    public static ViewConfigEntry getDefaultErrorView()
    {
        return getDefaultErrorViewForApplication();
    }
}
