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
package org.apache.myfaces.extensions.cdi.jsf.impl.listener.request;

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.injectFields;

import javax.enterprise.inject.Typed;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.component.UIComponent;
import javax.el.ValueExpression;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ELContextListener;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Iterator;
import java.util.Collection;

/**
 * @see org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase.RestoreInjectionPointsObserver
 */
//TODO move it to a meaningful package
@Typed()
class InjectionAwareApplicationWrapper extends Application
{
    private Application wrapped;

    private boolean advancedQualifierRequiredForDependencyInjection;

    InjectionAwareApplicationWrapper(Application wrapped,
                                     boolean advancedQualifierRequiredForDependencyInjection)
    {
        this.wrapped = wrapped;
        this.advancedQualifierRequiredForDependencyInjection = advancedQualifierRequiredForDependencyInjection;
    }

    /**
     * Performs dependency injection manually (if permitted).
     * {@inheritDoc}
     */
    public Converter createConverter(String converterId)
    {
        return injectFields(this.wrapped.createConverter(converterId),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /**
     * Performs dependency injection manually (if permitted).
     * {@inheritDoc}
     */
    public Converter createConverter(Class targetClass)
    {
        return injectFields(this.wrapped.createConverter(targetClass),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /**
     * Performs dependency injection manually (if permitted).
     * {@inheritDoc}
     */
    public Validator createValidator(String validatorId)
    {
        return injectFields(this.wrapped.createValidator(validatorId),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void addELResolver(ELResolver resolver)
    {
        wrapped.addELResolver(resolver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ELResolver getELResolver()
    {
        return wrapped.getELResolver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceBundle getResourceBundle(FacesContext ctx, String name)
    {
        return wrapped.getResourceBundle(ctx, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIComponent createComponent(
            ValueExpression componentExpression, FacesContext facesContext, String componentType)
    {
        return wrapped.createComponent(componentExpression, facesContext, componentType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionFactory getExpressionFactory()
    {
        return wrapped.getExpressionFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addELContextListener(ELContextListener listener)
    {
        wrapped.addELContextListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeELContextListener(ELContextListener listener)
    {
        wrapped.removeELContextListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ELContextListener[] getELContextListeners()
    {
        return wrapped.getELContextListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType)
    {
        return wrapped.evaluateExpressionGet(context, expression, expectedType);
    }

    /**
     * {@inheritDoc}
     */
    public ActionListener getActionListener()
    {
        return wrapped.getActionListener();
    }

    /**
     * {@inheritDoc}
     */
    public void setActionListener(ActionListener listener)
    {
        wrapped.setActionListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    public Locale getDefaultLocale()
    {
        return wrapped.getDefaultLocale();
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultLocale(Locale locale)
    {
        wrapped.setDefaultLocale(locale);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultRenderKitId()
    {
        return wrapped.getDefaultRenderKitId();
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultRenderKitId(String renderKitId)
    {
        wrapped.setDefaultRenderKitId(renderKitId);
    }

    /**
     * {@inheritDoc}
     */
    public String getMessageBundle()
    {
        return wrapped.getMessageBundle();
    }

    /**
     * {@inheritDoc}
     */
    public void setMessageBundle(String bundle)
    {
        wrapped.setMessageBundle(bundle);
    }

    /**
     * {@inheritDoc}
     */
    public NavigationHandler getNavigationHandler()
    {
        return wrapped.getNavigationHandler();
    }

    /**
     * {@inheritDoc}
     */
    public void setNavigationHandler(NavigationHandler handler)
    {
        wrapped.setNavigationHandler(handler);
    }

    /**
     * {@inheritDoc}
     */
    public PropertyResolver getPropertyResolver()
    {
        return wrapped.getPropertyResolver();
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyResolver(PropertyResolver resolver)
    {
        wrapped.setPropertyResolver(resolver);
    }

    /**
     * {@inheritDoc}
     */
    public VariableResolver getVariableResolver()
    {
        return wrapped.getVariableResolver();
    }

    /**
     * {@inheritDoc}
     */
    public void setVariableResolver(VariableResolver resolver)
    {
        wrapped.setVariableResolver(resolver);
    }

    /**
     * {@inheritDoc}
     */
    public ViewHandler getViewHandler()
    {
        return wrapped.getViewHandler();
    }

    /**
     * {@inheritDoc}
     */
    public void setViewHandler(ViewHandler handler)
    {
        wrapped.setViewHandler(handler);
    }

    /**
     * {@inheritDoc}
     */
    public StateManager getStateManager()
    {
        return wrapped.getStateManager();
    }

    /**
     * {@inheritDoc}
     */
    public void setStateManager(StateManager manager)
    {
        wrapped.setStateManager(manager);
    }

    /**
     * {@inheritDoc}
     */
    public void addComponent(String componentType, String componentClass)
    {
        wrapped.addComponent(componentType, componentClass);
    }

    /**
     * {@inheritDoc}
     */
    public UIComponent createComponent(String componentType)
    {
        return wrapped.createComponent(componentType);
    }

    /**
     * {@inheritDoc}
     */
    public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
    {
        return wrapped.createComponent(componentBinding, context, componentType);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getComponentTypes()
    {
        return wrapped.getComponentTypes();
    }

    /**
     * {@inheritDoc}
     */
    public void addConverter(String converterId, String converterClass)
    {
        wrapped.addConverter(converterId, converterClass);
    }

    /**
     * {@inheritDoc}
     */
    public void addConverter(Class targetClass, String converterClass)
    {
        wrapped.addConverter(targetClass, converterClass);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getConverterIds()
    {
        return wrapped.getConverterIds();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Class> getConverterTypes()
    {
        return wrapped.getConverterTypes();
    }

    /**
     * {@inheritDoc}
     */
    public MethodBinding createMethodBinding(String ref, Class[] params)
    {
        return wrapped.createMethodBinding(ref, params);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Locale> getSupportedLocales()
    {
        return wrapped.getSupportedLocales();
    }

    /**
     * {@inheritDoc}
     */
    public void setSupportedLocales(Collection<Locale> locales)
    {
        wrapped.setSupportedLocales(locales);
    }

    /**
     * {@inheritDoc}
     */
    public void addValidator(String validatorId, String validatorClass)
    {
        wrapped.addValidator(validatorId, validatorClass);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getValidatorIds()
    {
        return wrapped.getValidatorIds();
    }

    /**
     * {@inheritDoc}
     */
    public ValueBinding createValueBinding(String ref)
    {
        return wrapped.createValueBinding(ref);
    }
}
