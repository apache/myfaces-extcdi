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
package org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.grouped;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.grouped.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.grouped.spi.ConversationManager;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.spi.Context;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractGroupedConversationContextAdapter implements Context
{
    protected BeanManager beanManager;

    public AbstractGroupedConversationContextAdapter(BeanManager beanManager)
    {
        this.beanManager = beanManager;
    }

    /**
     * @return annotation of the codi conversation scope
     */
    public Class<? extends Annotation> getScope()
    {
        return ConversationScoped.class;
    }

    /**
     * @param component descriptor of the bean
     * @param creationalContext context for creating a bean
     * @return a scoped bean-instance
     */
    public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext)
    {
        if (component instanceof Bean)
        {
            ConversationManager conversationManager = resolveConversationManager();

            T beanInstance = component.create(creationalContext);
            scopeBeanInstance((Bean<T>)component, beanInstance, conversationManager);
            return beanInstance;
        }

        Class invalidComponentClass = component.create(creationalContext).getClass();
        throw new IllegalStateException(invalidComponentClass + " is no valid conversation scoped bean");
    }

    /**
     * @param component descriptor of the bean
     * @return an instance of the requested bean if it already exists in the current
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationContext}
     * null otherwise
     */
    public <T> T get(Contextual<T> component)
    {
        if (component instanceof Bean)
        {
            Bean<T> foundBean = ((Bean<T>) component);
            ConversationManager conversationManager = resolveConversationManager();

            return resolveBeanInstance(foundBean, conversationManager);
        }
        return null;
    }

    /**
     * @return an instance of a custom (the default) {@link ConversationManager}
     */
    private ConversationManager resolveConversationManager()
    {
        Bean<ConversationManager> conversationManagerBean = resolveConversationManagerBean();
        return conversationManagerBean.create(getConversationManagerCreationalContextFor(conversationManagerBean));
    }

    private CreationalContext<ConversationManager> getConversationManagerCreationalContextFor(
            Bean<ConversationManager> conversationManagerBean)
    {
        return this.beanManager.createCreationalContext(conversationManagerBean);
    }

    protected abstract Bean<ConversationManager> resolveConversationManagerBean();

    /**
     * @return the descriptor of the default {@link ConversationManager}
     */
    protected Set<Bean<?>> getDefaultConversationManager()
    {
        return this.beanManager.getBeans(ConversationManager.class);
    }

    /**
     * @param beanDescriptor descriptor of the requested bean
     * @param conversationManager the current {@link ConversationManager}
     * @return the instance of the requested bean if it exists in the current
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationContext}
     * null otherwise
     */
    private <T> T resolveBeanInstance(Bean<T> beanDescriptor, ConversationManager conversationManager)
    {
        //TODO
        return null;
    }

    /**
     * Store the given bean in the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationContext}
     *
     * @param beanDescriptor descriptor of the current bean
     * @param beanInstance bean to save in the current conversation
     * @param conversationManager current {@link ConversationManager}
     */
    private <T> void scopeBeanInstance(Bean<T> beanDescriptor, T beanInstance, ConversationManager conversationManager)
    {
        //TODO
    }
}
