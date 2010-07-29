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
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.view.definition.NavigationMode;

/**
 * @author Gerhard Petracek
 */
public class ViewDefinitionEntry
{
    private final String viewId;
    private final Class<? extends ViewDefinition> viewDefinitionClass;
    private final NavigationMode navigationMode;

    public ViewDefinitionEntry(String viewId,
                               Class<? extends ViewDefinition> viewDefinitionClass,
                               NavigationMode navigationMode)
    {
        this.viewId = viewId;
        this.viewDefinitionClass = viewDefinitionClass;
        this.navigationMode = navigationMode;
        //TODO validate view-di
    }

    public String getViewId()
    {
        return viewId;
    }

    public Class<? extends ViewDefinition> getViewDefinitionClass()
    {
        return viewDefinitionClass;
    }

    public NavigationMode getNavigationMode()
    {
        return navigationMode;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ViewDefinitionEntry))
        {
            return false;
        }

        ViewDefinitionEntry that = (ViewDefinitionEntry) o;

        if (!viewId.equals(that.viewId))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return viewId.hashCode();
    }
}
