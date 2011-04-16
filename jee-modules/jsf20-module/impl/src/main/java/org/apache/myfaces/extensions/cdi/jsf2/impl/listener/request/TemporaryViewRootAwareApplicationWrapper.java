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
package org.apache.myfaces.extensions.cdi.jsf2.impl.listener.request;

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf2.impl.component.DefaultTemporaryUIViewRoot;
import org.apache.myfaces.extensions.cdi.jsf2.impl.component.spi.TemporaryUIViewRoot;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

//needed due to EXTCDI-167
/**
 * @author Gerhard Petracek
 */
class TemporaryViewRootAwareApplicationWrapper extends ApplicationWrapper
{
    private Application wrapped;

    TemporaryViewRootAwareApplicationWrapper(Application wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    public Application getWrapped()
    {
        return this.wrapped;
    }

    protected UIComponent tryToWrapUIViewRoot(UIComponent uiComponent)
    {
        if(uiComponent instanceof UIViewRoot)
        {
            if(!uiComponent.getClass().getName().equals(UIViewRoot.class.getName()))
            {
                return getCustomizedUIViewRoot(uiComponent);
            }
            return new DefaultTemporaryUIViewRoot();
        }
        return uiComponent;
    }

    private UIComponent getCustomizedUIViewRoot(UIComponent uiComponent)
    {
        TemporaryUIViewRoot temporaryComponent = CodiUtils.lookupFromEnvironment(TemporaryUIViewRoot.class);

        if(temporaryComponent instanceof UIComponent)
        {
            return (UIComponent)temporaryComponent;
        }
        return uiComponent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(FacesContext context,
                                       Resource componentResource)
    {
        return tryToWrapUIViewRoot(super.createComponent(context, componentResource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(FacesContext context,
                                       String componentType,
                                       String rendererType)
    {
        return tryToWrapUIViewRoot(super.createComponent(context, componentType, rendererType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(String componentType) throws FacesException
    {
        return tryToWrapUIViewRoot(super.createComponent(componentType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(ValueBinding componentBinding,
                                       FacesContext context,
                                       String componentType) throws FacesException
    {
        return tryToWrapUIViewRoot(super.createComponent(componentBinding, context, componentType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(ValueExpression componentExpression,
                                       FacesContext context,
                                       String componentType,
                                       String rendererType)
    {
        return tryToWrapUIViewRoot(super.createComponent(componentExpression, context, componentType, rendererType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(ValueExpression componentExpression,
                                       FacesContext contexte,
                                       String componentType) throws FacesException
    {
        return tryToWrapUIViewRoot(super.createComponent(componentExpression, contexte, componentType));
    }
}
