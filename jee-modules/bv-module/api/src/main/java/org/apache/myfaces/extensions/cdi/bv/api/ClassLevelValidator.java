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
package org.apache.myfaces.extensions.cdi.bv.api;

import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Required if 'invalidValue' should be used for violation messages for class-level validation.
 *
 * @author Gerhard Petracek
 */
public abstract class ClassLevelValidator<A extends Annotation, T> implements ConstraintValidator<A, T>, Serializable
{
    protected A constraint;

    public void initialize(A constraint)
    {
        this.constraint = constraint;
    }

    public boolean isValid(T instance, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean valid = isValidInstance(instance, constraintValidatorContext);

        if (!valid)
        {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                                            createMessage(getInvalidValue(), getMessageDescriptor()))
                                      .addConstraintViolation();
        }

        return valid;
    }

    protected String createMessage(Serializable invalidValue, String messageDescriptor)
    {
        return invalidValue + "$org.apache.myfaces.extensions.cdi$" + messageDescriptor;
    }

    protected String getMessageDescriptor()
    {
        try
        {
            Method method = this.constraint.annotationType().getDeclaredMethod("message");
            return (String) method.invoke(this.constraint);
        }
        catch (Exception e)
        {
            if(e instanceof RuntimeException)
            {
                throw (RuntimeException)e;
            }
            throw new UnhandledException(e);
        }
    }

    protected abstract boolean isValidInstance(T t, ConstraintValidatorContext constraintValidatorContext);

    protected abstract Serializable getInvalidValue();
}
