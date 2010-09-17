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
package org.apache.myfaces.extensions.cdi.core.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.createNewInstanceOfBean;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.ScopeBeanEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.BeanAccessEvent;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
class ConversationBeanEntry<T> implements BeanEntry<T>
{
    private static final long serialVersionUID = -4756851133555458294L;

    private final Bean<T> bean;

    private T currentBeanInstance;

    private CreationalContext<T> creationalContext;

    private final boolean scopeBeanEventEnable;

    private final boolean beanAccessEventEnable;

    private final boolean unscopeBeanEventEnable;

    ConversationBeanEntry(CreationalContext<T> creationalContext,
                          Bean<T> bean,
                          boolean scopeBeanEventEnable,
                          boolean beanAccessEventEnable,
                          boolean unscopeBeanEventEnable)
    {
        this.bean = bean;
        this.creationalContext = creationalContext;

        this.scopeBeanEventEnable = scopeBeanEventEnable;
        this.beanAccessEventEnable = beanAccessEventEnable;
        this.unscopeBeanEventEnable = unscopeBeanEventEnable;
    }

    public Bean<T> getBean()
    {
        return this.bean;
    }

    public CreationalContext<T> getCreationalContext()
    {
        return creationalContext;
    }

    public T getBeanInstance()
    {
        if (this.currentBeanInstance == null)
        {
            //in case of a reset
            createNewBeanInstance();
        }

        if(this.beanAccessEventEnable)
        {
            //we don't have to check the implementation of Serializable - cdi already checked it
            getBeanManager().fireEvent(new BeanAccessEvent((Serializable)this.currentBeanInstance));
        }

        return this.currentBeanInstance;
    }

    public T resetBeanInstance()
    {
        T oldBeanInstance = this.currentBeanInstance;

        this.currentBeanInstance = null;

        return oldBeanInstance;
    }

    public boolean isScopeBeanEventEnabled()
    {
        return this.scopeBeanEventEnable;
    }

    public boolean isBeanAccessEventEnabled()
    {
        return this.beanAccessEventEnable;
    }

    public boolean isUnscopeBeanEventEnabled()
    {
        return this.unscopeBeanEventEnable;
    }

    private synchronized void createNewBeanInstance()
    {
        if(this.currentBeanInstance != null)
        {
            return;
        }
        
        this.currentBeanInstance = createNewInstanceOfBean(this.bean, this.creationalContext);

        if(this.scopeBeanEventEnable)
        {
            //we don't have to check the implementation of Serializable - cdi already checked it
            getBeanManager().fireEvent(new ScopeBeanEvent((Serializable)this.currentBeanInstance));
        }
    }

    private BeanManager getBeanManager()
    {
        return BeanManagerProvider.getInstance().getBeanManager();
    }
}
