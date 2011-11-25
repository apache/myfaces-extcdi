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

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.RestParameters;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewMetadata;
import java.io.Serializable;
import java.util.Collection;
import java.util.TreeSet;

/**
 * This class holds information about the last used RestParameters for a given JSF view.
 *
 * It will expire the conversation when any of those Views get accessed via GET with
 * a different set of &lt;f:viewParam&gt;s.
 */
@RequestScoped
public class JsfRestParameters extends RestParameters implements Serializable
{
    private static final long serialVersionUID = 1349109309042072780L;

    /**
     * We cache the viewParams values as long as the viewId remains the same
     * for this very request. We do this because evaluating the
     * viewParams with every bean invocation is very expensive.
     * This String also contains the viewId!
     */
    private String restId = null;

    /**
     * This flag will be used to remember a storage request;
     */
    public boolean isPostback()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext != null && facesContext.isPostback();
    }

    public String getRestId()
    {
        if (restId == null)
        {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext == null)
            {
                return null;
            }
            String viewId = getViewId(facesContext);
            if (viewId == null)
            {
                return null;
            }

            restId = viewId + "//" + getViewParams(facesContext);
        }

        return restId;
    }

    /**
     * @param facesContext current faces-context
     * @return the concatenated String of all viewParamName=viewParamValue of the given viewId
     */
    private String getViewParams(FacesContext facesContext)
    {
        Collection<UIViewParameter> currentViewParams = ViewMetadata.getViewParameters(facesContext.getViewRoot());
        String viewId = getViewId(facesContext);
        if (viewId == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder(viewId).append("?");

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

    private String getViewId(FacesContext facesContext)
    {
        String viewId = null;
        if (facesContext != null)
        {
            UIViewRoot viewRoot = facesContext.getViewRoot();
            if (viewRoot != null)
            {
                viewId = viewRoot.getViewId();
            }
        }

        return viewId;
    }

}
