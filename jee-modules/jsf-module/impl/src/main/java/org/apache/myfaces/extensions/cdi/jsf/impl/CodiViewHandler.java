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
package org.apache.myfaces.extensions.cdi.jsf.impl;

import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.WindowContextAwareViewHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.security.SecurityAwareViewHandler;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * Allows a better performance and a fine-grained deactivation of the sub-view handlers
 * btw. extending them or a stand-alone usage.
 *
 * @author Gerhard Petracek
 */
public class CodiViewHandler extends ViewHandlerWrapper implements Deactivatable
{
    private ViewHandler wrapped;

    private ViewHandler windowContextAwareViewHandler;

    private ViewHandler securityAwareViewHandler;

    public CodiViewHandler(ViewHandler wrapped)
    {
        this.wrapped = wrapped;
        if(isActivated())
        {
            this.windowContextAwareViewHandler = new WindowContextAwareViewHandler(this.wrapped);
            this.securityAwareViewHandler = new SecurityAwareViewHandler(this.wrapped);
        }
    }

    @Override
    public UIViewRoot createView(FacesContext facesContext, String viewId)
    {
        if(this.securityAwareViewHandler == null)
        {
            return this.wrapped.createView(facesContext, viewId);
        }
        return this.securityAwareViewHandler.createView(facesContext, viewId);
    }

    @Override
    public String getActionURL(FacesContext facesContext, String viewId)
    {
        if(this.windowContextAwareViewHandler == null)
        {
            return this.wrapped.getActionURL(facesContext, viewId);
        }
        //TODO add security check (deactivated per default)
        return this.windowContextAwareViewHandler.getActionURL(facesContext, viewId);
    }

    @Override
    public UIViewRoot restoreView(FacesContext facesContext, String viewId)
    {
        if(this.windowContextAwareViewHandler == null)
        {
            return this.wrapped.restoreView(facesContext, viewId);
        }
        return this.windowContextAwareViewHandler.restoreView(facesContext, viewId);
    }

    public ViewHandler getWrapped()
    {
        return this.wrapped;
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
