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
package org.apache.myfaces.extensions.cdi.jsf.impl.bv.resolver;

import org.apache.myfaces.extensions.cdi.core.api.qualifier.BeanValidation;
import org.apache.myfaces.extensions.cdi.jsf.api.config.JsfModuleConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.validation.ValidatorFactory;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class JsfValidatorFactoryProducer implements Serializable
{
    private static final long serialVersionUID = -4307637449713503965L;

    /**
     * Default constructor required by proxy libs
     */
    protected JsfValidatorFactoryProducer()
    {
    }

    @Produces
    @Dependent
    @BeanValidation
    public ValidatorFactory createValidatorFactory(JsfModuleConfig jsfModuleConfig)
    {
        if(jsfModuleConfig.isInvalidValueAwareMessageInterpolatorEnabled())
        {
            return new InvalidValueAwareValidatorFactory();
        }
        return new SerializableValidatorFactory();
    }
}