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

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import java.lang.annotation.Annotation;

/**
 * CDI Context for managing &#064;{@link TransactionScoped} contextual instances.
 */
public class TransactionContext implements Context
{
    // Attention! this is not a normal instance but a PROXY
    // thus it resolves the correct contextual instance every time
    private TransactionBeanStorage beanStorage;

    public TransactionContext(TransactionBeanStorage beanStorage)
    {
        this.beanStorage = beanStorage;
    }


    public <T> T get(Contextual<T> component)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Class<? extends Annotation> getScope()
    {
        return TransactionScoped.class;
    }

    public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isActive()
    {
        try
        {
            //X TODO beanStorage....
            return true;
        }
        catch(ContextNotActiveException cnae)
        {
            return false;
        }
    }



}
