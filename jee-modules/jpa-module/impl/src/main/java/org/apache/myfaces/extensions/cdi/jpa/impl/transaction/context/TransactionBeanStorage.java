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
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Contextual;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * <p>This bean stores information about
 * &#064;{@link org.apache.myfaces.extensions.cdi.jpa.api.TransactionScoped}
 * contextual instances, their {@link javax.enterprise.context.spi.CreationalContext} etc.</p>
 *
 * <p>We use a RequestScoped bean because this way we don't need to take
 * care about cleaning up any ThreadLocals ourselfs. This also makes sure that
 * we subsequently destroy any left over TransactionScoped beans (which should not happen,
 * but who knows). We also don't need to do any fancy synchronization stuff since
 * we are sure that we are always in the same Thread.</p>
 */
@RequestScoped
public class TransactionBeanStorage
{
    private static final Logger LOGGER = Logger.getLogger(TransactionBeanStorage.class.getName());


    /**
     * This is the actual bean storage.
     * The structure is:
     * <ol>
     *     <li>transactioKey identifies the 'database qualifier'</li>
     *     <li>transactionKey -> Stack: we need the Stack because of REQUIRES_NEW, etc</li>
     *     <li>top Element in the Stack -> Context beans for the transactionKey</li>
     * </ol>
     *
     */
    private Map<String, Stack<Map<Contextual, TransactionBeanBag>>> storedTransactionContexts =
            new HashMap<String, Stack<Map<Contextual, TransactionBeanBag>>>();

    private Map<Contextual, TransactionBeanBag> activeTransactionContext;

    private String activeTransactionKey = null;

    /**
     * Start the TransactionScope with the given qualifier
     * @param transactionKey
     */
    public void startTransactionScope(String transactionKey)
    {
        LOGGER.finer( "starting TransactionScope " + transactionKey);
        Stack<Map<Contextual, TransactionBeanBag>> transStack = storedTransactionContexts.get(transactionKey);

        if (transStack == null)
        {
            transStack = new Stack<Map<Contextual, TransactionBeanBag>>();
            storedTransactionContexts.put(transactionKey, transStack);
        }

        transStack.push(new HashMap<Contextual, TransactionBeanBag>());
    }

    /**
     * End the TransactionScope with the given qualifier.
     * This will subsequently destroy all beans which are stored
     * in the context
     * @param transactionKey
     */
    public void endTransactionScope(String transactionKey)
    {
        LOGGER.finer( "ending TransactionScope " + transactionKey);

        // drop the context from the storage
        Stack<Map<Contextual, TransactionBeanBag>> transStack = storedTransactionContexts.get(transactionKey);
        Map<Contextual, TransactionBeanBag> beans = transStack.pop();
        destroyBeans(beans);

        if (transStack.size() == 0)
        {
            storedTransactionContexts.remove(transactionKey);
        }

    }

    /**
     * Activate the TransactionScope with the given qualifier.
     * This is needed if a subsequently invoked &#064;Transactional
     * method will switch to another persistence unit.
     * This method must also be invoked when the transaction just got started
     * with {@link #startTransactionScope(String)}.
     *
     * @param transactionKey
     * @return the transactionKey of the previously activated transaction or <code>null</code> if non exists
     */
    public String activateTransactionScope(String transactionKey)
    {
        LOGGER.finer( "activating TransactionScope " + transactionKey);

        if (transactionKey == null)
        {
            activeTransactionKey = null;
            activeTransactionContext = null;
            return null;
        }

        Stack<Map<Contextual, TransactionBeanBag>> transStack = storedTransactionContexts.get(transactionKey);
        if (transStack == null)
        {
            throw new ContextNotActiveException("Cannot activate TransactionScope with key " + transactionKey);
        }

        activeTransactionContext =  transStack.peek();
        String oldTransactionContextKey = activeTransactionKey;
        activeTransactionKey = transactionKey;
        return oldTransactionContextKey;
    }


    /**
     * This will destroy all stored transaction contexts.
     *
     */
    public void endAllTransactionScopes()
    {
        LOGGER.finer( "destroying all TransactionScopes");

        for (Stack<Map<Contextual, TransactionBeanBag>> transStack : storedTransactionContexts.values())
        {
            while (!transStack.isEmpty())
            {
                Map<Contextual, TransactionBeanBag> beans = transStack.pop();
                destroyBeans(beans);
            }
        }

        // we also need to clean our active context info
        activeTransactionContext = null;
        activeTransactionKey = null;
    }


    /**
     * @return the Map which represents the currently active Context content.
     */
    public Map<Contextual, TransactionBeanBag> getActiveTransactionContext()
    {
        return activeTransactionContext;
    }

    /**
     * At the end of the request we will destroy all beans still
     * stored in the context.
     */
    @PreDestroy
    public void requestEnded()
    {
       endAllTransactionScopes();
    }

    /**
     * Properly destroy all the given beans.
     * @param activeBeans
     */
    private void destroyBeans(Map<Contextual, TransactionBeanBag> activeBeans)
    {
        for (TransactionBeanBag beanBag : activeBeans.values())
        {
            beanBag.getBean().destroy(beanBag.getContextualInstance(), beanBag.getCreationalContext());
        }
    }


}

