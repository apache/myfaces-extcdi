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
package org.apache.myfaces.extensions.cdi.trinidad.impl;

import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToInstantiateClassForName;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.trinidad.context.RenderingContext;

import javax.faces.event.PhaseEvent;
import javax.enterprise.event.Observes;

/**
 * Workaround for initializing the RenderingContext of Trinidad
 *
 * @author Gerhard Petracek
 */
public class InitTrinidad
{
    protected void initTrinidad(@Observes @BeforePhase(JsfPhaseId.ANY_PHASE) PhaseEvent phaseEvent)
    {
        if (RenderingContext.getCurrentInstance() == null)
        {
            tryToInstantiateClassForName("org.apache.myfaces.trinidadinternal.renderkit.core.CoreRenderingContext");
        }
    }
}
