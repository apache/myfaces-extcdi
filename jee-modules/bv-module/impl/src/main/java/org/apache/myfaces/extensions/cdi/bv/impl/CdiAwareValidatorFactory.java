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

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.injectFields;

import javax.enterprise.inject.Typed;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintValidator;

/**
 * {@link ValidatorFactory} which can be serialized
 */
@Typed()
class CdiAwareValidatorFactory extends SerializableValidatorFactory
{
    private static final long serialVersionUID = 5066880057488085949L;

    private ValidatorFactoryResolver validatorFactoryResolver;

    /**
     * Constructor used by proxy libs
     */
    protected CdiAwareValidatorFactory()
    {
    }

    protected CdiAwareValidatorFactory(ValidatorFactoryResolver validatorFactoryResolver)
    {
        this.validatorFactoryResolver = validatorFactoryResolver;
    }

    /**
     * {@inheritDoc}
     */
    public Validator getValidator()
    {
        ValidatorFactory validatorFactory = this.validatorFactoryResolver.getValidatorFactory();

        return validatorFactory.usingContext()
                .constraintValidatorFactory(getConstraintValidatorFactory())
                .messageInterpolator(validatorFactory.getMessageInterpolator())
                .traversableResolver(validatorFactory.getTraversableResolver())
                .getValidator();
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext usingContext()
    {
        return this.validatorFactoryResolver.getValidatorFactory().usingContext();
    }

    /**
     * {@inheritDoc}
     */
    public MessageInterpolator getMessageInterpolator()
    {
        return this.validatorFactoryResolver.getValidatorFactory().getMessageInterpolator();
    }

    /**
     * {@inheritDoc}
     */
    public TraversableResolver getTraversableResolver()
    {
        return this.validatorFactoryResolver.getValidatorFactory().getTraversableResolver();
    }

    /**
     * {@inheritDoc}
     */
    public ConstraintValidatorFactory getConstraintValidatorFactory()
    {
        return new ConstraintValidatorFactory()
        {
            /**
             * {@inheritDoc}
             */
            @SuppressWarnings({"unchecked"})
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> targetClass)
            {
                T validator = validatorFactoryResolver.getValidatorFactory()
                        .getConstraintValidatorFactory().getInstance(targetClass);

                return injectFields(validator, false);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public <T> T unwrap(Class<T> tClass)
    {
        return this.validatorFactoryResolver.getValidatorFactory().unwrap(tClass);
    }
}
