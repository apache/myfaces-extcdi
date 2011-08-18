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
package org.apache.myfaces.extensions.cdi.jsf.impl.bv;

import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.impl.NamedArguments;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.validation.MessageInterpolator;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class InvalidValueAwareMessageInterpolator implements MessageInterpolator
{
    private static final String INVALID_VALUE_KEY = "invalidValue";

    private MessageInterpolator wrapped;

    @Inject
    @Jsf
    private MessageContext messageContext;

    @Inject
    private Logger logger;

    protected InvalidValueAwareMessageInterpolator()
    {
    }

    /**
     * Constructor for wrapping the given {@link MessageInterpolator}
     * @param messageInterpolator message-interpolator which should be wrapped
     */
    public InvalidValueAwareMessageInterpolator(MessageInterpolator messageInterpolator)
    {
        this.wrapped = messageInterpolator;
    }

    /**
     * {@inheritDoc}
     */
    public String interpolate(String messageTemplate, Context context)
    {
        return interpolate(messageTemplate, context, null);
    }

    /**
     * {@inheritDoc}
     */
    public String interpolate(String messageTemplate, Context context, Locale locale)
    {
        Map<String, Serializable> attributes = new HashMap<String, Serializable>();

        String separator = "$org.apache.myfaces.extensions.cdi$";
        boolean classLevelViolation = messageTemplate.contains(separator);
        if(classLevelViolation)
        {
            int index = messageTemplate.indexOf(separator);

            addInvalidValue(attributes, messageTemplate.substring(0, index));
            messageTemplate = messageTemplate.substring(index + separator.length());
        }
        else
        {
            attributes = buildAttributes(context, attributes, classLevelViolation);
        }

        if(attributes == null)
        {
            return this.wrapped.interpolate(messageTemplate, context, this.messageContext.getLocale());
        }

        String result = this.messageContext.message()
                .text(messageTemplate)
                .argument(NamedArguments.convert(attributes)).toText();

        if (messageTemplate.equals(result) || (result != null &&
                result.startsWith(MessageResolver.MISSING_RESOURCE_MARKER)))
        {
            return this.wrapped.interpolate(messageTemplate, context, this.messageContext.getLocale());
        }

        return result;
    }

    private Map<String, Serializable> buildAttributes(Context context,
                                                      Map<String, Serializable> attributes,
                                                      boolean classLevelViolation)
    {
        attributes.putAll(getCustomAttributesOfConstraint(context.getConstraintDescriptor().getAttributes()));

        if (!classLevelViolation && attributes.containsKey(INVALID_VALUE_KEY))
        {
            logWarning(context);
            return null;
        }

        addInvalidValue(attributes, (Serializable)context.getValidatedValue());

        return attributes;
    }

    private void logWarning(Context context)
    {
        String constraintName = ((Annotation)context.getConstraintDescriptor().getAnnotation())
                .annotationType().getName(); //cast needed for jdk 1.5 - don't remove it
        String warnMessage = constraintName + " uses 'invalidValue' as custom constraint attribute." +
                "So it isn't possible to use it as implicit key in the violation message.";

        this.logger.warning(warnMessage);
    }

    private Map<String, Serializable> getCustomAttributesOfConstraint(Map<String, Object> attributesOfConstraint)
    {
        Map<String, Serializable> attributes = new HashMap<String, Serializable>();

        for (Map.Entry<String, Object> entry : attributesOfConstraint.entrySet())
        {
            if (allowAttributeForMessageInterpolation(entry))
            {
                attributes.put(entry.getKey(), (Serializable) entry.getValue());
            }
        }

        return attributes;
    }

    private void addInvalidValue(Map<String, Serializable> attributes, Serializable invalidValue)
    {
        if (invalidValue != null)
        {
            attributes.put(INVALID_VALUE_KEY, invalidValue);
        }
        else
        {
            attributes.put(INVALID_VALUE_KEY, "null");
        }
    }

    private boolean allowAttributeForMessageInterpolation(Map.Entry<String, Object> entry)
    {
        return entry.getValue() instanceof Serializable
                && !"groups".equals(entry.getKey())
                && !"payload".equals(entry.getKey())
                && !"message".equals(entry.getKey());
    }
}
