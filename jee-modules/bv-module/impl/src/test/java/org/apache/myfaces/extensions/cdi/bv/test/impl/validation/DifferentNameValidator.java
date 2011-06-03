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
package org.apache.myfaces.extensions.cdi.bv.test.impl.validation;

import org.apache.myfaces.extensions.cdi.bv.api.ClassLevelConstraintValidator;

import javax.inject.Inject;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

/**
 * Test impl
 */
public class DifferentNameValidator extends ClassLevelConstraintValidator<DifferentName, TestBean>
{
    private static final long serialVersionUID = 3851988368625335444L;

    @Inject
    private ValidationService validationService;

    private String invalidValue;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isValidInstance(TestBean testBean, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean result = this.validationService.isValid(testBean);

        if(result)
        {
            this.invalidValue = null;
        }
        else
        {
            this.invalidValue = testBean.getFirstName();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Serializable getInvalidValue()
    {
        return this.invalidValue;
    }
}
