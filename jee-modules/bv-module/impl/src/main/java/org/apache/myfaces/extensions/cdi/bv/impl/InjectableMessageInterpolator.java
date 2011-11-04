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

import javax.validation.MessageInterpolator;
import java.io.Serializable;
import java.util.Locale;

/**
 * {@link MessageInterpolator} which can be serialized
 */
class InjectableMessageInterpolator implements MessageInterpolator, Serializable
{
    private static final long serialVersionUID = -68329117991255147L;

    private InjectableValidatorFactory injectableValidatorFactory;

    /**
     * Constructor used by proxy libs
     */
    protected InjectableMessageInterpolator()
    {
    }

    InjectableMessageInterpolator(InjectableValidatorFactory injectableValidatorFactory)
    {
        this.injectableValidatorFactory = injectableValidatorFactory;
    }

    protected MessageInterpolator getMessageInterpolator()
    {
        return this.injectableValidatorFactory.getMessageInterpolator();
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public String interpolate(String s, Context context)
    {
        return getMessageInterpolator().interpolate(s, context);
    }

    /**
     * {@inheritDoc}
     */
    public String interpolate(String s, Context context, Locale locale)
    {
        return getMessageInterpolator().interpolate(s, context, locale);
    }
}
