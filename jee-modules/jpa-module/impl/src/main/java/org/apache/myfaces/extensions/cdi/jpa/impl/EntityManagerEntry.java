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
package org.apache.myfaces.extensions.cdi.jpa.impl;

import javax.persistence.EntityManager;

/**
 * separated from {@link PersistenceContextMetaEntry}
 * to allow the usage of {@link PersistenceContextMetaEntry} in the cache
 * (without a reference to the entity manager)
 */
class EntityManagerEntry
{
    private EntityManager entityManager;
    private PersistenceContextMetaEntry persistenceContextEntry;

    EntityManagerEntry(EntityManager entityManager, PersistenceContextMetaEntry persistenceContextEntry)
    {
        this.entityManager = entityManager;
        this.persistenceContextEntry = persistenceContextEntry;
    }

    EntityManager getEntityManager()
    {
        return entityManager;
    }

    PersistenceContextMetaEntry getPersistenceContextEntry()
    {
        return persistenceContextEntry;
    }
}
