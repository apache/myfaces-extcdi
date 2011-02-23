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
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.InlineViewConfigRoot;

/**
 * @author Gerhard Petracek
 */
class ExtractedInlineViewConfigDefinitionEntry extends ExtractedViewConfigDefinitionEntry
{
    public ExtractedInlineViewConfigDefinitionEntry(Class<? extends ViewConfig> viewDefinitionClass, String basePath)
    {
        super(viewDefinitionClass);

        if(basePath != null)
        {
            this.basePath = basePath + "/";
        }
        else
        {
            this.basePath = "";
        }
    }

    @Override
    public void setBasePath(String currentBasePath)
    {
        //filtered
    }

    @Override
    public String getViewId()
    {
        Class<?> viewConfigRootMarker = ViewConfigCache.getInlineViewConfigRootMarker();
        InlineViewConfigRoot viewConfigRoot = viewConfigRootMarker.getAnnotation(InlineViewConfigRoot.class);

        if(!viewConfigRoot.basePath().endsWith("/*") && !".".equals(viewConfigRoot.basePath()))
        {
            this.basePath = viewConfigRoot.basePath();
        }

        if(viewConfigRoot.basePath().endsWith("/*"))
        {
            String packageName = viewConfigRoot.basePath();
            this.basePath = packageName.substring(0, packageName.length() - 2) + "/" + this.basePath;
        }

        if(".".equals(viewConfigRoot.basePath()))
        {
            String packageName = viewConfigRootMarker.getPackage().getName();
            this.basePath = "/" + packageName.substring(packageName.lastIndexOf('.') + 1) + "/" + this.basePath;
        }

        if(DEFAULT_PAGE_NAME.equals(this.pageName))
        {
            this.pageName = createPageName(this.viewDefinitionClass.getSimpleName());

            for(String postfix : viewConfigRoot.pageBeanPostfix())
            {
                if(this.pageName.endsWith(postfix))
                {
                    this.pageName = this.pageName.substring(0, this.pageName.length() - postfix.length());
                    break;
                }
            }
        }

        return super.getViewId();
    }
}
