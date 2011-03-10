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
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigResolver;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultViewConfigResolver implements ViewConfigResolver
{
    private static final long serialVersionUID = 5092196084535892957L;

    /**
     * {@inheritDoc}
     */
    public ViewConfigDescriptor getViewConfig(String viewId)
    {
        return ViewConfigCache.getViewConfig(viewId);
    }

    /**
     * {@inheritDoc}
     */
    public ViewConfigDescriptor getDefaultErrorViewConfig()
    {
        return ViewConfigCache.getDefaultErrorView();
    }

    /**
     * {@inheritDoc}
     */
    public ViewConfigDescriptor getErrorViewConfig(Class<? extends ViewConfig> viewDefinitionClass)
    {
        ViewConfigDescriptor viewConfigDescriptor = getViewConfig(viewDefinitionClass);

        Class<? extends ViewConfig> errorView = null;
        if(viewConfigDescriptor instanceof EditableViewConfigDescriptor)
        {
            errorView = ((EditableViewConfigDescriptor)viewConfigDescriptor).getErrorView();
        }

        if(errorView == null)
        {
            return getDefaultErrorViewConfig();
        }
        return getViewConfig(errorView);
    }

    /**
     * {@inheritDoc}
     */
    public List<ViewConfigDescriptor> getViewConfigs()
    {
        return Collections
                .unmodifiableList((List<? extends ViewConfigDescriptor>) ViewConfigCache.getViewConfigDescriptors());
    }

    /**
     * {@inheritDoc}
     */
    public ViewConfigDescriptor getViewConfig(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return ViewConfigCache.getViewConfig(viewDefinitionClass);
    }
}
