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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.resolver;

import org.apache.myfaces.extensions.cdi.core.api.resolver.GenericResolver;
import org.apache.myfaces.extensions.cdi.core.api.resolver.BeanValidation;

import javax.faces.context.FacesContext;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
@BeanValidation
public class JsfValidatorFactoryResolver implements GenericResolver<ValidatorFactory>
{
    private static final String VALIDATOR_FACTORY_KEY = "javax.faces.validator.beanValidator.ValidatorFactory";

    public ValidatorFactory resolve()
    {
        Map<String, Object> applicationMap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        ValidatorFactory validatorFactory = null;

        if (applicationMap.containsKey(VALIDATOR_FACTORY_KEY))
        {
            if (applicationMap.get(VALIDATOR_FACTORY_KEY) instanceof ValidatorFactory)
            {
                validatorFactory = (ValidatorFactory) applicationMap.get(VALIDATOR_FACTORY_KEY);
            }
        }

        if (validatorFactory == null)
        {
            validatorFactory = Validation.buildDefaultValidatorFactory();
            applicationMap.put(VALIDATOR_FACTORY_KEY, validatorFactory);
        }
        return validatorFactory;
    }
}
