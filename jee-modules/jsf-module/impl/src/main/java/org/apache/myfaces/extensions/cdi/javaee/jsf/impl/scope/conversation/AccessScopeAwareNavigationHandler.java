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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.request.CodiFacesContextWrapper;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class AccessScopeAwareNavigationHandler extends NavigationHandler
{
    public static final String OLD_VIEW_ID_KEY = "oldViewId";
    public static final String NEW_VIEW_ID_KEY = "newViewId";

    private NavigationHandler navigationHandler;

    public AccessScopeAwareNavigationHandler(NavigationHandler navigationHandler)
    {
        this.navigationHandler = navigationHandler;
    }

    public void handleNavigation(FacesContext facesContext, String s, String s1)
    {
        facesContext = getWrappedFacesContext(facesContext);
        String oldViewId = facesContext.getViewRoot().getViewId();

        Map requestMap = facesContext.getExternalContext().getRequestMap();
        requestMap.put(OLD_VIEW_ID_KEY, oldViewId); //don't change the order

        this.navigationHandler.handleNavigation(facesContext, s, s1);

        String newViewId = facesContext.getViewRoot().getViewId();


        requestMap.put(NEW_VIEW_ID_KEY, newViewId);
    }

    //TODO check myfaces core - issue? facesContext is not wrapped here
    private FacesContext getWrappedFacesContext(FacesContext facesContext)
    {
        if(facesContext instanceof CodiFacesContextWrapper)
        {
            return facesContext;
        }
        return new CodiFacesContextWrapper(facesContext);
    }
}
