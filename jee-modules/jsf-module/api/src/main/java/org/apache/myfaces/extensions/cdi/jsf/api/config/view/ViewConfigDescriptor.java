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
package org.apache.myfaces.extensions.cdi.jsf.api.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
public interface ViewConfigDescriptor
{
    /**
     * View-ID of the current descriptor
     * @return current view-id
     */
    String getViewId();

    /**
     * Class which was used for creating the current descriptor
     * @return view-config class
     */
    Class<? extends ViewConfig> getViewConfig();

    /**
     * Navigation type which should be used if type-safe navigation is used
     * @return configured navigation mode
     */
    Page.NavigationMode getNavigationMode();

    /**
     * Custom meta-data which is configured for the entry. It allows to provide and resolve custom meta-data annotated
     * with {@link org.apache.myfaces.extensions.cdi.core.api.config.view.ViewMetaData}
     * @return custom meta-data of the current entry
     */
    List<Annotation> getMetaData();

    /**
     * {@link AccessDecisionVoter}s which should be invoked to secure the page represented by the current entry.
     * @return configured access-decision-voters
     */
    List<Class<? extends AccessDecisionVoter>> getAccessDecisionVoters();

    /**
     * Page-bean descriptors for the page represented by the current entry.
     * @return descriptors which represent the page-beans for the current page
     */
    List<PageBeanDescriptor> getPageBeanDescriptors();
}
