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

import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.ConstraintValidatorFactory;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 */
class InjectableValidatorFactory implements ValidatorFactory, Serializable
{
    private static final long serialVersionUID = 2200415478496396632L;

    private SerializableValidatorFactory serializableValidatorFactory;

    /**
     * Constructor used by proxy libs
     */
    protected InjectableValidatorFactory()
    {
    }

    InjectableValidatorFactory(SerializableValidatorFactory serializableValidatorFactory)
    {
        this.serializableValidatorFactory = serializableValidatorFactory;
    }

    protected ValidatorFactory getValidatorFactory()
    {
        return this.serializableValidatorFactory;
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public Validator getValidator()
    {
        return getValidatorFactory().getValidator();
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext usingContext()
    {
        return new AdvancedValidatorContext(this, getValidatorFactory().usingContext());
    }

    /**
     * {@inheritDoc}
     */
    public MessageInterpolator getMessageInterpolator()
    {
        return getValidatorFactory().getMessageInterpolator();
    }

    /**
     * {@inheritDoc}
     */
    public TraversableResolver getTraversableResolver()
    {
        return getValidatorFactory().getTraversableResolver();
    }

    /**
     * {@inheritDoc}
     */
    public ConstraintValidatorFactory getConstraintValidatorFactory()
    {
        return getValidatorFactory().getConstraintValidatorFactory();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T unwrap(Class<T> tClass)
    {
        return getValidatorFactory().unwrap(tClass);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
