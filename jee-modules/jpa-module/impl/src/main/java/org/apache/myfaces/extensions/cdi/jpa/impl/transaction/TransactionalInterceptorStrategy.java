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
package org.apache.myfaces.extensions.cdi.jpa.impl.transaction;

import org.apache.myfaces.extensions.cdi.core.impl.util.AnyLiteral;
import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;
import org.apache.myfaces.extensions.cdi.jpa.impl.PersistenceHelper;
import org.apache.myfaces.extensions.cdi.jpa.impl.spi.PersistenceStrategy;
import org.apache.myfaces.extensions.cdi.jpa.impl.transaction.context.TransactionBeanStorage;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Default implementation of our pluggable PersistenceStrategy.
 * It supports nested Transactions with the MANDATORY behaviour.</p>
 *
 * <p>The outermost &#064;Transactional interceptor for the given
 * {@link javax.inject.Qualifier} will open an {@link javax.persistence.EntityTransaction}
 * and the outermost &#064;Transactional interceptor for <b>all</b>
 * EntityManagers will flush and subsequently close all open transactions.</p>
 *
 * <p>If an Exception occurs in flushing the EntityManagers or any other Exception
 * gets thrown inside the intercepted method chain and <i>not</i> gets catched
 * until the outermost &#064;Transactional interceptor gets reached, then all
 * open transactions will get rollbacked.</p>
 *
 * <p>If you like to implement your own PersistenceStrategy, then use the
 * standard CDI &#064;Alternative mechanism.</p>
 */
@Dependent
public class TransactionalInterceptorStrategy implements PersistenceStrategy
{
    private static final long serialVersionUID = -1432802805095533499L;

    private static final Logger LOGGER = Logger.getLogger(TransactionalInterceptorStrategy.class.getName());

    @Inject
    private BeanManager beanManager;

    /** key=qualifier name, value= reference counter */
    private static transient ThreadLocal<HashMap<String, AtomicInteger>> refCounterMaps =
            new ThreadLocal<HashMap<String, AtomicInteger>>();

    /** key=qualifier name, value= EntityManager */
    private static transient ThreadLocal<HashMap<String, EntityManager>> ems =
            new ThreadLocal<HashMap<String, EntityManager>>();


    public Object execute(InvocationContext invocationContext) throws Exception
    {
        Transactional transactionalAnnotation = extractTransactionalAnnotation(invocationContext);

        Class<? extends Annotation> qualifierClass = getTransactionQualifier(transactionalAnnotation);
        String qualifierKey = qualifierClass.getName();

        // the 'layer' of the transactional invocation, aka the refCounter for the current qualifier
        int transactionLayer = incrementRefCounter(qualifierKey);

        if (transactionLayer == 0)
        {
            if (TransactionBeanStorage.getStorage() == null)
            {
                TransactionBeanStorage.activateNewStorage();
            }

            // 0 indicates that a new Context needs to get started
            TransactionBeanStorage.getStorage().startTransactionScope(qualifierKey);
        }

        String previousTransactionKey = TransactionBeanStorage.getStorage().activateTransactionScope(qualifierKey);

        EntityManager entityManager = resolveEntityManagerForQualifier(qualifierClass);

        if(entityManager == null)
        {
            //fallback to support direct injection via @PersistenceContext
            entityManager = PersistenceHelper.tryToFindEntityManagerReference(invocationContext.getTarget());
        }

        storeEntityManagerForQualifier(qualifierKey, entityManager);

        EntityTransaction transaction = entityManager.getTransaction();

        // used to store any exception we get from the services
        Exception firstException = null;

        try
        {
            if(!transaction.isActive())
            {
                transaction.begin();
            }

            return invocationContext.proceed();
        }
        catch(Exception e)
        {
            firstException = e;

            // we only cleanup and rollback all open transactions in the outermost interceptor!
            // this way, we allow inner functions to catch and handle exceptions properly.
            if (isOutermostInterceptor())
            {
                HashMap<String, EntityManager> emsEntries = ems.get();
                for (Map.Entry<String, EntityManager> emsEntry: emsEntries.entrySet())
                {
                    EntityManager em = emsEntry.getValue();
                    transaction = em.getTransaction();
                    if (transaction != null && transaction.isActive())
                    {
                        try
                        {
                            transaction.rollback();
                        }
                        catch (Exception eRollback)
                        {
                            if(LOGGER.isLoggable(Level.SEVERE))
                            {
                                LOGGER.log(Level.SEVERE,
                                        "Got additional Exception while subsequently " +
                                                "rolling back other SQL transactions", eRollback);
                            }
                        }
                    }
                }

                // drop all EntityManagers from the ThreadLocal
                ems.remove();
            }

            // give any extensions a chance to supply a better error message
            e = prepareException(e);

            // rethrow the exception
            throw e;

        }
        finally
        {
            // will get set if we got an Exception while committing
            // in this case, we rollback all later transactions too.
            boolean commitFailed = false;

            // commit all open transactions in the outermost interceptor!
            // this is a 'JTA for poor men' only, and will not guaranty
            // commit stability over various databases!
            if (isOutermostInterceptor())
            {

                // only commit all transactions if we didn't rollback
                // them already
                if (firstException == null)
                {
                    // but first try to flush all the transactions and write the updates to the database
                    for (EntityManager em: ems.get().values())
                    {
                        transaction = em.getTransaction();
                        if(transaction != null && transaction.isActive())
                        {
                            try
                            {
                                if (!commitFailed)
                                {
                                    em.flush();
                                }
                            }
                            catch (Exception e)
                            {
                                firstException = e;
                                commitFailed = true;
                                break;
                            }
                        }
                    }

                    // and now either commit or rollback all transactions
                    for (Map.Entry<String, EntityManager> emEntry: ems.get().entrySet())
                    {
                        EntityManager em = emEntry.getValue();
                        transaction = em.getTransaction();
                        if(transaction != null && transaction.isActive())
                        {
                            try
                            {
                                if (!commitFailed)
                                {
                                    transaction.commit();
                                }
                                else
                                {
                                    transaction.rollback();
                                }
                            }
                            catch (Exception e)
                            {
                                firstException = e;
                                commitFailed = true;
                            }
                        }
                    }

                    ems.remove();
                    ems.set(null);

                    refCounterMaps.set(null);
                    refCounterMaps.remove();

                    // and now we close all open transactionscopes and reset the storage
                    TransactionBeanStorage oldStorage = TransactionBeanStorage.getStorage();
                    TransactionBeanStorage.resetStorage();

                    // we do this delayed to allow new transactions in a PreDestroy method
                    oldStorage.endAllTransactionScopes();
                }
            }
            else
            {
                // we are NOT the outermost TransactionInterceptor
                // so we have to re-activate the previous transaction
                TransactionBeanStorage.getStorage().activateTransactionScope(previousTransactionKey);
            }

            decrementRefCounter(qualifierKey);

            if (commitFailed)
            {
                //noinspection ThrowFromFinallyBlock
                throw firstException;
            }
        }
    }

