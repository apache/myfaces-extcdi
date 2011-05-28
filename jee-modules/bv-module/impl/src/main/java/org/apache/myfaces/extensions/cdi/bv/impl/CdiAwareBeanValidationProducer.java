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
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import static org.apache.myfaces.extensions.cdi.bv.api.BeanValidationModuleBeanNames.VALIDATOR_FACTORY;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.validation.ValidatorFactory;
import javax.inject.Named;

/**
 * @author Gerhard Petracek
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
     * Creates an injectable {@link ValidatorFactory} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * for wrapping it
     * @return injectable validator-factory
     */
    @Produces
    @RequestScoped //TODO test ApplicationScoped with mojarra
    @Advanced
    @Named(VALIDATOR_FACTORY)
    public InjectableValidatorFactory createValidatorFactoryForDependencyInjectionAwareConstraintValidators()
    {
        ValidatorFactory validatorFactory =
                CodiUtils.getContextualReferenceByName(BeanManagerProvider.getInstance().getBeanManager(),
                        "jsfBeanValidationValidatorFactory", true, ValidatorFactory.class);

        if (validatorFactory == null)
        {
            validatorFactory = ValidatorFactoryStorage.getOrCreateValidatorFactory();
        }

        return new InjectableValidatorFactory(new CdiAwareValidatorFactory(validatorFactory));
    }

    /**
     * Creates an injectable {@link javax.validation.Validator} which supports cdi based dependency injection for
     * {@link javax.validation.ConstraintValidator}s
     * @return injectable validator
     */
    @Produces
    @RequestScoped
    @Advanced
    public InjectableValidator createValidatorForDependencyInjectionAwareConstraintValidators()
    {
        return new InjectableValidator(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators().getValidator());
    }

    /**
     * Creates an injectable {@link javax.validation.ConstraintValidatorFactory}
     * which supports cdi based dependency injection for {@link javax.validation.ConstraintValidator}s
     * @return injectable constraint-validator-factory
     */
    @Produces
    @RequestScoped
    @Advanced
    public InjectableConstraintValidatorFactory
        createConstraintValidatorFactoryForDependencyInjectionAwareConstraintValidators()
    {
        return new InjectableConstraintValidatorFactory(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators()
                        .getConstraintValidatorFactory());
    }

    /**
     * Creates an injectable {@link javax.validation.MessageInterpolator}
     * @return injectable message-interpolator
     */
    @Produces
    @RequestScoped
    @Advanced
    public InjectableMessageInterpolator createMessageInterpolator()
    {
        return new InjectableMessageInterpolator(
                createValidatorFactoryForDependencyInjectionAwareConstraintValidators()
                        .getMessageInterpolator());
    }
}