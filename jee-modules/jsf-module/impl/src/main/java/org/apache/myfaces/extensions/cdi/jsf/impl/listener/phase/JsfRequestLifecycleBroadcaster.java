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
package org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfLifecyclePhaseInformation;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */

@RequestScoped
@Named
public class JsfRequestLifecycleBroadcaster implements JsfLifecyclePhaseInformation
{
    private javax.faces.event.PhaseId facesPhaseId;

    @Inject
    private Event<PhaseEvent> phaseEvent;

    @Inject
    @BeforePhase(JsfPhaseId.ANY_PHASE)
    private Event<PhaseEvent> beforeAnyPhaseEvent;

    @Inject
    @AfterPhase(JsfPhaseId.ANY_PHASE)
    private Event<PhaseEvent> afterAnyPhaseEvent;

    void broadcastBeforeEvent(PhaseEvent phaseEvent)
    {
        this.facesPhaseId = phaseEvent.getPhaseId();

        this.phaseEvent.select(createAnnotationLiteral(phaseEvent.getPhaseId(), true)).fire(phaseEvent);
        this.beforeAnyPhaseEvent.fire(phaseEvent);
    }

    void broadcastAfterEvent(PhaseEvent phaseEvent)
    {
        this.phaseEvent.select(createAnnotationLiteral(phaseEvent.getPhaseId(), false)).fire(phaseEvent);
        this.afterAnyPhaseEvent.fire(phaseEvent);
    }

    private Annotation createAnnotationLiteral(javax.faces.event.PhaseId phaseId, boolean isBeforeEvent)
    {
        if (isBeforeEvent)
        {
            return createBeforeLiteral(phaseId);
        }
        return createAfterLiteral(phaseId);
    }

    private Annotation createBeforeLiteral(final javax.faces.event.PhaseId phaseId)
    {
        return new BeforePhaseBinding()
        {
            private static final long serialVersionUID = 849645435335842723L;

            /**
             * {@inheritDoc}
             */
            public JsfPhaseId value()
            {
                return JsfPhaseId.convertFromFacesClass(phaseId);
            }
        };
    }

    private Annotation createAfterLiteral(final javax.faces.event.PhaseId phaseId)
    {
        return new AfterPhaseBinding()
        {
            private static final long serialVersionUID = 490037768660184656L;

            /**
             * {@inheritDoc}
             */
            public JsfPhaseId value()
            {
                return JsfPhaseId.convertFromFacesClass(phaseId);
            }
        };
    }

    /*
     * implementation of JsfLifecyclePhaseInformation methods
     */

    /**
     * {@inheritDoc}
     */
    public boolean isRestoreViewPhase()
    {
        return javax.faces.event.PhaseId.RESTORE_VIEW.equals(this.facesPhaseId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isApplyRequestValuesPhase()
    {
        return javax.faces.event.PhaseId.APPLY_REQUEST_VALUES.equals(this.facesPhaseId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isProcessValidationsPhase()
    {
        return javax.faces.event.PhaseId.PROCESS_VALIDATIONS.equals(this.facesPhaseId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUpdateModelValuesPhase()
    {
        return javax.faces.event.PhaseId.UPDATE_MODEL_VALUES.equals(this.facesPhaseId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInvokeApplicationPhase()
    {
        return javax.faces.event.PhaseId.INVOKE_APPLICATION.equals(this.facesPhaseId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRenderResponsePhase()
    {
        return javax.faces.event.PhaseId.RENDER_RESPONSE.equals(this.facesPhaseId);
    }
}
