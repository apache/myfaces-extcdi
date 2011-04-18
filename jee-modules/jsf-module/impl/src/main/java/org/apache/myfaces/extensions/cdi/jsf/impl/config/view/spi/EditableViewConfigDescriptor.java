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
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;

/**
 * @author Gerhard Petracek
 */
public interface EditableViewConfigDescriptor extends ViewConfigDescriptor
{
    /**
     * Exposes the inline error-page
     * @return inline error-page
     */
    //it isn't available in ViewConfigDescriptor to avoid confusions of app-developers
    //(e.g. navigation to the error view should be handled differently)
    Class<? extends ViewConfig> getErrorView();

    /**
     * Exposes the {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.ViewParameterMode} of the current
     * entry
     * @return current view-parameter-mode
     */
    //due to the usage in @Page we can't move it to the api of the jsf2 module
    Page.ViewParameterMode getViewParameterMode();

    /**
     * Allows to add page-beans
     * @param pageBeanClass page-bean which should be added
     */
    void addPageBean(Class pageBeanClass);

    /**
     * Allows to mark the current descriptor as partial config e.g. due to inline configs with
     * {@link org.apache.myfaces.extensions.cdi.core.api.config.view.View}
     * @param partialMode indicates if the entry is in the partial view-config mode
     */
    void setPartialViewConfig(boolean partialMode);

    /**
     * Exposes if the current view-config represents a partial config which isn't created based on a central config
     * @return true if it is a partial config, false otherwise
     */
    boolean isPartialViewConfig();

    /**
     * Invokes the methods annotated with {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.InitView}
     */
    void invokeInitViewMethods();

    /**
     * Invokes the methods annotated with {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.PrePageAction}
     */
    void invokePrePageActionMethods();

    /**
     * Invokes the methods annotated with {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.PreRenderView}
     */
    void invokePreRenderViewMethods();

    /**
     * Invokes the methods annotated with {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.PostRenderView}
     */
    void invokePostRenderViewMethods();
}