    private EntityManager resolveEntityManagerForQualifier(Class<? extends Annotation> qualifierClass)
    {
        Bean<EntityManager> entityManagerBean = resolveEntityManagerBean(qualifierClass);

        if(entityManagerBean == null)
        {
            return null;
        }

        return (EntityManager) beanManager.getReference(entityManagerBean, EntityManager.class,
                                                                beanManager.createCreationalContext(entityManagerBean));
    }

    private void storeEntityManagerForQualifier(String qualifierKey, EntityManager entityManager)
    {
        if (ems.get() == null)
        {
            ems.set(new HashMap<String, EntityManager>());
        }

        ems.get().put(qualifierKey, entityManager);
    }

    /**
     * This method might get overridden in subclasses to supply better error messages.
     * This is useful if e.g. a JPA provider only provides a stubborn Exception for
     * their ConstraintValidationExceptions.
     * @param e
     * @return
     */
    protected Exception prepareException(Exception e)
    {
        return e;
    }

    /**
     * @return <code>true</code> if we are the outermost interceptor over all qualifiers.
     */
    private boolean isOutermostInterceptor()
    {
        HashMap<String, AtomicInteger> refCounterMap = refCounterMaps.get();
        return refCounterMap == null ||
               (refCounterMap.size() == 1 && refCounterMap.values().iterator().next().get() == 1);
    }

    /**
     * Increment the ref counter for the given classifier and return the
     * old value
     * @param qualifierKey name of the qualifier used for the DB
     * @return the previous value of the refCounter
     */
    private int incrementRefCounter(String qualifierKey)
    {
        HashMap<String, AtomicInteger> refCounterMap = refCounterMaps.get();

        if (refCounterMap == null)
        {
            refCounterMap = new HashMap<String, AtomicInteger>();
            refCounterMaps.set(refCounterMap);
        }

        AtomicInteger refCounter = refCounterMap.get(qualifierKey);

        if (refCounter == null)
        {
            refCounter = new AtomicInteger(0);
            refCounterMap.put(qualifierKey, refCounter);
        }

        return refCounter.incrementAndGet() - 1;
    }

    /**
     * Decrement the reference counter for the given classifier and
     * return the layer. Also cleans up the {@link #refCounterMaps}.
     *
     * @param qualifierKey
     * @return the layer number. 0 represents the outermost interceptor for the qualifier
     */
    private int decrementRefCounter(String qualifierKey)
    {
        HashMap<String, AtomicInteger> refCounterMap = refCounterMaps.get();
        if (refCounterMap == null)
        {
            return 0;
        }

        AtomicInteger refCounter = refCounterMap.get(qualifierKey);

        if (refCounter == null)
        {
            return 0;
        }

        int layer = refCounter.decrementAndGet();

        if (layer == 0)
        {
            refCounterMap.remove(qualifierKey);
        }

        if (refCounterMap.size() == 0)
        {
            refCounterMaps.set(null);
            refCounterMaps.remove();
        }

        return layer;
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

        for (Bean<?> currentEntityManagerBean : entityManagerBeans)
        {
            Set<Annotation> foundQualifierAnnotations = currentEntityManagerBean.getQualifiers();

            for (Annotation currentQualifierAnnotation : foundQualifierAnnotations)
            {
                if (currentQualifierAnnotation.annotationType().equals(qualifierClass))
                {
                    return (Bean<EntityManager>) currentEntityManagerBean;
                }
            }
        }
        return null;
    }
}
