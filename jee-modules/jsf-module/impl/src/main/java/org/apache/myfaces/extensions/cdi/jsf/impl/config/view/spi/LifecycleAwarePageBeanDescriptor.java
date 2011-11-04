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

import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;

import javax.faces.event.PhaseId;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Allows to provide a custom {@link PageBeanDescriptor}
 */
public interface LifecycleAwarePageBeanDescriptor extends PageBeanDescriptor
{
    /**
     * Exposes the lifecycle-callbacks annotated with
     * {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.InitView}
     * @return init-view lifecycle-callback methods
     */
    List<Method> getInitViewMethods();

    /**
     * Exposes the lifecycle-callbacks annotated with
     * {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.PrePageAction}
     * @return pre-page-action lifecycle-callback methods
     */
    List<Method> getPrePageActionMethods();

    /**
     * Exposes the lifecycle-callbacks annotated with
     * {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.PreRenderView}
     * @return pre-render-view lifecycle-callback methods
     */
    List<Method> getPreRenderViewMethods();

    /**
     * Exposes the lifecycle-callbacks annotated with
     * {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.PostRenderView}
     * @return post-render-view lifecycle-callback methods
     */
    List<Method> getPostRenderViewMethods();

    /**
     * Exposes the entry for the lifecycle-callbacks
     * ({@link org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase} and/or
     * {@link org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase}) for the given {@link PhaseId}
     * @param phaseId current phase-id
     * @return entry for the lifecycle-callbacks
     */
    RequestLifecycleCallbackEntry getPhasesLifecycleCallback(PhaseId phaseId);
}
