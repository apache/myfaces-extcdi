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

import org.apache.myfaces.extensions.cdi.jpa.impl.spi.PersistenceStrategy;
import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;
import org.apache.myfaces.extensions.cdi.core.impl.util.AnyLiteral;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.inject.Inject;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.Default;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.interceptor.InvocationContext;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * @author Mark Struberg
 * @author Gerhard Petracek
 */
@Dependent
public class DefaultTransactionalInterceptorStrategy implements PersistenceStrategy
{
    private static final long serialVersionUID = -1432802805095533499L;

    //don't use final in interceptors
    private static String noFieldMarker = TransactionalInterceptor.class.getName() + ":DEFAULT_FIELD";

    //all implementations will be serializable
    @Inject
    private BeanManager beanManager;

    private static transient ThreadLocal<AtomicInteger> refCount = new ThreadLocal<AtomicInteger>();

    private transient Logger logger = Logger.getLogger(getClass().getName());

    /** key=qualifier name, value= EntityManager */
    private static transient ThreadLocal<HashMap<String, EntityManager>> entityManagerMap =
            new ThreadLocal<HashMap<String, EntityManager>>();

    private static transient volatile Map<ClassLoader, Map<String, PersistenceContextMetaEntry>>
            persistenceContextMetaEntries =
            new ConcurrentHashMap<ClassLoader, Map<String, PersistenceContextMetaEntry>>();

    //TODO for mark
    /** 1 ms  in nanoTime ticks */
    //static final long LONG_MILLISECOND = 1000000L;
    //static final long LONG_RUNNING_THRESHOLD = 300L * LONG_MILLISECOND;

    /**
     * {@inheritDoc}
     */
    public Object execute(InvocationContext context) throws Exception
    {
        Transactional transactionalAnnotation = extractTransactionalAnnotation(context);

        Class<? extends Annotation> qualifierClass = getTransactionQualifier(transactionalAnnotation);

        Bean<EntityManager> entityManagerBean = resolveEntityManagerBean(qualifierClass);

        EntityManagerEntry entityManagerEntry = null;
        EntityManager entityManager;

        String entityManagerId = qualifierClass.getName();
        if (entityManagerBean == null)
        {
            //support for special add-ons which introduce backward compatibility - resolves the injected entity manager
            entityManagerEntry = tryToFindEntityManagerEntryInTarget(context.getTarget());

            if(entityManagerEntry == null)
            {
                throw unsupportedUsage(context);
            }

            entityManager = entityManagerEntry.getEntityManager();
            entityManagerId = entityManagerEntry.getPersistenceContextEntry().getUnitName();

            //might happen due to special add-ons - don't change it!
            if(entityManager == null)
            {
                //TODO log warning in project stage dev.
                return context.proceed();
            }
        }
        else
        {
            entityManager = (EntityManager) this.beanManager.getReference(entityManagerBean, EntityManager.class,
                    this.beanManager.createCreationalContext(entityManagerBean));
        }

        if (entityManagerMap.get() == null)
        {
            entityManagerMap.set(new HashMap<String, EntityManager>());
        }
        entityManagerMap.get().put(entityManagerId, entityManager);
        // log.info("growing: " + ems.get().size());

        if (refCount.get() == null)
        {
            refCount.set(new AtomicInteger(0));
        }

        return startProcess(context, entityManagerEntry, entityManager);
    }

    protected Transactional extractTransactionalAnnotation(InvocationContext context)
    {
        Transactional transactionalAnnotation = context.getMethod().getAnnotation(Transactional.class);

        if (transactionalAnnotation == null)
        {
            transactionalAnnotation = context.getTarget().getClass().getAnnotation(Transactional.class);
        }
        return transactionalAnnotation;
    }

    protected Class<? extends Annotation> getTransactionQualifier(Transactional transactionalAnnotation)
    {
        Class<? extends Annotation> qualifierClass = Default.class;
        if (transactionalAnnotation != null)
        {
            qualifierClass = transactionalAnnotation.qualifier();
        }
        return qualifierClass;
    }

    protected Bean<EntityManager> resolveEntityManagerBean(Class<? extends Annotation> qualifierClass)
    {
        Set<Bean<?>> entityManagerBeans = beanManager.getBeans(EntityManager.class, new AnyLiteral());
        if (entityManagerBeans == null)
        {
            entityManagerBeans = new HashSet<Bean<?>>();
        }
        Bean<EntityManager> entityManagerBean = null;

        it:
        for (Bean<?> currentEntityManagerBean : entityManagerBeans)
        {
            Set<Annotation> foundQualifierAnnotations = currentEntityManagerBean.getQualifiers();

            for (Annotation currentQualifierAnnotation : foundQualifierAnnotations)
            {
                if (currentQualifierAnnotation.annotationType().equals(qualifierClass))
                {
                    entityManagerBean = (Bean<EntityManager>) currentEntityManagerBean;
                    break it;
                }
            }
        }
        return entityManagerBean;
    }

    protected Object startProcess(InvocationContext context,
                                  EntityManagerEntry entityManagerEntry,
                                  EntityManager entityManager) throws Exception
    {
        EntityTransaction transaction = entityManager.getTransaction();

        if(entityManagerEntry != null)
        {
            //only in case of add-ons synchronize
            //-> nested calls (across beans) which share the same entity manager aren't supported
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (entityManager)
            {
                return proceedMethodInTransaction(context,
                                                  entityManagerEntry,
                                                  entityManager,
                                                  transaction);
            }
        }
        //we don't have a shared entity manager
        return proceedMethodInTransaction(context,
                                          entityManagerEntry,
                                          entityManager,
                                          transaction);
    }

