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
package org.apache.myfaces.extensions.cdi.jsf.impl.bv.resolver;

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.bv.InvalidValueAwareMessageInterpolator;

import javax.enterprise.inject.Typed;
import javax.validation.MessageInterpolator;
import javax.validation.ValidatorContext;
import javax.validation.TraversableResolver;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validator;


/**
 * {@link ValidatorContext} which allows to use 'invalidValue' as placeholder in violation messages
 */
//TODO move to a different package
@Typed()
class InvalidValueAwareValidatorContext implements ValidatorContext
{
    private ValidatorContext wrapped;

    /**
     * Constructor for wrapping the given {@link ValidatorContext}
     * @param wrapped validator-context which should be wrapped
     */
    public InvalidValueAwareValidatorContext(ValidatorContext wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext messageInterpolator(MessageInterpolator messageInterpolator)
    {
        MessageInterpolator messageInterpolatorWrapper = new InvalidValueAwareMessageInterpolator(messageInterpolator);
        CodiUtils.injectFields(messageInterpolatorWrapper, false);
        return this.wrapped.messageInterpolator(messageInterpolatorWrapper);
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public ValidatorContext traversableResolver(TraversableResolver traversableResolver)
    {
        return wrapped.traversableResolver(traversableResolver);
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory)
    {
        return wrapped.constraintValidatorFactory(constraintValidatorFactory);
    }

    /**
     * {@inheritDoc}
     */
    public Validator getValidator()
    {
        return wrapped.getValidator();
    }
}
