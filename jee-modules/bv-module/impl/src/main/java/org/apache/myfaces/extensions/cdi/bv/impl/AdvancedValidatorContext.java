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

import javax.enterprise.inject.Typed;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

/**
 * @author Gerhard Petracek
 */
@Typed()
class AdvancedValidatorContext implements ValidatorContext
{
    private ValidatorContext wrapped;
    private ValidatorFactory validatorFactory;

    protected AdvancedValidatorContext()
    {
    }

    AdvancedValidatorContext(ValidatorFactory validatorFactory, ValidatorContext validatorContext)
    {
        this.validatorFactory = validatorFactory;
        this.wrapped = validatorContext;
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext messageInterpolator(MessageInterpolator messageInterpolator)
    {
        wrapped.messageInterpolator(messageInterpolator);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext traversableResolver(TraversableResolver traversableResolver)
    {
        wrapped.traversableResolver(traversableResolver);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext constraintValidatorFactory(ConstraintValidatorFactory factory)
    {
        wrapped.constraintValidatorFactory(factory);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Validator getValidator()
    {
        ConstraintValidatorFactory constraintValidatorFactory =
                this.validatorFactory.getConstraintValidatorFactory();

        this.wrapped.constraintValidatorFactory(
                new InjectionAwareConstraintValidatorFactory(constraintValidatorFactory));
        return this.wrapped.getValidator();
    }
}