    protected Object proceedMethodInTransaction(InvocationContext context,
                                                EntityManagerEntry entityManagerEntry,
                                                EntityManager entityManager,
                                                EntityTransaction transaction) throws Exception
    {
        // used to store any exception we get from the services
        Exception firstException = null;

        try
        {
            beginTransaction(entityManager);
            refCount.get().incrementAndGet();

            return context.proceed();

        }
        catch(Exception e)
        {
            firstException = e;

            // we only cleanup and rollback all open transactions in the outermost interceptor!
            // this way, we allow inner functions to catch and handle exceptions properly.
            if (refCount.get().intValue() == 1)
            {
                for (EntityManager currentEntityManager : entityManagerMap.get().values())
                {
                    try
                    {
                        rollbackTransaction(currentEntityManager);
                    }
                    catch (Exception eRollback)
                    {
                        this.logger.log(Level.SEVERE, "Got additional Exception while subsequently " +
                                "rolling back other SQL transactions", eRollback);
                    }
                }

                refCount.remove();

                // drop all EntityManagers from the ThreadLocal
                entityManagerMap.remove();
            }

            // rethrow the exception
            throw e;

        }
        finally
        {
            if (refCount.get() != null)
            {
                refCount.get().decrementAndGet();


                // will get set if we got an Exception while committing
                // in this case, we rollback all later transactions too.
                boolean commitFailed = false;

                // commit all open transactions in the outermost interceptor!
                // this is a 'JTA for poor men' only, and will not guaranty
                // commit stability over various databases!
                if (refCount.get().intValue() == 0)
                {
                    // only commit all transactions if we didn't rollback
                    // them already
                    if (firstException == null)
                    {
                        // first flush all EntityManagers
                        for (EntityManager currentEntityManager : entityManagerMap.get().values())
                        {
                            try
                            {
                                currentEntityManager.flush();
                            }
                            catch (Exception e)
                            {
                                firstException = e;
                                commitFailed = true;
                                break;
                            }
                        }

                        // and finally do all the commits
                        for (EntityManager currentEntityManager : entityManagerMap.get().values())
                        {
                            transaction = currentEntityManager.getTransaction();
                            if(transaction != null && transaction.isActive())
                            {
                                try
                                {
                                    if (!commitFailed)
                                    {
                                        commitTransaction(currentEntityManager);
                                    }
                                    else
                                    {
                                        rollbackTransaction(currentEntityManager);
                                    }
                                }
                                catch (Exception e)
                                {
                                    firstException = e;
                                    commitFailed = true;
                                }
                            }
                        }
                    }

                    // finally remove all ThreadLocals
                    refCount.remove();
                    entityManagerMap.remove();

                    if(!commitFailed)
                    {
                        endProcess(entityManagerEntry, entityManager, null);
                    }
                    else
                    {
                        endProcess(entityManagerEntry, entityManager, firstException);
                    }
                }
            }
        }
    }

    protected void beginTransaction(EntityManager entityManager)
    {
        EntityTransaction transaction = entityManager.getTransaction();

        if(!transaction.isActive())
        {
            transaction.begin();
        }
    }

    protected void commitTransaction(EntityManager entityManager)
    {
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.commit();
    }

    protected void rollbackTransaction(EntityManager entityManager)
    {
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction != null && transaction.isActive())
        {
            transaction.rollback();
        }
    }

    protected void endProcess(EntityManagerEntry entityManagerEntry,
                              EntityManager entityManager,
                              Exception exception) throws Exception
    {
        if (exception != null)
        {
            throw exception;
        }
        else
        {
            //commit was successful and entity manager of bean was used
            //(and not an entity manager of a producer) which isn't of type extended
            if(entityManagerEntry != null && entityManager != null && entityManager.isOpen() &&
                    !entityManagerEntry.getPersistenceContextEntry().isExtended())
            {
                entityManager.clear();
            }
        }
    }

    /*
     * needed for special add-ons - don't change it!
     */
    private EntityManagerEntry tryToFindEntityManagerEntryInTarget(Object target)
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

    private synchronized Map<String, PersistenceContextMetaEntry> initMapping(
            Map<String, PersistenceContextMetaEntry> mapping)
    {
        if(mapping == null)
        {
            mapping = new ConcurrentHashMap<String, PersistenceContextMetaEntry>();
            persistenceContextMetaEntries.put(getClassLoader(), mapping);
        }
        return mapping;
    }

    private PersistenceContextMetaEntry findPersistenceContextEntry(Class target)
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

    private ClassLoader getClassLoader()
    {
        return ClassUtils.getClassLoader(null);
    }

    private IllegalStateException unsupportedUsage(InvocationContext context)
    {
        String target;

        target = context.getTarget().getClass().getName();
        if (context.getMethod().isAnnotationPresent(Transactional.class))
        {
            target += "." + context.getMethod().getName();
        }

        return new IllegalStateException("Please check your implementation! " +
                "There is no @Transactional or @Transactional(MyQualifier.class) or @PersistenceContext at "
                + target + " Please check the documentation for the correct usage or contact the mailing list. " +
                "Hint: @Transactional just allows one qualifier -> using multiple Entity-Managers " +
                "(-> different qualifiers) within ONE intercepted method isn't supported.");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
