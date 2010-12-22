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
import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.api.resolver.qualifier.BeanValidation;
import org.apache.myfaces.extensions.cdi.core.api.resolver.GenericResolver;
import static org.apache.myfaces.extensions.cdi.bv.api.BeanValidationModuleBeanNames.VALIDATOR_FACTORY;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.inject.Named;

/**
 * @author Gerhard Petracek
 */
public class CdiAwareBeanValidationProducer
{
    protected CdiAwareBeanValidationProducer()
    {
    }

    @Produces
    @Dependent
    @Advanced
    @Named(VALIDATOR_FACTORY)
    public ValidatorFactory createValidatorFactoryForDependencyInjectionAwareConstraintValidators(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver,
            CodiCoreConfig codiCoreConfig)
    {
        ValidatorFactory validatorFactory = validatorFactoryResolver.resolve();
        if (validatorFactory == null)
        {
            validatorFactory = ValidatorFactoryStorage.getOrCreateValidatorFactory();
        }

        return new CdiAwareValidatorFactory(validatorFactory, codiCoreConfig);
    }

    @Produces
    @Dependent
    @Advanced
    public Validator createValidatorForDependencyInjectionAwareConstraintValidators(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver,
            CodiCoreConfig codiCoreConfig)
    {
        return createValidatorFactoryForDependencyInjectionAwareConstraintValidators(
                validatorFactoryResolver, codiCoreConfig).getValidator();
    }

    @Produces
    @Dependent
    @Advanced
    public ConstraintValidatorFactory createConstraintValidatorFactoryForDependencyInjectionAwareConstraintValidators(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver,
            CodiCoreConfig codiCoreConfig)
    {
        return createValidatorFactoryForDependencyInjectionAwareConstraintValidators(
                validatorFactoryResolver, codiCoreConfig).getConstraintValidatorFactory();
    }

    @Produces
    @Dependent
    @Advanced
    public MessageInterpolator createMessageInterpolator(
            @BeanValidation GenericResolver<ValidatorFactory> validatorFactoryResolver,
            CodiCoreConfig codiCoreConfig)
    {
        return createValidatorFactoryForDependencyInjectionAwareConstraintValidators(
                validatorFactoryResolver, codiCoreConfig).getMessageInterpolator();
    }
}