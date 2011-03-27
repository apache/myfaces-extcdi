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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntryFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractGroupedConversationContext
{
    protected BeanManager beanManager;

    private ConversationConfig conversationConfig;

    private boolean scopeBeanEventEnable = false;

    private boolean accessBeanEventEnable = false;

    private boolean unscopeBeanEventEnable = false;

    private static RuntimeException runtimeException = new RuntimeException();

    //workaround for weld
    private final boolean useFallback;

    protected AbstractGroupedConversationContext(BeanManager beanManager)
    {
        this.beanManager = beanManager;

        boolean useCreationFallback = true;
        for(StackTraceElement element : runtimeException.getStackTrace())
        {
            if(element.toString().contains("org.apache.webbeans."))
            {
                useCreationFallback = false;
                break;
            }
        }

        this.useFallback = useCreationFallback;
    }

    /**
     * @param bean         descriptor of the bean
     * @param creationalContext context for creating a bean
     * @return a scoped bean-instance
     */
    public <T> T create(Bean<T> bean, CreationalContext<T> creationalContext)
    {
        //workaround for weld - start
        if(useFallback)
        {
            T scopedBean = resolve(bean);

            if(scopedBean != null)
            {
                return scopedBean;
            }
        }
        //workaround for weld - end

        lazyInitConversationConfig();

        WindowContextManager windowContextManager = resolveWindowContextManager();

        BeanEntry<T> beanEntry = resolveBeanEntryFactory().createBeanEntry(bean,
                                                                           creationalContext,
                                                                           this.scopeBeanEventEnable,
                                                                           this.accessBeanEventEnable,
                                                                           this.unscopeBeanEventEnable);

        scopeBeanEntry(windowContextManager, beanEntry);

        return beanEntry.getBeanInstance();
    }

    /**
     * Resolves the instance for the given bean definition
     * @param bean current bean
     * @param <T> type of the current bean
     * @return instance of the scoped bean
     */
    @SuppressWarnings({"UnnecessaryLocalVariable"})
    public <T> T resolve(Bean<T> bean)
    {
        WindowContextManager windowContextManager = resolveWindowContextManager();

        T foundBeanInstance = resolveBeanInstance(windowContextManager, bean);

        return foundBeanInstance;
    }

    /**
     * @return an instance of a custom or the default
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     */
    protected abstract WindowContextManager resolveWindowContextManager();

    /**
     * @return an instance of a custom or the default
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntryFactory}
     */
    protected abstract BeanEntryFactory resolveBeanEntryFactory();

    /**
     * @param windowContextManager the current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanDescriptor      descriptor of the requested bean
     * @return the instance of the requested bean if it exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    protected abstract <T> T resolveBeanInstance(WindowContextManager windowContextManager, Bean<T> beanDescriptor);

    /**
     * Store the given bean in the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *
     * @param windowContextManager current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanEntry           current bean-entry
     */
    protected abstract <T> void scopeBeanEntry(WindowContextManager windowContextManager, BeanEntry<T> beanEntry);

    protected abstract ConversationConfig getConversationConfig();

    /**
     * See {@link javax.enterprise.context.spi.Context#isActive()}
     * @return true if the context is active, false otherwise
     */
    public abstract boolean isActive();

    private void lazyInitConversationConfig()
    {
        if(this.conversationConfig == null)
        {
            this.conversationConfig = getConversationConfig();

            this.scopeBeanEventEnable = this.conversationConfig.isScopeBeanEventEnabled();
            this.accessBeanEventEnable = this.conversationConfig.isAccessBeanEventEnabled();
            this.unscopeBeanEventEnable = this.conversationConfig.isUnscopeBeanEventEnabled();
        }
    }
}
