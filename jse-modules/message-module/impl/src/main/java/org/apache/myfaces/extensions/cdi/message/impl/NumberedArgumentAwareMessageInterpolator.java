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

import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO add ArgumentFilter
 */
public class NumberedArgumentAwareMessageInterpolator extends AbstractFormatterAwareMessageInterpolator
{
    private static final long serialVersionUID = 8699632465559596371L;

    /**
     * {@inheritDoc}
     */
    public String interpolate(MessageContext messageContext, String messageDescriptor, Serializable... arguments)
    {
        Serializable[] numberedArguments = extractNumberedArguments(arguments);

        if (numberedArguments.length > 0)
        {
            return formatMessage(messageContext, messageDescriptor, numberedArguments);
        }

        return messageDescriptor;
    }

    private Serializable[] extractNumberedArguments(Serializable[] arguments)
    {
        List<Serializable> result = new ArrayList<Serializable>();

        for (Serializable argument : arguments)
        {
            if (!(argument instanceof NamedArgument))
            {
                result.add(argument);
            }
        }

        return result.toArray(new Serializable[result.size()]);
    }

    //TODO add warning for unused arguments,...
    private String formatMessage(MessageContext messageContext, String messageDescriptor, Serializable[] arguments)
    {
        Object[] localizedArguments = null;
        Object argument;
        Object localizedArgument;

        for (int i = 0; i < arguments.length; i++)
        {
            argument = arguments[i];
            localizedArgument = formatAsString(messageContext, argument);

            if (argument != null)
            {
                if (localizedArguments == null)
                {
                    //TODO
                    localizedArguments = copyArguments(arguments);
                }
                localizedArguments[i] = localizedArgument;
            }
        }

        MessageFormat messageFormat = new MessageFormat(messageDescriptor, messageContext.getLocale());

        if (localizedArguments == null)
        {
            return messageFormat.format(arguments);
        }
        else
        {
            return messageFormat.format(localizedArguments);
        }
    }

    private Object[] copyArguments(Object[] arguments)
    {
        Object[] result = new Object[arguments.length];
        System.arraycopy(arguments, 0, result, 0, arguments.length);
        return result;
    }
}