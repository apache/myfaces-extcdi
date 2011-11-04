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
package org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.RestParameters;

import javax.enterprise.event.Observes;
import javax.faces.component.UIViewParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.view.ViewMetadata;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class holds information about the last used RestParameters for a given JSF view.
 *
 * It will expire the conversation when any of those Views get accessed via GET with
 * a different set of &lt;f:viewParam&gt;s.
 */
@WindowScoped
public class JsfRestParameters extends RestParameters implements Serializable
{
    private static final long serialVersionUID = 1349109309042072780L;

    /**
     * This flag will be used to remember a storage request;
     */
    private boolean resetPending;

    /**
     * key= viewId
     * value= concatenated viewParam names + values
     *
     * We use @WindowScoped to automatically include the windowId in a correct way.
     *
     * TODO we might change this to only store a hashKey.
     */
    private Map<String, String> viewParametersForViewId = new ConcurrentHashMap<String, String>();

    /**
     * Check and update the view parameters of the given viewId.
     *
     * @return <code>true</code> if the viewParameters are now different than at the last invocation
     */
    public boolean checkForNewViewParameters()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext == null)
        {
            // this might happen if we are outside the JSF-Servlet, e.g. in a ServletFilter.
            return false;
        }


        if (facesContext.isPostback())
        {
            // we ignore POST requests
            return false;
        }

        String viewId = facesContext.getViewRoot().getViewId();

        String currentViewParams = getViewParams(facesContext, viewId);
        String oldViewParams = viewParametersForViewId.get(viewId);

        if (!currentViewParams.equals(oldViewParams))
        {
            viewParametersForViewId.put(viewId, currentViewParams);

            if (resetPending)
            {
                // if a reset is pending, then we need to expire the context
                resetPending = false;
                return true;
            }

            // only reset the rest context if the oldViewParamaeters were different
            // but not if they didn't got set yet
            return oldViewParams != null;
        }

        resetPending = false;

        return false;
    }

    /**
     * @param facesContext current faces-context
     * @param viewId current question
     * @return the concatenated String of all viewParamName=viewParamValue of the given viewId
     */
    private String getViewParams(FacesContext facesContext, String viewId)
    {
        Collection<UIViewParameter> currentViewParams = ViewMetadata.getViewParameters(facesContext.getViewRoot());
        StringBuilder sb = new StringBuilder();

        // for sorting the view params
        TreeSet<String> viewParamNames = new TreeSet<String>();

        for (UIViewParameter viewParameter : currentViewParams)
        {
            String viewParamName = viewParameter.getName();
            viewParamNames.add(viewParamName);
        }

        for (String viewParamName : viewParamNames)
        {
            String viewParamValue = facesContext.getExternalContext().getRequestParameterMap().get(viewParamName);
            if (viewParamValue == null)
            {
                viewParamValue = "";
            }

            sb.append(viewParamName).append("=").append(viewParamValue).append("+/+");
        }

        return sb.toString();
    }

    @Override
    public void reset()
    {
        viewParametersForViewId.clear();
        resetPending = true;
    }

    /**
     * We need to store the view params after render response because
     * we do not get them in the first initial view invocation when
     * a new windowid got detected.
     */
    public void afterRenderResponse(@Observes @AfterPhase(JsfPhaseId.RENDER_RESPONSE) PhaseEvent phaseEvent)
    {
        if (resetPending)
        {
            resetPending = false;
            return;
        }

        FacesContext facesContext = phaseEvent.getFacesContext();

        // we ignore postbacks
        if (facesContext.isPostback())
        {
            return;
        }

        String viewId = facesContext.getViewRoot().getViewId();
        viewParametersForViewId.put(viewId, getViewParams(facesContext, viewId));
    }
}
