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
package org.apache.myfaces.extensions.cdi.jsf.impl.navigation;

import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

/**
 * We have to ensure the invocation order for the type-safe navigation feature/s.
 *
 * @author Gerhard Petracek
 */
public class CodiNavigationHandler extends NavigationHandler implements Deactivatable
{
    private final NavigationHandler wrapped;

    private final NavigationHandler originalNavigationHandler;

    public CodiNavigationHandler(NavigationHandler navigationHandler)
    {
        if(isActivated())
        {
            ViewConfigAwareNavigationHandler viewConfigAwareNavigationHandler =
                    new ViewConfigAwareNavigationHandler(navigationHandler, false);

            this.wrapped = new AccessScopeAwareNavigationHandler(viewConfigAwareNavigationHandler);
        }
        else
        {
            this.wrapped = navigationHandler;
        }

        this.originalNavigationHandler = navigationHandler;
    }

    public void handleNavigation(FacesContext context, String fromAction, String outcome)
    {
        if(context.getResponseComplete() /*see EXTCDI-92*/)
        {
            this.originalNavigationHandler.handleNavigation(context, fromAction, outcome);
        }
        else
        {
            this.wrapped.handleNavigation(context, fromAction, outcome);
        }
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
