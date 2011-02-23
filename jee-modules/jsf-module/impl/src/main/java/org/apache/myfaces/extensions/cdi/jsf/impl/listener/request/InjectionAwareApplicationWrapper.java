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
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.event.ActionListener;
import javax.faces.component.UIComponent;
import javax.el.ValueExpression;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ELContextListener;
import javax.el.ELException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.Iterator;
import java.util.Collection;

/**
 * TODO move it to a meaningful package
 *
 * @author Gerhard Petracek
 * @see org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase.RestoreInjectionPointsObserver
 */
class InjectionAwareApplicationWrapper extends Application
{
    private Application wrapped;

    private boolean advancedQualifierRequiredForDependencyInjection;

    protected InjectionAwareApplicationWrapper(Application wrapped,
                                               boolean advancedQualifierRequiredForDependencyInjection)
    {
        this.wrapped = wrapped;
        this.advancedQualifierRequiredForDependencyInjection = advancedQualifierRequiredForDependencyInjection;
    }

    public Converter createConverter(String converterId)
    {
        return injectFields(this.wrapped.createConverter(converterId),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    public Converter createConverter(Class targetClass)
    {
        return injectFields(this.wrapped.createConverter(targetClass),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    public Validator createValidator(String validatorId)
    {
        return injectFields(this.wrapped.createValidator(validatorId),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /*
     * generated
     */
    public void addELResolver(ELResolver resolver)
    {
        wrapped.addELResolver(resolver);
    }

    public ELResolver getELResolver()
    {
        return wrapped.getELResolver();
    }

    public ResourceBundle getResourceBundle(FacesContext ctx, String name)
            throws NullPointerException
    {
        return wrapped.getResourceBundle(ctx, name);
    }

    public UIComponent createComponent(
            ValueExpression componentExpression, FacesContext facesContext, String componentType)
            throws NullPointerException
    {
        return wrapped.createComponent(componentExpression, facesContext, componentType);
    }

    public ExpressionFactory getExpressionFactory()
    {
        return wrapped.getExpressionFactory();
    }

    public void addELContextListener(ELContextListener listener)
    {
        wrapped.addELContextListener(listener);
    }

    public void removeELContextListener(ELContextListener listener)
    {
        wrapped.removeELContextListener(listener);
    }

    public ELContextListener[] getELContextListeners()
    {
        return wrapped.getELContextListeners();
    }

    public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType)
            throws ELException
    {
        return wrapped.evaluateExpressionGet(context, expression, expectedType);
    }

    public ActionListener getActionListener()
    {
        return wrapped.getActionListener();
    }

    public void setActionListener(ActionListener listener)
    {
        wrapped.setActionListener(listener);
    }

    public Locale getDefaultLocale()
    {
        return wrapped.getDefaultLocale();
    }

    public void setDefaultLocale(Locale locale)
    {
        wrapped.setDefaultLocale(locale);
    }

    public String getDefaultRenderKitId()
    {
        return wrapped.getDefaultRenderKitId();
    }

    public void setDefaultRenderKitId(String renderKitId)
    {
        wrapped.setDefaultRenderKitId(renderKitId);
    }

    public String getMessageBundle()
    {
        return wrapped.getMessageBundle();
    }

    public void setMessageBundle(String bundle)
    {
        wrapped.setMessageBundle(bundle);
    }

    public NavigationHandler getNavigationHandler()
    {
        return wrapped.getNavigationHandler();
    }

    public void setNavigationHandler(NavigationHandler handler)
    {
        wrapped.setNavigationHandler(handler);
    }

    public PropertyResolver getPropertyResolver()
    {
        return wrapped.getPropertyResolver();
    }

    public void setPropertyResolver(PropertyResolver resolver)
    {
        wrapped.setPropertyResolver(resolver);
    }

    public VariableResolver getVariableResolver()
    {
        return wrapped.getVariableResolver();
    }

    public void setVariableResolver(VariableResolver resolver)
    {
        wrapped.setVariableResolver(resolver);
    }

    public ViewHandler getViewHandler()
    {
        return wrapped.getViewHandler();
    }

    public void setViewHandler(ViewHandler handler)
    {
        wrapped.setViewHandler(handler);
    }

    public StateManager getStateManager()
    {
        return wrapped.getStateManager();
    }

    public void setStateManager(StateManager manager)
    {
        wrapped.setStateManager(manager);
    }

    public void addComponent(String componentType, String componentClass)
    {
        wrapped.addComponent(componentType, componentClass);
    }

    public UIComponent createComponent(String componentType)
    {
        return wrapped.createComponent(componentType);
    }

    public UIComponent createComponent(ValueBinding componentBinding, FacesContext context, String componentType)
    {
        return wrapped.createComponent(componentBinding, context, componentType);
    }

    public Iterator<String> getComponentTypes()
    {
        return wrapped.getComponentTypes();
    }

    public void addConverter(String converterId, String converterClass)
    {
        wrapped.addConverter(converterId, converterClass);
    }

    public void addConverter(Class targetClass, String converterClass)
    {
        wrapped.addConverter(targetClass, converterClass);
    }

    public Iterator<String> getConverterIds()
    {
        return wrapped.getConverterIds();
    }

    public Iterator<Class> getConverterTypes()
    {
        return wrapped.getConverterTypes();
    }

    public MethodBinding createMethodBinding(String ref, Class[] params)
            throws ReferenceSyntaxException
    {
        return wrapped.createMethodBinding(ref, params);
    }

    public Iterator<Locale> getSupportedLocales()
    {
        return wrapped.getSupportedLocales();
    }

    public void setSupportedLocales(Collection<Locale> locales)
    {
        wrapped.setSupportedLocales(locales);
    }

    public void addValidator(String validatorId, String validatorClass)
    {
        wrapped.addValidator(validatorId, validatorClass);
    }

    public Iterator<String> getValidatorIds()
    {
        return wrapped.getValidatorIds();
    }

    public ValueBinding createValueBinding(String ref)
            throws ReferenceSyntaxException
    {
        return wrapped.createValueBinding(ref);
    }
}
