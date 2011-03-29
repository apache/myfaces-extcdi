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
import org.apache.myfaces.extensions.cdi.core.api.resolver.qualifier.BeanValidation;
import org.apache.myfaces.extensions.cdi.core.api.resolver.GenericResolver;
import static org.apache.myfaces.extensions.cdi.bv.api.BeanValidationModuleBeanNames.VALIDATOR_FACTORY;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.inject.Named;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class CdiAwareBeanValidationProducer
{
    protected CdiAwareBeanValidationProducer()
    {
    }

    /**
     * Creates an injectable {@link ValidatorFactory} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * @param validatorFactoryResolver resolver for resolving a custom configured {@link ValidatorFactory}
     * for wrapping it
     * @return injectable validator-factory
     */
    @Produces
    @Dependent
    @Advanced
    @Named(VALIDATOR_FACTORY)
    public ValidatorFactory createValidatorFactoryForDependencyInjectionAwareConstraintValidators(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver)
    {
        ValidatorFactory validatorFactory = validatorFactoryResolver.resolve();
        if (validatorFactory == null)
        {
            validatorFactory = ValidatorFactoryStorage.getOrCreateValidatorFactory();
        }

        return new InjectableValidatorFactory(new CdiAwareValidatorFactory(validatorFactory));
    }

    /**
     * Creates an injectable {@link Validator} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * @param validatorFactoryResolver resolver for resolving a custom configured {@link ValidatorFactory}
     * which should be used as wrapped factory
     * @return injectable validator
     */
    @Produces
    @Dependent
    @Advanced
    public Validator createValidatorForDependencyInjectionAwareConstraintValidators(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver)
    {
        return new InjectableValidator(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators(
                    validatorFactoryResolver).getValidator());
    }

    /**
     * Creates an injectable {@link ConstraintValidatorFactory} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * @param validatorFactoryResolver resolver for resolving a custom configured {@link ValidatorFactory}
     * which should be used as wrapped factory
     * @return injectable constraint-validator-factory
     */
    @Produces
    @Dependent
    @Advanced
    public ConstraintValidatorFactory createConstraintValidatorFactoryForDependencyInjectionAwareConstraintValidators(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver)
    {
        return new InjectableConstraintValidatorFactory(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators(validatorFactoryResolver)
                        .getConstraintValidatorFactory());
    }

    /**
     * Creates an injectable {@link MessageInterpolator}
     * @param validatorFactoryResolver resolver for resolving a custom configured {@link ValidatorFactory}
     * which should be used as wrapped factory
     * @return injectable message-interpolator
     */
    @Produces
    @Dependent
    @Advanced
    public MessageInterpolator createMessageInterpolator(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver)
    {
        return new InjectableMessageInterpolator(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators(validatorFactoryResolver)
                        .getMessageInterpolator());
    }
}