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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import java.io.Serializable;

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.injectFields;

/**
 * @author Gerhard Petracek
 */
class InjectableConstraintValidatorFactory implements ConstraintValidatorFactory, Serializable
{
    private static final long serialVersionUID = -4851853797257005554L;

    private InjectableValidatorFactory injectableValidatorFactory;

    /**
     * Constructor used by proxy libs
     */
    protected InjectableConstraintValidatorFactory()
    {
    }

    InjectableConstraintValidatorFactory(InjectableValidatorFactory injectableValidatorFactory)
    {
        this.injectableValidatorFactory = injectableValidatorFactory;
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> tClass)
    {
        T validator = this.injectableValidatorFactory.getValidatorFactory()
                .getConstraintValidatorFactory().getInstance(tClass);

        return injectFields(validator, false);
    }
}
