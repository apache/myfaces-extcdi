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
package org.apache.myfaces.extensions.cdi.message.test;

import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Gerhard Petracek
 */
class TestMessageResolver implements MessageResolver
{
    private static final String TEST_MESSAGES = "org.apache.myfaces.extensions.cdi.message.test.messages";

    public String getMessage(String key, Locale locale, Map<Class, Class<? extends MessagePayload>> messagePayload)
    {
        if (!isKey(key))
        {
            return key;
        }

        try
        {
            key = extractKey(key);
            return ResourceBundle.getBundle(TEST_MESSAGES, locale, getClassLoader()).getString(key);
        }
        catch (MissingResourceException e)
        {
            return key;
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

    private ClassLoader getClassLoader()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
        {
            classLoader = TestMessageResolver.class.getClassLoader();
        }
        return classLoader;
    }
}
