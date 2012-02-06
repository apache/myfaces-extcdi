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


import org.apache.myfaces.extensions.cdi.jpa.api.TransactionScoped;
import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * CDI Context for managing &#064;{@link TransactionScoped} contextual instances.
 */
@Typed()
public class TransactionContext implements Context
{
    public <T> T get(Contextual<T> component)
    {
        Map<Contextual, TransactionBeanEntry> transactionBeanEntryMap = getTransactionBeanEntryMap();

        if (transactionBeanEntryMap == null)
        {
            return null;
        }

        TransactionBeanEntry transactionBeanEntry = transactionBeanEntryMap.get(component);
        if (transactionBeanEntry != null)
        {
            checkTransactionBeanEntry(transactionBeanEntry);
            return (T) transactionBeanEntry.getContextualInstance();
        }

        return null;
    }

    public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext)
    {
        if(TransactionBeanStorage.getStorage().getActiveTransactionContext() == null)
        {
            TransactionBeanStorage.activateNewStorage();
        }

        Map<Contextual, TransactionBeanEntry> transactionBeanEntryMap = getTransactionBeanEntryMap();

        if (transactionBeanEntryMap == null)
        {
            throw new ContextNotActiveException("Not accessed within a transactional method - use @" +
                    Transactional.class.getName());
        }

        TransactionBeanEntry transactionBeanEntry = transactionBeanEntryMap.get(component);
        if (transactionBeanEntry != null)
        {
            checkTransactionBeanEntry(transactionBeanEntry);
            return (T) transactionBeanEntry.getContextualInstance();
        }

        // if it doesn't yet exist, we need to create it now!
        T instance = component.create(creationalContext);
        transactionBeanEntry = new TransactionBeanEntry(component, instance, creationalContext);
        transactionBeanEntryMap.put(component, transactionBeanEntry);

        checkTransactionBeanEntry(transactionBeanEntry);
        return instance;
    }

    private void checkTransactionBeanEntry(TransactionBeanEntry<?> transactionBeanEntry)
    {
        String activeTransactionKey = TransactionBeanStorage.getStorage().getActiveTransactionKey();

        for(Annotation qualifier : transactionBeanEntry.getQualifiers())
        {
            if(qualifier.annotationType().getName().endsWith(activeTransactionKey))
            {
                return;
            }
        }

        throw new IllegalStateException("Transaction qualifier of the intercepted bean or method and " +
                "the injected entity-manager has to be the same. Active transaction qualifier: " +
                activeTransactionKey + " qualifier/s of the entity-manager: " +
                extractQualifiers(transactionBeanEntry));
    }

    private String extractQualifiers(TransactionBeanEntry<?> transactionBeanEntry)
    {
        StringBuilder result = new StringBuilder();
        for(Annotation annotation : transactionBeanEntry.getQualifiers())
        {
            if(result.length() != 0)
            {
                result.append(";");
            }

            result.append(annotation.annotationType().getName());
        }
        return result.toString();
    }

    private Map<Contextual, TransactionBeanEntry> getTransactionBeanEntryMap()
    {
        TransactionBeanStorage transactionBeanStorage = TransactionBeanStorage.getStorage();

        if(transactionBeanStorage != null)
        {
            transactionBeanStorage.activateTransactionScope(null);
            return transactionBeanStorage.getActiveTransactionContext();
        }
        return null;
    }

    public Class<? extends Annotation> getScope()
    {
        return TransactionScoped.class;
    }

    public boolean isActive()
    {
        return TransactionBeanStorage.getStorage() != null;
    }
}
