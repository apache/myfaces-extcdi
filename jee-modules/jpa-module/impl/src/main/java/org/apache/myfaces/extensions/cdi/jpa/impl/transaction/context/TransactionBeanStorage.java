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
package org.apache.myfaces.extensions.cdi.jpa.impl.transaction.context;

import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import java.util.HashMap;
import java.util.Map;

/**
 * This bean stores information about
 * &#064;{@link org.apache.myfaces.extensions.cdi.jpa.api.TransactionScoped}
 * contextual instances, their {@link javax.enterprise.context.spi.CreationalContext} etc
 */
@RequestScoped
public class TransactionBeanStorage
{

    private Map<String, Map<Bean, TransactionBeanBag>> storedBeans =
            new HashMap<String, Map<Bean, TransactionBeanBag>>();

    private Map<Bean, TransactionBeanBag> activeBeanBag;

    /**
     * Start the TransactionScope with the given qualifier
     * @param transactionKey
     */
    public void startTransactionScope(String transactionKey)
    {

    }

    /**
     * End the TransactionScope with the given qualifier.
     * This will subsequently destroy all beans which are stored
     * in the context
     * @param transactionKey
     */
    public void endTransactionScope(String transactionKey)
    {
        //X TODO
    }

    /**
     * Activate the TransactionScope with the given qualifier.
     * This is needed if a subsequently invoked &#064;Transactional
     * method will switch to another persistence unit.
     *
     * @param transactionKey
     */
    public void activateTransactionScope(String transactionKey)
    {
        //X TODO
    }

    public Map<Bean, TransactionBeanBag> getActiveBeanBags()
    {
        return activeBeanBag;
    }

    /**
     * At the end of the request we will destroy all beans still
     * stored in the context.
     */
    @PreDestroy
    protected void requestEnded()
    {
        //X TODO
    }
}

