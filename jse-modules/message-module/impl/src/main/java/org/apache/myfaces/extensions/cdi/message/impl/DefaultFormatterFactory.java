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

import org.apache.myfaces.extensions.cdi.message.api.Formatter;
import org.apache.myfaces.extensions.cdi.message.api.FormatterFactory;
import org.apache.myfaces.extensions.cdi.message.api.GenericConfig;
import org.apache.myfaces.extensions.cdi.message.impl.formatter.FormatterBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * TODO change to producer
 *
 * @author Manfred Geiler
 * @author Gerhard Petracek
 */
public class DefaultFormatterFactory implements FormatterFactory
{
    private static final long serialVersionUID = -7462205386564870045L;

    transient protected Logger logger = getLogger();

    private List<Formatter> formatters = new ArrayList<Formatter>();
    private Map<Class<?>, Formatter> formatterCache = null;
    private Map<FormatterConfigKey, GenericConfig> formatterConfigs =
            new ConcurrentHashMap<FormatterConfigKey, GenericConfig>();

    public synchronized FormatterFactory add(Formatter formatter)
    {
        this.formatters.add(formatter);
        formatterCache = null;
        return this;
    }

    public List<Formatter> reset()
    {
        List<Formatter> oldFormatters = Collections.unmodifiableList(this.formatters);
        this.formatters.clear();
        return oldFormatters;
    }

    public Formatter findFormatter(Class<?> type)
    {

        if (this.formatterCache != null && this.formatterCache.containsKey(type))
        {
            return this.formatterCache.get(type);
        }
        else
        {
            if (this.formatterCache == null)
            {
                this.formatterCache = new ConcurrentHashMap<Class<?>, Formatter>();
            }

            Formatter found = findFormatterFor(type, this.formatters);

            if (found == null)
            {
                getLogger().info("default formatter used for: " + type.getName());
                found = FormatterBuilder.createFormatter(type);
            }

            this.formatterCache.put(type, found);
            return found;
        }
    }

    public FormatterFactory addFormatterConfig(Class<?> type, GenericConfig formatterConfig)
    {
        addFormatterConfig(type, formatterConfig, Locale.getDefault());
        return this;
    }

    public FormatterFactory addFormatterConfig(Class<?> type, GenericConfig formatterConfig, Locale locale)
    {
        if (formatterConfig.containsProperty(Locale.class.getName()))
        {
            locale = formatterConfig.getProperty(Locale.class.getName(), Locale.class);
        }
        this.formatterConfigs.put(createKey(type, locale), formatterConfig);
        return this;
    }

    public GenericConfig findFormatterConfig(Class<?> type, Locale locale)
    {
        return this.formatterConfigs.get(createKey(type, locale));
    }

    private Formatter findFormatterFor(Class<?> type, Iterable<Formatter> formatters)
    {
        for (Formatter formatter : formatters)
        {
            if (formatter.isResponsibleFor(type))
            {
                return formatter;
            }
        }
        return null;
    }

    private FormatterConfigKey createKey(Class<?> type, Locale locale)
    {
        return new FormatterConfigKey(type, locale);
    }

    private Logger getLogger()
    {
        if(this.logger == null)
        {
            this.logger = Logger.getLogger(getClass().getName());
        }
        return this.logger;
    }

    class FormatterConfigKey implements Serializable
    {
        private static final long serialVersionUID = -6430653319283563370L;

        private Class type;
        private Locale locale;

        FormatterConfigKey(Class type, Locale locale)
        {
            this.type = type;
            this.locale = locale;
        }

        /*
         * generated
         */

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(o instanceof FormatterConfigKey))
            {
                return false;
            }

            FormatterConfigKey that = (FormatterConfigKey) o;

            if (!locale.equals(that.locale))
            {
                return false;
            }
            //noinspection RedundantIfStatement
            if (!type.equals(that.type))
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = type.hashCode();
            result = 31 * result + locale.hashCode();
            return result;
        }
    }
}
