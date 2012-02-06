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

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.Typed;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>This bean stores information about
 * &#064;{@link org.apache.myfaces.extensions.cdi.jpa.api.TransactionScoped}
 * contextual instances, their {@link javax.enterprise.context.spi.CreationalContext} etc.</p>
 *
 * <p>We use a RequestScoped bean because this way we don't need to take
 * care about cleaning up any ThreadLocals ourselves. This also makes sure that
 * we subsequently destroy any left over TransactionScoped beans (which should not happen,
 * but who knows). We also don't need to do any fancy synchronization stuff since
 * we are sure that we are always in the same Thread.</p>
 */
@Typed()
public class TransactionBeanStorage
{
    private static final Logger LOGGER = Logger.getLogger(TransactionBeanStorage.class.getName());

    private static ThreadLocal<TransactionBeanStorage> currentStorage = new ThreadLocal<TransactionBeanStorage>();

    private TransactionBeanStorage()
    {
    }

    /**
     * @return the storage for the current thread if there is one - null otherwise
     */
    public static TransactionBeanStorage getStorage()
    {
        return currentStorage.get();
    }

    /**
     * Creates a new storage for the current thread
     * @return the storage which was associated with the thread before - null if there was no storage
     */
    public static TransactionBeanStorage activateNewStorage()
    {
        TransactionBeanStorage previousStorage = currentStorage.get();
        currentStorage.set(new TransactionBeanStorage());
        return previousStorage;
    }

    /**
     * Removes the current storage
     */
    public static void resetStorage()
    {
        currentStorage.remove();
        currentStorage.set(null);
    }

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
    private Map<String, Stack<Map<Contextual, TransactionBeanEntry>>> storedTransactionContexts =
            new HashMap<String, Stack<Map<Contextual, TransactionBeanEntry>>>();

    private Map<Contextual, TransactionBeanEntry> activeTransactionContext;

    private String activeTransactionKey = null;

    /**
     * Start the TransactionScope with the given qualifier
     * @param transactionKey
     */
    public void startTransactionScope(String transactionKey)
    {
        if(LOGGER.isLoggable(Level.FINER))
        {
            LOGGER.finer( "starting TransactionScope " + transactionKey);
        }

        Stack<Map<Contextual, TransactionBeanEntry>> transStack = storedTransactionContexts.get(transactionKey);

        if (transStack == null)
        {
            transStack = new Stack<Map<Contextual, TransactionBeanEntry>>();
            storedTransactionContexts.put(transactionKey, transStack);
        }

        transStack.push(new HashMap<Contextual, TransactionBeanEntry>());
    }

    /**
     * End the TransactionScope with the given qualifier.
     * This will subsequently destroy all beans which are stored
     * in the context
     * @param transactionKey
     */
    public void endTransactionScope(String transactionKey)
    {
        if(LOGGER.isLoggable(Level.FINER))
        {
            LOGGER.finer( "ending TransactionScope " + transactionKey);
        }

        // drop the context from the storage
        Stack<Map<Contextual, TransactionBeanEntry>> transStack = storedTransactionContexts.get(transactionKey);
        Map<Contextual, TransactionBeanEntry> beans = transStack.pop();
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
        if(LOGGER.isLoggable(Level.FINER))
        {
            LOGGER.finer( "activating TransactionScope " + transactionKey);
        }

        //can be null on the topmost stack-layer
        if (transactionKey == null && this.activeTransactionKey == null)
        {
            //TODO refactor it to a reset method
            activeTransactionKey = null;
            activeTransactionContext = new HashMap<Contextual, TransactionBeanEntry>();
            return null;
        }

        String oldTransactionContextKey = activeTransactionKey;

        if (transactionKey == null)
        {
            transactionKey = this.activeTransactionKey;
        }

        Stack<Map<Contextual, TransactionBeanEntry>> transStack = storedTransactionContexts.get(transactionKey);

        if (transStack == null)
        {
            throw new IllegalStateException("Cannot activate TransactionScope with key " + transactionKey);
        }

        activeTransactionContext =  transStack.peek();
        activeTransactionKey = transactionKey;
        return oldTransactionContextKey;
    }

    public String getActiveTransactionKey()
    {
        return activeTransactionKey;
    }

    /**
     * This will destroy all stored transaction contexts.
     *
     */
    public void endAllTransactionScopes()
    {
        if(LOGGER.isLoggable(Level.FINER))
        {
            LOGGER.finer( "destroying all TransactionScopes");
        }

        for (Stack<Map<Contextual, TransactionBeanEntry>> transStack : storedTransactionContexts.values())
        {
            while (!transStack.isEmpty())
            {
                Map<Contextual, TransactionBeanEntry> beans = transStack.pop();
                destroyBeans(beans);
            }
        }

        // we also need to clean our active context info
        storedTransactionContexts.clear();
        activeTransactionContext = null;
        activeTransactionKey = null;
    }


    /**
     * @return the Map which represents the currently active Context content.
     */
    public Map<Contextual, TransactionBeanEntry> getActiveTransactionContext()
    {
        return activeTransactionContext;
    }

    /**
     * Properly destroy all the given beans.
     * @param activeBeans
     */
    private void destroyBeans(Map<Contextual, TransactionBeanEntry> activeBeans)
    {
        for (TransactionBeanEntry beanBag : activeBeans.values())
        {
            beanBag.getBean().destroy(beanBag.getContextualInstance(), beanBag.getCreationalContext());
        }
    }
}

