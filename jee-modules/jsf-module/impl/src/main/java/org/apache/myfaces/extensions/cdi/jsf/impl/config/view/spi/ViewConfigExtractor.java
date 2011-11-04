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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;

import java.io.Serializable;

/**
 * Allows to implement a custom extractor which for custom view-config concepts
 */
public interface ViewConfigExtractor extends Serializable
{
    /**
     * Creates a {@link EditableViewConfigDescriptor} for the given view-config class
     * @param viewDefinitionClass current view-config class
     * @return descriptor which represents the view-config for the given config class
     */
    EditableViewConfigDescriptor extractViewConfig(Class<? extends ViewConfig> viewDefinitionClass);

    /**
     * Evaluates if the given view-config class is an inline conifg - that means if it is a resolvable (page-)bean
     * @param viewDefinitionClass view-config class
     * @return true if it is a resolvable (page-)bean with inline view-config, false otherwise
     */
    boolean isInlineViewConfig(Class<? extends ViewConfig> viewDefinitionClass);

    /**
     * Creates a {@link EditableViewConfigDescriptor} for the given inline view-config class
     * @param viewDefinitionClass current view-config class
     * @return descriptor which represents the view-config for the given config class
     */
    EditableViewConfigDescriptor extractInlineViewConfig(Class<? extends ViewConfig> viewDefinitionClass);
}
