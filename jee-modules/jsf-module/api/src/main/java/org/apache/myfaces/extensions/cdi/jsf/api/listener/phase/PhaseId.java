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
 * @author Gerhard Petracek
 */
public enum PhaseId
{
    RESTORE_VIEW(javax.faces.event.PhaseId.RESTORE_VIEW),
    APPLY_REQUEST_VALUES(javax.faces.event.PhaseId.APPLY_REQUEST_VALUES),
    PROCESS_VALIDATIONS(javax.faces.event.PhaseId.PROCESS_VALIDATIONS),
    UPDATE_MODEL_VALUES(javax.faces.event.PhaseId.UPDATE_MODEL_VALUES),
    INVOKE_APPLICATION(javax.faces.event.PhaseId.INVOKE_APPLICATION),
    RENDER_RESPONSE(javax.faces.event.PhaseId.RENDER_RESPONSE),
    ANY_PHASE(javax.faces.event.PhaseId.ANY_PHASE);

    javax.faces.event.PhaseId phaseId;

    PhaseId(javax.faces.event.PhaseId phaseId)
    {
        this.phaseId = phaseId;
    }

    public static javax.faces.event.PhaseId convertToFacesClass(PhaseId phaseId)
    {
        return phaseId.getPhaseId();
    }

    public static PhaseId convertFromFacesClass(javax.faces.event.PhaseId phaseId)
    {
        if(RESTORE_VIEW.getPhaseId().equals(phaseId))
        {
            return RESTORE_VIEW;
        }

        if(RENDER_RESPONSE.getPhaseId().equals(phaseId))
        {
            return RENDER_RESPONSE;
        }

        if(APPLY_REQUEST_VALUES.getPhaseId().equals(phaseId))
        {
            return APPLY_REQUEST_VALUES;
        }

        if(PROCESS_VALIDATIONS.getPhaseId().equals(phaseId))
        {
            return PROCESS_VALIDATIONS;
        }

        if(UPDATE_MODEL_VALUES.getPhaseId().equals(phaseId))
        {
            return UPDATE_MODEL_VALUES;
        }

        if(INVOKE_APPLICATION.getPhaseId().equals(phaseId))
        {
            return INVOKE_APPLICATION;
        }

        return null;
    }

    javax.faces.event.PhaseId getPhaseId()
    {
        return phaseId;
    }
}
