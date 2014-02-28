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

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntryFactory;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@inheritDoc}
 */
@ApplicationScoped
public class DefaultBeanEntryFactory implements BeanEntryFactory
{
    @Inject
    private BeanManager beanManager;

    /**
     * {@inheritDoc}
     */
    public <T> BeanEntry<T> createBeanEntry(Bean<T> bean,
                                            CreationalContext<T> creationalContext,
                                            boolean scopeBeanEventEnabled,
                                            boolean accessBeanEventEnabled,
                                            boolean unscopeBeanEventEnabled)
    {
        if(Serializable.class.isAssignableFrom(bean.getClass()))
        {
            return new ConversationBeanEntry<T>(creationalContext,
                                                bean,
                                                this.beanManager,
                                                scopeBeanEventEnabled,
                                                accessBeanEventEnabled,
                                                unscopeBeanEventEnabled);
        }

        if(PassivationCapable.class.isAssignableFrom(bean.getClass()))
        {
            return new PassivationAwareConversationBeanEntry<T>(creationalContext,
                                                                bean,
                                                                ((PassivationCapable)bean).getId(),
                                                                this.beanManager,
                                                                scopeBeanEventEnabled,
                                                                accessBeanEventEnabled,
                                                                unscopeBeanEventEnabled);
        }

        //should never occur
        Logger logger = Logger.getLogger(DefaultBeanEntryFactory.class.getName());
        if(logger.isLoggable(Level.WARNING))
        {
            logger.warning("the bean-implementation: " + bean.getClass() + " doesn't implement " +
                Serializable.class.getName() + " or " + PassivationCapable.class.getName() +
                    " -> mechanisms like session serialization won't work.");
        }

        return new ConversationBeanEntry<T>(creationalContext,
                                            bean,
                                            this.beanManager,
                                            scopeBeanEventEnabled,
                                            accessBeanEventEnabled,
                                            unscopeBeanEventEnabled);
    }
}
