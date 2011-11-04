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
package org.apache.myfaces.extensions.cdi.jpa.impl.datasource;

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.JndiUtils;
import org.apache.myfaces.extensions.cdi.jpa.api.datasource.AbstractConfigurableDataSource;

import javax.enterprise.context.Dependent;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class DefaultConfigurableDataSource extends AbstractConfigurableDataSource
{
    /**
     * {@inheritDoc}
     */
    protected DataSource resolveDataSource() throws SQLException
    {
        String jndiLookupName = getDataSourceConfig().getJndiResourceName(connectionId);

        if (jndiLookupName != null && jndiLookupName.length() > 0)
        {
            return JndiUtils.lookup(jndiLookupName, DataSource.class);
        }

        // no JNDI, so we take the direct JDBC route.
        String jdbcDriverClass = getDataSourceConfig().getDriverClassName(connectionId);
        if (jdbcDriverClass == null || jdbcDriverClass.length() == 0)
        {
            throw new SQLException("Neither a JNDI location nor a JDBC driver class name is configured!");
        }

        return ClassUtils.tryToInstantiateClassForName(jdbcDriverClass, DataSource.class);
    }

    /**
     * {@inheritDoc}
     */
    protected void configureDataSource(DataSource dataSource)
    {
        Map<String, String> config = getDataSourceConfig().getConnectionProperties(connectionId);

        for (Map.Entry<String, String> configOption : config.entrySet())
        {
            setProperty(dataSource, configOption);
        }
    }

    protected void setProperty(DataSource dataSource, Map.Entry<String, String> configOption)
    {
        Method setterMethod = findSetterForProperty(dataSource, configOption);

        if(setterMethod == null)
        {
            Logger logger = Logger.getLogger(getClass().getName());

            if(logger.isLoggable(Level.WARNING))
            {
                logger.warning(dataSource.getClass().getName() +
                        " has no setter for property '" + configOption.getKey() + "'. Property gets ignored.");
            }
            return;
        }

        try
        {
            setterMethod.invoke(dataSource, configOption.getValue());
        }
        catch (Exception e)
        {
            throw new IllegalStateException(setterMethod.getDeclaringClass().getName() + "#" + setterMethod.getName() +
                    " failed", e);
        }
    }

    protected Method findSetterForProperty(DataSource dataSource, Map.Entry<String, String> configOption)
    {
        for (Method method : dataSource.getClass().getMethods())
        {
            if (method.getParameterTypes().length == 1 &&
                    String.class.isAssignableFrom(method.getParameterTypes()[0]) &&
                    method.getName().equalsIgnoreCase("set" + configOption.getKey())) //simple detection
            {
                return method;
            }
        }
        return null;
    }
}
