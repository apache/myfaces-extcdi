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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.listener.phase;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.PhaseId;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;

@ApplicationScoped
@Named
public class JsfRequestLifecycleBroadcaster
{
    static final String BEAN_NAME = "jsfRequestLifecycleBroadcaster";

    @Inject
    private Event<PhaseEvent> phaseEvent;

    void broadcastBeforeEvent(PhaseEvent phaseEvent)
    {
        this.phaseEvent.select(createAnnotationLiteral(phaseEvent.getPhaseId(), true)).fire(phaseEvent);
    }

    void broadcastAfterEvent(PhaseEvent phaseEvent)
    {
        this.phaseEvent.select(createAnnotationLiteral(phaseEvent.getPhaseId(), false)).fire(phaseEvent);
    }

    private Annotation createAnnotationLiteral(javax.faces.event.PhaseId phaseId, boolean isBeforeEvent)
    {
        if(isBeforeEvent)
        {
            return createBeforeLiteral(phaseId);
        }
        return createAfterLiteral(phaseId);
    }

    private Annotation createBeforeLiteral(final javax.faces.event.PhaseId phaseId)
    {
        return new BeforePhaseBinding() {
            private static final long serialVersionUID = 849645435335842723L;

            public PhaseId value()
            {
                return PhaseId.convert(phaseId);
            }
        };
    }

    private Annotation createAfterLiteral(final javax.faces.event.PhaseId phaseId)
    {
        return new AfterPhaseBinding() {
            private static final long serialVersionUID = 490037768660184656L;

            public PhaseId value()
            {
                return PhaseId.convert(phaseId);
            }
        };
    }
}
