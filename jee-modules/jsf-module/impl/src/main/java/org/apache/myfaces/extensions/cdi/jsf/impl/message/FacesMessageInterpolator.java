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
package org.apache.myfaces.extensions.cdi.jsf.impl.message;

import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.impl.CompositeMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.ELAwareMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.NamedArgumentMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.NumberedArgumentAwareMessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ELProvider;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ArgumentFilter;

import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
class FacesMessageInterpolator implements MessageInterpolator
{
    private static final long serialVersionUID = -5367321374822078592L;
    
    private MessageInterpolator messageInterpolator;

    FacesMessageInterpolator(ELProvider elProvider, ArgumentFilter argumentFilter)
    {
        if(elProvider != null)
        {
            this.messageInterpolator = new CompositeMessageInterpolator(
                    new ELAwareMessageInterpolator(elProvider, argumentFilter),
                    new NumberedArgumentAwareMessageInterpolator());
        }
        else
        {
            this.messageInterpolator = new CompositeMessageInterpolator(
                    new NamedArgumentMessageInterpolator(),
                    new NumberedArgumentAwareMessageInterpolator());
        }
    }

    /**
     * {@inheritDoc}
     */
    public String interpolate(MessageContext messageContext, String messageText, Serializable... arguments)
    {
        return this.messageInterpolator.interpolate(messageContext, messageText, arguments);
    }
}
