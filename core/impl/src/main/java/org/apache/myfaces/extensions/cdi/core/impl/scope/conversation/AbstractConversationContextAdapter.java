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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractConversationContextAdapter implements Context
{
    protected BeanManager beanManager;

    private static RuntimeException runtimeException = new RuntimeException();

    //workaround for weld
    private final boolean useFallback;

    public AbstractConversationContextAdapter(BeanManager beanManager)
    {
        this.beanManager = beanManager;

        boolean useFallback = true;
        for(StackTraceElement element : runtimeException.getStackTrace())
        {
            if(element.toString().contains("org.apache.webbeans."))
            {
                useFallback = false;
                break;
            }
        }

        this.useFallback = useFallback;
    }

    /**
     * @return annotation of the codi conversation scope
     */
    public Class<? extends Annotation> getScope()
    {
        return ConversationScoped.class;
    }

    /**
     * @param component         descriptor of the bean
     * @param creationalContext context for creating a bean
     * @return a scoped bean-instance
     */
    public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext)
    {
        if (component instanceof Bean)
        {
            //workaround for weld - start
            if(useFallback)
            {
                T scopedBean = get(component);

                if(scopedBean != null)
                {
                    return scopedBean;
                }
            }
            //workaround for weld - end

            WindowContextManager conversationManager = resolveWindowContextManager();

            Bean<T> bean = ((Bean<T>) component);

            BeanEntry<T> beanEntry = new ConversationBeanEntry<T>(creationalContext, bean);

            scopeBeanEntry(conversationManager, beanEntry);

            return beanEntry.getBeanInstance();
        }

        Class invalidComponentClass = component.create(creationalContext).getClass();
        throw new IllegalStateException(invalidComponentClass + " is no valid conversation scoped bean");
    }

    /**
     * @param component descriptor of the bean
     * @return an instance of the requested bean if it already exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    public <T> T get(Contextual<T> component)
    {
        if (component instanceof Bean)
        {
            Bean<T> bean = ((Bean<T>) component);
            WindowContextManager windowContextManager = resolveWindowContextManager();

            T foundBeanInstance = resolveBeanInstance(windowContextManager, bean);

            return foundBeanInstance;
        }
        throw new IllegalStateException(component.getClass() + " is no valid conversation scoped bean");
    }

    /**
     * @return an instance of a custom (the default)
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     */
    private WindowContextManager resolveWindowContextManager()
    {
        Bean<WindowContextManager> windowContextManagerBean = resolveWindowContextManagerBean();
        return CodiUtils.getOrCreateScopedInstanceOfBean(windowContextManagerBean);

        //TODO cleanup:
        //return (WindowContextManager)this.beanManager.getReference(
        //windowContextManagerBean, ConversationManager.class,
        //getConversationManagerCreationalContextFor(windowContextManagerBean));
    }

    protected abstract Bean<WindowContextManager> resolveWindowContextManagerBean();

    /**
     * @param conversationManager the current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanDescriptor      descriptor of the requested bean
     * @return the instance of the requested bean if it exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    protected abstract <T> T resolveBeanInstance(WindowContextManager conversationManager, Bean<T> beanDescriptor);

    /**
     * Store the given bean in the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *
     * @param conversationManager current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanEntry           current bean-entry
     */
    protected abstract <T> void scopeBeanEntry(WindowContextManager conversationManager, BeanEntry<T> beanEntry);
}
