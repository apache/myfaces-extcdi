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

import javax.enterprise.inject.Typed;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * This class can be used for static config. and will resolve the final implementation at runtime.
 *
 * @author Gerhard Petracek
 */
@Typed()
public class ConfigurableDataSource extends AbstractConfigurableDataSource
{
    private AbstractConfigurableDataSource configurableDataSource;

    /**
     * {@inheritDoc}
     */
    protected DataSource resolveDataSource() throws SQLException
    {
        //only needed here because #configureDataSource will be called afterwards (in any case)
        this.configurableDataSource = BeanManagerProvider.getInstance()
                .getContextualReference(AbstractConfigurableDataSource.class);

        return this.configurableDataSource.resolveDataSource();
    }

    /**
     * {@inheritDoc}
     */
    protected void configureDataSource(DataSource dataSource)
    {
        this.configurableDataSource.configureDataSource(dataSource);
    }
}
