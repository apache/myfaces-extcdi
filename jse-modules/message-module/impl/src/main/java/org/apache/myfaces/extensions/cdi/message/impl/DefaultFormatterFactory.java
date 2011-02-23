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

import java.util.List;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * TODO change to producer
 *
 * @author Manfred Geiler
 * @author Gerhard Petracek
 */
public class DefaultFormatterFactory implements FormatterFactory
{
    private static final long serialVersionUID = -7462205386564870045L;

    private transient Logger logger = getLogger();

    private CopyOnWriteArrayList<Formatter> formatters = new CopyOnWriteArrayList<Formatter>();
    private ConcurrentHashMap<Class<?>, Formatter> formatterCache = null;
    private ConcurrentHashMap<FormatterConfigKey, GenericConfig> formatterConfigs =
            new ConcurrentHashMap<FormatterConfigKey, GenericConfig>();

    public synchronized FormatterFactory add(Formatter formatter)
    {
        if(!this.formatters.contains(formatter))
        {
            this.formatters.add(formatter);
            if(this.formatterCache != null)
            {
                this.formatterCache.clear();
            }
        }
        return this;
    }

    public synchronized List<Formatter> reset()
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

            Formatter found = findFormatterFor(type);

            if (found == null)
            {
                getLogger().info("default formatter used for: " + type.getName());
                found = FormatterBuilder.createFormatter(type);
            }

            if(found.isStateless())
            {
                this.formatterCache.put(type, found);
            }
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

    private Formatter findFormatterFor(Class<?> type)
    {
        for (Formatter formatter : this.formatters)
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

    protected Logger getLogger()
    {
        if(this.logger == null)
        {
            this.logger = Logger.getLogger(getClass().getName());
        }
        return this.logger;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
