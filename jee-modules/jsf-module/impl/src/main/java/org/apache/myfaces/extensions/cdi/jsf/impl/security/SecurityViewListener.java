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
package org.apache.myfaces.extensions.cdi.jsf.impl.security;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.SecurityUtils.tryToHandleSecurityViolation;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigEntry;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.SecurityUtils.invokeVoters;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
public class SecurityViewListener
{
    public void checkPermission(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent event)
    {
        FacesContext facesContext = event.getFacesContext();
        ViewConfigEntry entry = ViewConfigCache.getViewDefinition(facesContext.getViewRoot().getViewId());

        if(entry == null)
        {
            return;
        }

        try
        {
            invokeVoters(null, entry.getAccessDecisionVoters(), entry.getErrorView());
        }
        catch (AccessDeniedException accessDeniedException)
        {
            tryToHandleSecurityViolation(accessDeniedException);
            facesContext.renderResponse();
        }
    }
}
