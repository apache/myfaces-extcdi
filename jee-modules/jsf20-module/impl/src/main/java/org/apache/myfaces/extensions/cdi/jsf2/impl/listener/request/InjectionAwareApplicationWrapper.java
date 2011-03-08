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

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.injectFields;

import javax.enterprise.inject.Typed;
import javax.faces.application.ApplicationWrapper;
import javax.faces.application.Application;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;

/**
 * TODO move it to a meaningful package
 *
 * @author Gerhard Petracek
 * @see org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase.RestoreInjectionPointsObserver
 */
@Typed()
class InjectionAwareApplicationWrapper extends ApplicationWrapper
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
     * {@inheritDoc}
     */
    public Application getWrapped()
    {
        return this.wrapped;
    }

    /**
     * Performs dependency injection manually (if permitted).
     * {@inheritDoc}
     */
    @Override
    public Converter createConverter(String converterId)
    {
        return injectFields(this.wrapped.createConverter(converterId),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /**
     * Performs dependency injection manually (if permitted).
     * {@inheritDoc}
     */
    @Override
    public Converter createConverter(Class targetClass)
    {
        return injectFields(this.wrapped.createConverter(targetClass),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /**
     * Performs dependency injection manually (if permitted).
     * {@inheritDoc}
     */
    @Override
    public Validator createValidator(String validatorId)
    {
        return injectFields(this.wrapped.createValidator(validatorId),
                this.advancedQualifierRequiredForDependencyInjection);
    }
}
