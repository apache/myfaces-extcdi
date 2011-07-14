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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.UnscopeBeanEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Named;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
class BeanStorage implements Serializable
{
    private static final long serialVersionUID = 7020160538290030954L;

    //all implementations will be serializable
    private BeanManager beanManager;

    private ConcurrentHashMap<Class, BeanEntry<Serializable>> beanMap =
            new ConcurrentHashMap<Class, BeanEntry<Serializable>>();

    BeanStorage(BeanManager beanManager)
    {
        this.beanManager = beanManager;
    }

    BeanEntry getBean(Class beanClass)
    {
        return this.beanMap.get(beanClass);
    }

    <T> Set<Class<T>> getBeanSubGroup(Class<T> key)
    {
        Set<Class<T>> result = new HashSet<Class<T>>();

        for(Class beanClass : this.beanMap.keySet())
        {
            if(key.isAssignableFrom(beanClass))
            {
                result.add(beanClass);
            }
        }
        return result;
    }

    BeanEntry addBean(BeanEntry<Serializable> beanEntry)
    {
        Class beanClass = beanEntry.getBean().getBeanClass();
        this.beanMap.remove(beanClass);
        this.beanMap.put(beanClass, beanEntry);
        return beanEntry;
    }

    BeanEntry<Serializable> removeBean(Class<Serializable> beanClass)
    {
        return this.beanMap.remove(beanClass);
    }

    //TODO don't reset window scoped beans
    void resetStorage()
    {
        Serializable oldBeanInstance;
        for (BeanEntry<Serializable> beanHolder : this.beanMap.values())
        {
            oldBeanInstance = beanHolder.resetBeanInstance();

            if(beanHolder.isUnscopeBeanEventEnabled())
            {
                fireUnscopeBeanEvent(oldBeanInstance);
            }

            beanHolder.getBean().destroy(oldBeanInstance, beanHolder.getCreationalContext());
        }
    }

    private <T extends Serializable> void fireUnscopeBeanEvent(T instance)
    {
        this.beanManager.fireEvent(new UnscopeBeanEvent(instance));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        result.append(this.beanMap.size());
        result.append(" beans:\n");
        Bean<Serializable> bean;
        Class<? extends Annotation> qualifierType;
        for(BeanEntry<Serializable> beanEntry  : this.beanMap.values())
        {
            bean = beanEntry.getBean();

            result.append("\t[bean]\n");
            result.append("\tbean-class:\t\t\t\t");
            result.append(bean.getBeanClass());
            
            result.append("\n\tcustom qualifier types: ");

            boolean customQualifierFound = false;
            for(Annotation qualifier : bean.getQualifiers())
            {
                qualifierType = qualifier.annotationType();

                if(!
                        (ConversationGroup.class.equals(qualifierType) ||
                         Any.class.equals(qualifierType) ||
                         Default.class.equals(qualifierType) ||
                         Named.class.equals(qualifierType)))
                {
                    result.append("\t");
                    result.append(qualifier.annotationType().getName());
                    result.append("\n");
                    customQualifierFound = true;
                }
            }

            if(!customQualifierFound)
            {
                result.append("---\n");
            }
        }

        result.append("\n*******");

        return result.toString();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
