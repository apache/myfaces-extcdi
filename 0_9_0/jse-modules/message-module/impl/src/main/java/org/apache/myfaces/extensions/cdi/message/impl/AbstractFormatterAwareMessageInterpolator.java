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
package org.apache.myfaces.extensions.cdi.message.impl;

import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.Localizable;
import org.apache.myfaces.extensions.cdi.message.api.Formatter;

/**
 * @author Gerhard Petracek
 */
abstract class AbstractFormatterAwareMessageInterpolator implements MessageInterpolator
{
    @SuppressWarnings({"unchecked"})
    protected Object formatAsString(MessageContext messageContext, Object value)
    {
        if (value == null)
        {
            return null;
        }

        if (value instanceof String)
        {
            return value;
        }

        if (messageContext.config().getFormatterFactory() != null)
        {
            Formatter formatter = messageContext.config().getFormatterFactory().findFormatter(value.getClass());
            if (formatter != null)
            {
                return formatter.format(messageContext, value);
            }
        }

        if (value instanceof Localizable)
        {
            return ((Localizable) value).toString(messageContext);
        }

        return value;
    }
}
