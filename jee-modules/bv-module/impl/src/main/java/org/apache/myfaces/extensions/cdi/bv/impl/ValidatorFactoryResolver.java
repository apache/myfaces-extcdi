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

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.inject.Typed;
import javax.validation.ValidatorFactory;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Typed()
class ValidatorFactoryResolver implements Serializable
{
    private static final long serialVersionUID = -8862792298701494514L;

    ValidatorFactory getValidatorFactory()
    {
        ValidatorFactory validatorFactory =
                CodiUtils.getContextualReferenceByName(BeanManagerProvider.getInstance().getBeanManager(),
                        "jsfBeanValidationValidatorFactory", true, ValidatorFactory.class);

        if (validatorFactory == null)
        {
            validatorFactory = ValidatorFactoryStorage.getOrCreateValidatorFactory();
        }

        return validatorFactory;
    }
}
