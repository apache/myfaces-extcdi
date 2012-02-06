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
package org.apache.myfaces.extensions.cdi.jpa.test;

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Callable;

/**
 * see {@link org.apache.myfaces.extensions.cdi.jpa.api.TransactionHelper}
 */
@ApplicationScoped
public class TestTransactionHelper
{
    public static TestTransactionHelper getInstance()
    {
        return BeanManagerProvider.getInstance().getContextualReference(TestTransactionHelper.class);
    }

    /**
     * see {@link org.apache.myfaces.extensions.cdi.jpa.api.TransactionHelper}
     */
    @Transactional(qualifier = TransactionScopeAware.class)
    public <T> T executeTransactional(Callable<T> callable) throws Exception
    {
        return callable.call();
    }
}
