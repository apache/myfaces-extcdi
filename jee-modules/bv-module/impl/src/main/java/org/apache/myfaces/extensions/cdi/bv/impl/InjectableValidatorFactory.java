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

import org.apache.myfaces.extensions.cdi.core.impl.util.AdvancedLiteral;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

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

    private transient ValidatorFactory wrapped;

    /**
     * Default constructor
     */
    public InjectableValidatorFactory()
    {
    }

    InjectableValidatorFactory(ValidatorFactory validatorFactory)
    {
        this.wrapped = validatorFactory;
    }

    protected ValidatorFactory getWrapped()
    {
        if(this.wrapped == null)
        {
            this.wrapped = CodiUtils.getContextualReferenceByClass(ValidatorFactory.class, new AdvancedLiteral());

            if(this.wrapped instanceof InjectableValidatorFactory)
            {
                this.wrapped = ((InjectableValidatorFactory)this.wrapped).getWrapped();
            }
        }
        return this.wrapped;
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public Validator getValidator()
    {
        return getWrapped().getValidator();
    }

    /**
     * {@inheritDoc}
     */
    public ValidatorContext usingContext()
    {
        return getWrapped().usingContext();
    }

    /**
     * {@inheritDoc}
     */
    public MessageInterpolator getMessageInterpolator()
    {
        return getWrapped().getMessageInterpolator();
    }

    /**
     * {@inheritDoc}
     */
    public TraversableResolver getTraversableResolver()
    {
        return getWrapped().getTraversableResolver();
    }

    /**
     * {@inheritDoc}
     */
    public ConstraintValidatorFactory getConstraintValidatorFactory()
    {
        return getWrapped().getConstraintValidatorFactory();
    }

    /**
     * {@inheritDoc}
     */
    public <T> T unwrap(Class<T> tClass)
    {
        return getWrapped().unwrap(tClass);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
