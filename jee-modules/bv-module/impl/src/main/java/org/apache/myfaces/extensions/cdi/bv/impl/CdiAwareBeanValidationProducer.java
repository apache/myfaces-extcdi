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
package org.apache.myfaces.extensions.cdi.bv.impl;

import org.apache.myfaces.extensions.cdi.core.api.Advanced;

import static org.apache.myfaces.extensions.cdi.bv.api.BeanValidationModuleBeanNames.VALIDATOR_FACTORY;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * Producer for BV artifacts
 */
@ApplicationScoped
public class CdiAwareBeanValidationProducer
{
    /**
     * Constructor used by proxy libs
     */
    protected CdiAwareBeanValidationProducer()
    {
    }

    /**
     * Creates an injectable {@link javax.validation.ValidatorFactory} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * for wrapping it
     * @return injectable validator-factory
     */
    @Produces
    @Dependent
    @Advanced
    @Named(VALIDATOR_FACTORY)
    public InjectableValidatorFactory createValidatorFactoryForDependencyInjectionAwareConstraintValidators()
    {
        //TODO
        return new InjectableValidatorFactory(new CdiAwareValidatorFactory(new ValidatorFactoryResolver()));
    }

    /**
     * Creates an injectable {@link javax.validation.Validator} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * @return injectable validator
     */
    @Produces
    @Dependent
    @Advanced
    public InjectableValidator createValidatorForDependencyInjectionAwareConstraintValidators()
    {
        return new InjectableValidator(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators());
    }

    /**
     * Creates an injectable {@link javax.validation.ConstraintValidatorFactory}
     * which supports cdi based dependency injection for {@link javax.validation.ConstraintValidator}s
     * @return injectable constraint-validator-factory
     */
    @Produces
    @Dependent
    @Advanced
    public InjectableConstraintValidatorFactory
        createConstraintValidatorFactoryForDependencyInjectionAwareConstraintValidators()
    {
        return new InjectableConstraintValidatorFactory(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators());
    }

    /**
     * Creates an injectable {@link javax.validation.MessageInterpolator}
     * @return injectable message-interpolator
     */
    @Produces
    @Dependent
    @Advanced
    public InjectableMessageInterpolator createMessageInterpolator()
    {
        return new InjectableMessageInterpolator(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators());
    }
}