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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Fallback e.g. for Weld
 *
 * @author Gerhard Petracek
 */
class PassivationAwareConversationBeanEntry<T> extends AbstractConversationBeanEntry<T>
{
    private static final long serialVersionUID = 5461280883272018770L;

    private String beanId;

    private transient Bean<T> bean;

    PassivationAwareConversationBeanEntry(CreationalContext<T> creationalContext,
                                          Bean<T> bean,
                                          String beanId,
                                          BeanManager beanManager,
                                          boolean scopeBeanEventEnable,
                                          boolean accessBeanEventEnable,
                                          boolean unscopeBeanEventEnable)
    {
        super(creationalContext, beanManager, scopeBeanEventEnable, accessBeanEventEnable, unscopeBeanEventEnable);
        this.bean = bean;
        this.beanId = beanId;
    }

    /**
     * {@inheritDoc}
     */
    public Bean<T> getBean()
    {
        if(this.bean == null)
        {
            this.bean = (Bean<T>) this.beanManager.getPassivationCapableBean(this.beanId);
        }

        return bean;
    }
}
