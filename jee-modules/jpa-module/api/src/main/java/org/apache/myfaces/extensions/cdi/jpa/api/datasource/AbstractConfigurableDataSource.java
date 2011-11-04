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
package org.apache.myfaces.extensions.cdi.jpa.api.datasource;

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * <p>This class can be used instead of a real DataSource.
 * It is a simple wrapper to hide any database configuration details
 * and make it configurable via CDI.</p>
 * <p/>
 * <p>The configuration itself will be provided via CDI mechanics.
 * To distinguish different databases, users can specify a
 * <code>connectionId</code>. If no <code>connectionId</code> is set,
 * the String <code>default</code> will be used</p>
 */
public abstract class AbstractConfigurableDataSource implements DataSource
{
    /**
     * config and settings are loaded only once.
     */
    private boolean loaded;

    /**
     * The connectionId allows to configure multiple databases.
     * This can e.g. be used to distinguish between a 'customer' and 'admin'
     * database.
     */
    protected String connectionId = "default";

    /**
     * The underlying configuration of the datasource
     */
    private DataSourceConfig dataSourceConfig;

    private volatile DataSource wrappedDataSource;

    public AbstractConfigurableDataSource()
    {
        loaded = false;
        dataSourceConfig = BeanManagerProvider.getInstance().getContextualReference(DataSourceConfig.class);
    }

    public void setConnectionId(String connectionId)
    {
        if (loaded)
        {
            throw new IllegalStateException("connectionId must not get changed after the DataSource was established");
        }
        this.connectionId = connectionId;
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() throws SQLException
    {
        return getConnection(null, null);
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection(String userName, String password) throws SQLException
    {
        if (wrappedDataSource == null)
        {
            initDataSource();
        }

        if (userName == null && password == null)
        {
            return wrappedDataSource.getConnection();
        }
        return wrappedDataSource.getConnection(userName, password);
    }


    /**
     * {@inheritDoc}
     */
    public PrintWriter getLogWriter() throws SQLException
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setLogWriter(PrintWriter printWriter) throws SQLException
    {
    }

    /**
     * {@inheritDoc}
     */
    public void setLoginTimeout(int loginTimeout) throws SQLException
    {
    }

    /**
     * {@inheritDoc}
     */
    public int getLoginTimeout() throws SQLException
    {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        if (isWrapperFor(iface))
        {
            return (T) this;
        }
        else
        {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return iface.isAssignableFrom(getClass());
    }

    /**
     * NEW JDK1.7 signature.
     * This makes sure that CODI can also get compiled using java-7.
     * This method is not actively used though.
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        throw new SQLFeatureNotSupportedException();
    }

    protected void initDataSource() throws SQLException
    {
        // double check lock idiom on volatile member is ok as of Java5
        if (wrappedDataSource != null)
        {
            return;
        }

        this.wrappedDataSource = resolveDataSource();

        if(this.wrappedDataSource == null)
        {
            throw new IllegalStateException("No DataSource found.");
        }

        configureDataSource(this.wrappedDataSource);
    }

    public DataSourceConfig getDataSourceConfig()
    {
        return dataSourceConfig;
    }

    protected abstract DataSource resolveDataSource() throws SQLException;

    protected abstract void configureDataSource(DataSource dataSource);
}
