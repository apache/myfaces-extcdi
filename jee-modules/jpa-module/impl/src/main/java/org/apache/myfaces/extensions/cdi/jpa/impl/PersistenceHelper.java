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

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Typed;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper which provides util methods for
 * {@link org.apache.myfaces.extensions.cdi.jpa.impl.transaction.TransactionalInterceptorStrategy} and
 * {@link LegacyTransactionalInterceptorStrategy}
 */
@Typed()
public class PersistenceHelper
{
    //don't use final in interceptors
    private static String noFieldMarker = PersistenceHelper.class.getName() + ":DEFAULT_FIELD";

    private static transient volatile Map<ClassLoader, Map<String, PersistenceContextMetaEntry>>
            persistenceContextMetaEntries =
            new ConcurrentHashMap<ClassLoader, Map<String, PersistenceContextMetaEntry>>();

    private PersistenceHelper()
    {
        //prevent instantiation
    }

    /**
     * Analyzes the given instance and returns the found reference to an injected {@link EntityManager}
     * or null otherwise
     * @param target instance to analyze
     * @return the injected entity-manager or null otherwise
     */
    public static EntityManager tryToFindEntityManagerReference(Object target)
    {
        EntityManagerEntry entityManagerEntry = tryToFindEntityManagerEntryInTarget(target);

        if(entityManagerEntry == null)
        {
            return null;
        }
        return entityManagerEntry.getEntityManager();
    }

    /*
    * needed for special add-ons - don't change it!
    */
    static EntityManagerEntry tryToFindEntityManagerEntryInTarget(Object target)
    {
        Map<String, PersistenceContextMetaEntry> mapping = persistenceContextMetaEntries.get(getClassLoader());

        mapping = initMapping(mapping);

        String key = target.getClass().getName();
        PersistenceContextMetaEntry persistenceContextEntry = mapping.get(key);

        if( persistenceContextEntry != null && noFieldMarker.equals(persistenceContextEntry.getFieldName()))
        {
            return null;
        }

        if(persistenceContextEntry == null)
        {
            persistenceContextEntry = findPersistenceContextEntry(target.getClass());

            if(persistenceContextEntry == null)
            {
                mapping.put(key, new PersistenceContextMetaEntry(
                        Object.class, noFieldMarker, Default.class.getName(), false));
                return null;
            }

            mapping.put(key, persistenceContextEntry);
        }

        Field entityManagerField;
        try
        {
            entityManagerField = persistenceContextEntry.getSourceClass()
                    .getDeclaredField(persistenceContextEntry.getFieldName());
        }
        catch (NoSuchFieldException e)
        {
            //TODO add logging in case of project stage dev.
            return null;
        }

        entityManagerField.setAccessible(true);
        try
        {
            EntityManager entityManager = (EntityManager)entityManagerField.get(target);
            return new EntityManagerEntry(entityManager, persistenceContextEntry);
        }
        catch (IllegalAccessException e)
        {
            //TODO add logging in case of project stage dev.
            return null;
        }
    }

    private static synchronized Map<String, PersistenceContextMetaEntry> initMapping(
            Map<String, PersistenceContextMetaEntry> mapping)
    {
        if(mapping == null)
        {
            mapping = new ConcurrentHashMap<String, PersistenceContextMetaEntry>();
            persistenceContextMetaEntries.put(getClassLoader(), mapping);
        }
        return mapping;
    }

    private static PersistenceContextMetaEntry findPersistenceContextEntry(Class target)
    {
        //TODO support other injection types
        Class currentParamClass = target;
        PersistenceContext persistenceContext;
        while (currentParamClass != null && !Object.class.getName().equals(currentParamClass.getName()))
        {
            for(Field currentField : currentParamClass.getDeclaredFields())
            {
                persistenceContext = currentField.getAnnotation(PersistenceContext.class);
                if(persistenceContext != null)
                {
                    return new PersistenceContextMetaEntry(
                                   currentParamClass,
                                   currentField.getName(),
                                   persistenceContext.unitName(),
                                   PersistenceContextType.EXTENDED.equals(persistenceContext.type()));
                }
            }
            currentParamClass = currentParamClass.getSuperclass();
        }

        return null;
    }

    private static ClassLoader getClassLoader()
    {
        return ClassUtils.getClassLoader(null);
    }
}
