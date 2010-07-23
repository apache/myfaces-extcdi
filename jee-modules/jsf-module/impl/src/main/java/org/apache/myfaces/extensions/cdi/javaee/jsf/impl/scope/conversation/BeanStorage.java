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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.manager.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.UnscopeBeanEvent;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.destroyBean;

import javax.enterprise.inject.spi.BeanManager;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
class BeanStorage implements Serializable
{
    private static final long serialVersionUID = 7020160538290030954L;

    private transient BeanManager beanManager;

    private Map<Class, BeanEntry<Serializable>> beanMap = new ConcurrentHashMap<Class, BeanEntry<Serializable>>();

    public BeanEntry getBean(Class beanClass)
    {
        synchronized (this)
        {
            BeanEntry<Serializable> beanEntry = this.beanMap.get(beanClass);

            if (beanEntry == null)
            {
                return null;
            }

            //don't use something like Bean#touch here to ensure that the correct ViewId is used as well
            return addBean(this.beanMap, beanEntry);
        }
    }

    public BeanEntry addBean(BeanEntry<Serializable> beanEntry)
    {
        synchronized (this)
        {
            return addBean(this.beanMap, beanEntry);
        }
    }

    private BeanEntry addBean(Map<Class, BeanEntry<Serializable>> beanMap, BeanEntry<Serializable> beanEntry)
    {
        //BeanEntryHolder newBean = new BeanEntryHolder(beanHolder);
        Class beanClass = beanEntry.getBean().getBeanClass();
        beanMap.remove(beanClass);
        beanMap.put(beanClass, beanEntry);
        //this.beanAccessedEventEvent.fire(new BeanAccessedEvent(bean.getBeanInstance()));
        return beanEntry;
    }

    //TODO don't reset window scoped beans
    public void resetStorage()
    {
        Serializable oldBeanInstance;
        for (BeanEntry<Serializable> beanHolder : this.beanMap.values())
        {
            oldBeanInstance = beanHolder.resetBeanInstance();
            fireUnscopeBeanEvent(oldBeanInstance, beanHolder);
            beanHolder.getBean().destroy(oldBeanInstance, beanHolder.getCreationalContext());
        }
    }

    private <T extends Serializable> void fireUnscopeBeanEvent(T instance, BeanEntry<T> beanHolder)
    {
        getOrCreateBeanManager().fireEvent(new UnscopeBeanEvent(instance));

        destroyBean(beanHolder.getCreationalContext(), beanHolder.getBean(), instance);
    }

    private BeanManager getOrCreateBeanManager()
    {
        if (this.beanManager == null)
        {
            this.beanManager = BeanManagerProvider.getInstance().getBeanManager();
        }

        return this.beanManager;
    }
}
