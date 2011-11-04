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
package org.apache.myfaces.extensions.cdi.jsf.api.listener.phase;

/**
 * Allows to detect the current request-lifecycle-phase in a bean.
 */
public interface JsfLifecyclePhaseInformation
{
    /**
     * Evaluates if the current phase is the restore-view phase.
     * @return true if the current phase matches the restore-view phase, false otherwise
     */
    boolean isRestoreViewPhase();

    /**
     * Evaluates if the current phase is the apply-request-values phase.
     * @return true if the current phase matches the apply-request-values phase, false otherwise
     */
    boolean isApplyRequestValuesPhase();

    /**
     * Evaluates if the current phase is the process-validations phase.
     * @return true if the current phase matches the process-validations phase, false otherwise
     */
    boolean isProcessValidationsPhase();

    /**
     * Evaluates if the current phase is the update-model-values phase.
     * @return true if the current phase matches the update-model-values phase, false otherwise
     */
    boolean isUpdateModelValuesPhase();

    /**
     * Evaluates if the current phase is the invoke-application phase.
     * @return true if the current phase matches the invoke-application phase, false otherwise
     */
    boolean isInvokeApplicationPhase();

    /**
     * Evaluates if the current phase is the render-response phase.
     * @return true if the current phase matches the render-response phase, false otherwise
     */
    boolean isRenderResponsePhase();
}
