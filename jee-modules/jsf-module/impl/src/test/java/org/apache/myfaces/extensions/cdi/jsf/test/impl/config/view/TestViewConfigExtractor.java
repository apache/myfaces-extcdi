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
package org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.Advanced;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigExtractor;

/**
 * {@link ViewConfigExtractor} with an injected instance of the default implementation (by convention)
 */
@Advanced
public class TestViewConfigExtractor implements ViewConfigExtractor
{
    private static final long serialVersionUID = -1714138861845603400L;

    private ViewConfigExtractor defaultViewConfigExtractor;

    public EditableViewConfigDescriptor extractViewConfig(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return defaultViewConfigExtractor.extractViewConfig(viewDefinitionClass);
    }

    public boolean isInlineViewConfig(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return defaultViewConfigExtractor.isInlineViewConfig(viewDefinitionClass);
    }

    public EditableViewConfigDescriptor extractInlineViewConfig(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return defaultViewConfigExtractor.extractInlineViewConfig(viewDefinitionClass);
    }
}
