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
package org.apache.myfaces.extensions.cdi.bv.test.impl;

import org.apache.myfaces.extensions.cdi.bv.test.impl.validation.DifferentName;
import org.apache.myfaces.extensions.cdi.bv.test.impl.validation.DifferentNameValidator;
import org.apache.myfaces.extensions.cdi.bv.test.impl.validation.TestBean;
import org.apache.myfaces.extensions.cdi.core.api.Advanced;
import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.test.junit4.AbstractCdiAwareTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.inject.Inject;
import javax.validation.*;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Test impl
 */
@RunWith(JUnit4.class)
public class SimpleBeanValidationTest extends AbstractCdiAwareTest
{
    @Inject
    @Advanced
    private Validator validator;

    @Inject
    @Advanced
    private ValidatorFactory validatorFactory;

    @Inject
    @Advanced
    private ConstraintValidatorFactory constraintValidatorFactory;

    /**
     */
    @Test
    public void testConstraintValidatorInjection()
    {
        Set<ConstraintViolation<TestBean>> violations = this.validator.validate(new TestBean("Tester", "Tester"));

        assertEquals(1, violations.size());
    }

    /**
     */
    @Test
    public void testInjectionAwareConstraintValidatorFactory()
    {
        ConstraintValidator<DifferentName, TestBean> constraintValidator =
                this.constraintValidatorFactory.getInstance(DifferentNameValidator.class);

        constraintValidator.initialize(DefaultAnnotation.of(DifferentName.class));
        boolean result = constraintValidator.isValid(new TestBean("Tester", "Tester"),
                        new ConstraintValidatorContext()
                        {
                            public void disableDefaultConstraintViolation()
                            {
                                //
                            }

                            public String getDefaultConstraintMessageTemplate()
                            {
                                return "";
                            }

                            public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String msgTemplate)
                            {
                                return new ConstraintViolationBuilder()
                                {
                                    public NodeBuilderDefinedContext addNode(String name)
                                    {
                                        return null;
                                    }

                                    public ConstraintValidatorContext addConstraintViolation()
                                    {
                                        return null;
                                    }
                                };
                            }
                        });
        assertEquals(false, result);
    }

    /**
     */
    @Test
    public void testInvalidValueInMessage()
    {
        Validator validator = this.validatorFactory.usingContext().messageInterpolator(new MessageInterpolator()
        {
            public String interpolate(String messageTemplate, Context context)
            {
                return interpolate(messageTemplate, context, null);
            }

            public String interpolate(String messageTemplate, Context context, Locale locale)
            {
                //simplified version of the interpolator provided by the jsf module
                String invalidValue = messageTemplate.substring(0, messageTemplate.indexOf("$"));
                String message = messageTemplate.substring(messageTemplate.lastIndexOf("$") + 1);

                return message.replace("{invalidValue}", invalidValue);
            }
        }).getValidator();
        Set<ConstraintViolation<TestBean>> violations = validator.validate(new TestBean("Tester", "Tester"));

        assertEquals(1, violations.size());

        assertEquals("The same name 'Tester' isn't allowed.", violations.iterator().next().getMessage());
    }
}
