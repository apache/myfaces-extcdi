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

import javax.validation.MessageInterpolator;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
class InjectableMessageInterpolator implements MessageInterpolator, Serializable
{
    private static final long serialVersionUID = -68329117991255147L;

    private transient MessageInterpolator wrapped;

    InjectableMessageInterpolator(MessageInterpolator wrapped)
    {
        this.wrapped = wrapped;
    }

    protected MessageInterpolator getWrapped()
    {
        if(this.wrapped == null)
        {
            this.wrapped = CodiUtils
                    .getContextualReferenceByClass(MessageInterpolator.class, new AdvancedLiteral());

            if(this.wrapped instanceof InjectableMessageInterpolator)
            {
                this.wrapped = ((InjectableMessageInterpolator)this.wrapped).getWrapped();
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
    public String interpolate(String s, Context context)
    {
        return getWrapped().interpolate(s, context);
    }

    /**
     * {@inheritDoc}
     */
    public String interpolate(String s, Context context, Locale locale)
    {
        return getWrapped().interpolate(s, context, locale);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
