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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.message;

import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * @author Gerhard Petracek
 */
class JsfAwareApplicationMessagesMessageResolver implements MessageResolver
{
    public String getMessage(String messageDescriptor,
                             Locale locale,
                             Map<Class, Class<? extends MessagePayload>> messagePayload)
    {
        if (!isKey(messageDescriptor))
        {
            return messageDescriptor;
        }

        try
        {
            messageDescriptor = extractKey(messageDescriptor);

            return JsfUtils.getCustomFacesMessageBundle(locale).getString(messageDescriptor);
        }
        catch (MissingResourceException e)
        {
            return defaultFacesMessage(messageDescriptor, locale);
        }
    }

    private boolean isKey(String key)
    {
        return key.startsWith("{") && key.endsWith("}");
    }

    private String extractKey(String key)
    {
        return key.substring(1, key.length() - 1);
    }

    private String defaultFacesMessage(String messageDescriptor, Locale locale)
    {
        try
        {
            return JsfUtils.getDefaultFacesMessageBundle(locale).getString(messageDescriptor);
        }
        catch (MissingResourceException e)
        {
            return messageDescriptor;
        }
    }
}
