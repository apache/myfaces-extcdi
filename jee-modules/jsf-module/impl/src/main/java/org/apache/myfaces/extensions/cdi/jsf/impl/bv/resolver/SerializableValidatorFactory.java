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

import javax.enterprise.inject.Typed;
import javax.faces.context.FacesContext;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.ConstraintValidatorFactory;
import java.io.Serializable;
import java.util.Map;

/**
 * Might be needed by startup listeners (which use artifacts which have @Advanced ValidatorFactory) for
 * creating sample data. For such sample data it's ok to have a factory which isn't aware of JSF.
 *
 * @author Gerhard Petracek
 */
@Typed()
class SerializableValidatorFactory implements ValidatorFactory, Serializable
{
    private static final long serialVersionUID = 58677422630598169L;

    private static final String VALIDATOR_FACTORY_KEY = "javax.faces.validator.beanValidator.ValidatorFactory";

    private transient ValidatorFactory standaloneValidatorFactory;

    private transient ValidatorFactory currentValidatorFactory;

    private transient boolean jsfInitialized = false;

    public SerializableValidatorFactory()
    {
    }

    public Validator getValidator()
    {
        return getValidatorFactory().getValidator();
    }

    public ValidatorContext usingContext()
    {
        return getValidatorFactory().usingContext();
    }

    public MessageInterpolator getMessageInterpolator()
    {
        return getValidatorFactory().getMessageInterpolator();
    }

    public TraversableResolver getTraversableResolver()
    {
        return getValidatorFactory().getTraversableResolver();
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory()
    {
        return getValidatorFactory().getConstraintValidatorFactory();
    }

    public <T> T unwrap(Class<T> tClass)
    {
        return getValidatorFactory().unwrap(tClass);
    }

    protected ValidatorFactory getValidatorFactory()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if(!this.jsfInitialized && facesContext == null &&
                this.currentValidatorFactory == null)
        {
            return getStandaloneValidatorFactory();
        }

        if(facesContext == null)
        {
            return this.standaloneValidatorFactory;
        }

        if(this.jsfInitialized)
        {
            return this.currentValidatorFactory;
        }
        else
        {
            this.jsfInitialized = true;
            return getJsfAwareValidatorFactory(facesContext);
        }
    }

    private ValidatorFactory getStandaloneValidatorFactory()
    {
        if(this.standaloneValidatorFactory == null)
        {
            this.standaloneValidatorFactory = Validation.buildDefaultValidatorFactory();
        }
        return this.standaloneValidatorFactory;
    }

    private ValidatorFactory getJsfAwareValidatorFactory(FacesContext facesContext)
    {
        if(this.currentValidatorFactory == null)
        {
            Map<String, Object> applicationMap = facesContext.getExternalContext().getApplicationMap();

            if (applicationMap.containsKey(VALIDATOR_FACTORY_KEY))
            {
                if (applicationMap.get(VALIDATOR_FACTORY_KEY) instanceof ValidatorFactory)
                {
                    this.currentValidatorFactory = (ValidatorFactory) applicationMap.get(VALIDATOR_FACTORY_KEY);
                }
            }

            if (this.currentValidatorFactory == null)
            {
                this.currentValidatorFactory = getStandaloneValidatorFactory();
                applicationMap.put(VALIDATOR_FACTORY_KEY, this.currentValidatorFactory);
            }
        }

        return this.currentValidatorFactory;
    }
}
