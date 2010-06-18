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

import org.apache.myfaces.extensions.cdi.message.api.GenericConfig;
import org.apache.myfaces.extensions.cdi.message.impl.formatter.NumberFormatterConfigKeys;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
class TestEnglishNumberConfig implements GenericConfig
{
    private Map<String, Serializable> properties = new HashMap<String, Serializable>();
    private static final long serialVersionUID = 547837293567706390L;

    {
        this.properties.put(NumberFormatterConfigKeys.GROUPING_SEPARATOR_KEY, ",");
        this.properties.put(NumberFormatterConfigKeys.DECIMAL_SEPARATOR_KEY, ".");
        this.properties.put(NumberFormatterConfigKeys.MINIMUM_FRACTION_DIGITS_KEY, 2);
        this.properties.put(NumberFormatterConfigKeys.MINIMUM_INTEGER_DIGITS_KEY, 1);
    }

    public GenericConfig addProperty(String key, Serializable value)
    {
        this.properties.put(key, value);
        return this;
    }

    public Serializable getProperty(String key)
    {
        return this.properties.get(key);
    }

    public <T extends Serializable> T getProperty(String key, Class<T> targetType)
    {
        //noinspection unchecked
        return (T) getProperty(key);
    }

    public boolean containsProperty(String key)
    {
        return this.properties.containsKey(key);
    }
}