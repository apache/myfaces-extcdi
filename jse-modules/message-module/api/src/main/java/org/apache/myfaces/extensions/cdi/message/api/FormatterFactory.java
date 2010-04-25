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
package org.apache.myfaces.extensions.cdi.message.api;

import java.io.Serializable;
import java.util.Locale;

/**
 * creates and initialize a formatter for a given type
 *
 * @author Gerhard Petracek
 */
public interface FormatterFactory extends ConfigRegistry<Formatter, FormatterFactory>, Serializable
{
    /**
     * @param type the type of the instance which has to be formatted
     * @return an initialized formatter which is able to format instances of the given type (or a default formatter)
     */
    Formatter findFormatter(Class<?> type);

    /**
     * allows to add a config for formatting the given type
     *
     * @param type the type the config belongs to
     * @param formatterConfig config for the formatter of the given type
     * @return the instance of the factory to allow a fluent api
     */
    FormatterFactory addFormatterConfig(Class<?> type, GenericConfig formatterConfig);

    /**
     * allows to add a config for formatting the given type
     *
     * @param type the type the config belongs to
     * @param formatterConfig config for the formatter of the given type
     * @param locale the locale the config belongs to
     * @return the instance of the factory to allow a fluent api
     */
    FormatterFactory addFormatterConfig(Class<?> type, GenericConfig formatterConfig, Locale locale);

    /**
     * @param type the type which has to be formatted and maybe need a config for it
     * @param locale the current locale
     * @return the config for a given type and locale - null otherwise
     */
    GenericConfig findFormatterConfig(Class<?> type, Locale locale);
}
