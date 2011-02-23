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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
class InjectableValidator implements Validator, Serializable
{
    private static final long serialVersionUID = 7925077169313672595L;

    private transient Validator wrapped;

    public InjectableValidator()
    {
    }

    InjectableValidator(Validator validator)
    {
        this.wrapped = validator;
    }

    protected Validator getWrapped()
    {
        if(this.wrapped == null)
        {
            this.wrapped = CodiUtils.getContextualReferenceByClass(Validator.class, new AdvancedLiteral());

            if(this.wrapped instanceof InjectableValidator)
            {
                this.wrapped = ((InjectableValidator)this.wrapped).getWrapped();
            }
        }
        return this.wrapped;
    }

    /*
     * generated
     */
    public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes)
    {
        return getWrapped().validate(t, classes);
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes)
    {
        return getWrapped().validateProperty(t, s, classes);
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> tClass, String s, Object o, Class<?>... classes)
    {
        return getWrapped().validateValue(tClass, s, o, classes);
    }

    public BeanDescriptor getConstraintsForClass(Class<?> aClass)
    {
        return getWrapped().getConstraintsForClass(aClass);
    }

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
