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

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import static org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId.*;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.SecurityUtils.tryToHandleSecurityViolation;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import static org.apache.myfaces.extensions.cdi.core.impl.util.SecurityUtils.invokeVoters;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.event.PhaseEvent;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * If you don't need it or there is any issue, deactivate it via a CDI extension via
 * {@link javax.enterprise.inject.spi.ProcessAnnotatedType#veto()} .
 * 
 * @author Gerhard Petracek
 */
public class SecurityViewListener
{
    private static final String LAZY_SECURITY_CHECK_KEY = "LAZY_SECURITY_CHECK";

    /**
     * Checks the permission based on the
     * {@link org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter}s
     * which are configured for the current view.
     * @param event current phase-event
     * @param beanManager current bean-manager
     */
    public void checkPermission(@Observes @AfterPhase(RESTORE_VIEW) PhaseEvent event, BeanManager beanManager)
    {
        FacesContext facesContext = event.getFacesContext();

        if(facesContext.getViewRoot() == null)
        {
            triggerFallback(facesContext);
            return;
        }

        checkPermission(beanManager, facesContext);
    }

    /**
     * Checks the permission based on the
     * {@link org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter}s
     * which are configured for the current view.
     * @param event current phase-event
     * @param beanManager current bean-manager
     */
    public void checkPermissionBeforeRendering(
            @Observes @BeforePhase(RENDER_RESPONSE) PhaseEvent event, BeanManager beanManager)
    {
        FacesContext facesContext = event.getFacesContext();

        if(facesContext.getViewRoot() == null)
        {
            //TODO log warning
            return;
        }

        Map<String, Object> requestMap = getRequestMap(facesContext);

        if(requestMap != null && Boolean.TRUE.equals(requestMap.get(LAZY_SECURITY_CHECK_KEY)))
        {
            checkPermission(beanManager, facesContext);
        }
    }

    private void checkPermission(BeanManager beanManager, FacesContext facesContext)
    {
        ViewConfigDescriptor entry = ViewConfigCache.getViewConfigDescriptor(facesContext.getViewRoot().getViewId());

        if(entry == null)
        {
            return;
        }

        try
        {
            Class<? extends ViewConfig> errorView = null;

            if(entry instanceof EditableViewConfigDescriptor)
            {
                errorView = ((EditableViewConfigDescriptor)entry).getErrorView();
            }
            invokeVoters(null, beanManager, entry.getAccessDecisionVoters(), errorView);
        }
        catch (AccessDeniedException accessDeniedException)
        {
            tryToHandleSecurityViolation(accessDeniedException);
            facesContext.renderResponse();
        }
    }

    private void triggerFallback(FacesContext facesContext)
    {
        Map<String, Object> requestMap = getRequestMap(facesContext);

        if(requestMap != null)
        {
            requestMap.put(LAZY_SECURITY_CHECK_KEY, Boolean.TRUE);
        }
    }

    private Map<String, Object> getRequestMap(FacesContext facesContext)
    {
        if(facesContext.getExternalContext() == null || facesContext.getExternalContext().getRequestMap() == null)
        {
            //TODO log warning
            return null;
        }

        return facesContext.getExternalContext().getRequestMap();
    }
}
