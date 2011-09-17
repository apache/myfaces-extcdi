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

import org.apache.myfaces.extensions.cdi.jpa.api.TransactionScoped;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * producer for a simple &#064;Default EntityManager
 */
@Dependent
public class TransactionScopedEntityManagerProducer {

    private @PersistenceContext(unitName = "test") EntityManager entityManager;

    public @Produces @TransactionScoped @TransactionScopeAware EntityManager createEntityManager()
    {
        return entityManager;
    }

    public void closeEntityManager(@Disposes @TransactionScopeAware EntityManager em)
    {
        em.close();
    }
}
