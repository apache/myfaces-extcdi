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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.io.Serializable;
import java.util.Set;

/**
 * {@link Validator} which can be serialized
 */
class InjectableValidator implements Validator, Serializable
{
    private static final long serialVersionUID = 7925077169313672595L;

    private InjectableValidatorFactory injectableValidatorFactory;

    /**
     * Constructor used by proxy libs
     */
    protected InjectableValidator()
    {
    }

    InjectableValidator(InjectableValidatorFactory injectableValidatorFactory)
    {
        this.injectableValidatorFactory = injectableValidatorFactory;
    }

    protected Validator getValidator()
    {
        return this.injectableValidatorFactory.getValidator();
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes)
    {
        return getValidator().validate(t, classes);
    }

    /**
     * {@inheritDoc}
     */
    public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes)
    {
        return getValidator().validateProperty(t, s, classes);
    }

    /**
     * {@inheritDoc}
     */
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> tClass, String s, Object o, Class<?>... classes)
    {
        return getValidator().validateValue(tClass, s, o, classes);
    }

    /**
     * {@inheritDoc}
     */
    public BeanDescriptor getConstraintsForClass(Class<?> aClass)
    {
        return getValidator().getConstraintsForClass(aClass);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T unwrap(Class<T> tClass)
    {
        return getValidator().unwrap(tClass);
    }
}
