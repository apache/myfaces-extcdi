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

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;
import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.getOrCreateScopedInstanceOfBeanByName;
import org.apache.myfaces.extensions.cdi.core.api.Advanced;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.inject.Inject;
import javax.enterprise.inject.spi.BeanManager;

/**
 * @author Gerhard Petracek
 */

@Advanced
@JsfPhaseListener
public class JsfRequestLifecyclePhaseListener implements PhaseListener
{
    private static final long serialVersionUID = -4351903831660165998L;

    private static final String BEAN_NAME = "jsfRequestLifecycleBroadcaster";

    //all implementations will be serializable
    @Inject
    private BeanManager beanManager;

    /**
     * {@inheritDoc}
     */
    public void beforePhase(PhaseEvent phaseEvent)
    {
        resolveBroadcaster().broadcastBeforeEvent(phaseEvent);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPhase(PhaseEvent phaseEvent)
    {
        resolveBroadcaster().broadcastAfterEvent(phaseEvent);
    }

    private JsfRequestLifecycleBroadcaster resolveBroadcaster()
    {
        //cdi has to inject the event
        return getOrCreateScopedInstanceOfBeanByName(
                this.beanManager, BEAN_NAME, JsfRequestLifecycleBroadcaster.class);
    }

    /**
     * {@inheritDoc}
     */
    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }
}