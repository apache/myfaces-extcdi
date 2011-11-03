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

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;

import java.util.Map;

/**
 * <p>Configuration for the DataSource.
 * The <code>connectionId</code> parameter can be used to distinguish
 * between different databases.</p>
 *
 * <p>There are 2 ways to configure a DataSource
 *
 * <ol>
 *     <li>
 *         via JNDI lookup - specify the JNDI resource location for the DataSource via
 *         {@link #getJndiResourceName(String)}
 *     </li>
 *     <li>
 *         via JDBC connecion properties - This will be used if {@link #getJndiResourceName(String)}
 *         returns <code>null</code>. In this case you must specify the {@link #getDriverClassName(String)}
 *         and {@link #getConnectionProperties(String)}.
 *     </li>
 * </ol>
 *
 * </p>
 *
 *
 */
public interface DataSourceConfig extends CodiConfig
{

    /**
     * Return the JNDI resource name if the DataSource should get retrieved via JNDI.
     * If a native JDBC connection should get used, this method must return <code>null</code>.
     * And the JDBC connection properties must get set via
     * {@link #getDriverClassName(String)} and {@link #getConnectionProperties(String)}.
     *
     * @param connectionId used to distinguish between different databases.
     *
     * @return the JNDI lookup for the DataSource or <code>null</code> if a native
     *      JDBC connection should get used.
     */
    public String getJndiResourceName(String connectionId);

    /**
     * @param connectionId used to distinguish between different databases.
     * @return the fully qualified class name of the JDBC driver for the underlying connection
     */
    public String getDriverClassName(String connectionId);

    /**
     * @param connectionId used to distinguish between different databases.
     *
     * @return allows to configure additional connection properties which will
     *      get applied to the underlying JDBC driver.
     */
    public Map<String, String> getConnectionProperties(String connectionId);


}
