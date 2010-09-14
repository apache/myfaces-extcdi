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
import org.apache.myfaces.extensions.cdi.message.impl.spi.ELProvider;
import org.apache.myfaces.extensions.cdi.message.impl.spi.SimpleELContext;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ArgumentFilter;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manfred Geiler
 * @author Gerhard Petracek
 */
public class ELAwareMessageInterpolator extends AbstractFormatterAwareMessageInterpolator
{
    private static final long serialVersionUID = 3451979493272628741L;

    private static final Pattern MESSAGE_ARGS_PATTERN = Pattern.compile("\\{([^\\}]+?)\\}");

    private ELProvider elProvider;
    private ArgumentFilter argumentFilter;

    public ELAwareMessageInterpolator(ELProvider elProvider)
    {
        this(elProvider, null);
    }

    public ELAwareMessageInterpolator(ELProvider elProvider, ArgumentFilter argumentFilter)
    {
        this.elProvider = elProvider;

        if(argumentFilter != null)
        {
            this.argumentFilter = argumentFilter;
        }
        else
        {
            this.argumentFilter = new DefaultArgumentFilter();
        }
    }

    public String interpolate(MessageContext messageContext, String messageDescriptor, Serializable... arguments)
    {
        List<NamedArgument> namedArguments = addNamedArguments(arguments);

        if (namedArguments.size() > 0)
        {
            return interpolateNamedArguments(messageContext, messageDescriptor, namedArguments);
        }
        return messageDescriptor;
    }

    private List<NamedArgument> addNamedArguments(Serializable[] arguments)
    {
        List<NamedArgument> result = new ArrayList<NamedArgument>();

        for (Serializable argument : arguments)
        {
            if (argument instanceof NamedArgument)
            {
                result.add((NamedArgument) argument);
            }
        }

        return result;
    }

    //TODO add warning for unused arguments,...
    private synchronized String interpolateNamedArguments(MessageContext messageContext,
                                                          String messageDescriptor,
                                                          List<NamedArgument> namedArguments)
    {
        ExpressionFactory factory = this.elProvider.createExpressionFactory();
        SimpleELContext elContext = this.elProvider.createELContext(this.elProvider.createELResolver());

        for (NamedArgument argument : namedArguments)
        {
            Serializable value = argument.getValue();
            Class valueType = value != null ? value.getClass() : Object.class;
            elContext.setVariable(argument.getName(), factory.createValueExpression(value, valueType));
        }

        Matcher matcher = MESSAGE_ARGS_PATTERN.matcher(messageDescriptor);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find())
        {
            String resolvedArgumentValue;
            String expression = matcher.group(1);

            ValueExpression valueExpression =
                    factory.createValueExpression(elContext, "${" + expression + "}", Object.class);

            Object value = valueExpression.getValue(elContext);

            if(this.argumentFilter.isArgumentAllowed(expression, value))
            {
                resolvedArgumentValue = formatAsString(messageContext, value).toString();
            }
            else
            {
                //the default impl. is: '{' + expression + '}' 
                resolvedArgumentValue = this.argumentFilter.getDefaultValue(expression);
            }

            matcher.appendReplacement(buffer, "");
            buffer.append(resolvedArgumentValue);
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }
}