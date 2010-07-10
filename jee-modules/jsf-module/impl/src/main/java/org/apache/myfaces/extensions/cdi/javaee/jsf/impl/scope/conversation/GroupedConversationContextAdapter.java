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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Window;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.AbstractConversationContextAdapter;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;

/**
 * jsf specific parts for managing grouped conversations
 *
 * @author Gerhard Petracek
 */
public class GroupedConversationContextAdapter extends AbstractConversationContextAdapter
{
    public GroupedConversationContextAdapter(BeanManager beanManager)
    {
        super(beanManager);
    }

    /**
     * @return true as soon as JSF is active
     *         the {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         will be created automatically
     */
    public boolean isActive()
    {
        return FacesContext.getCurrentInstance().getExternalContext().getSession(false) != null;
    }

    /**
     * @return the descriptor of a custom
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * with the qualifier {@link org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf} or
     *         the descriptor of the default implementation provided by this module
     */
    protected Bean<WindowContextManager> resolveConversationManagerBean()
    {
        return ConversationUtils.resolveConversationManagerBean();
    }

    /**
     * @param conversationManager the current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanDescriptor      descriptor of the requested bean
     * @return the instance of the requested bean if it exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    protected <T> T resolveBeanInstance(WindowContextManager conversationManager, Bean<T> beanDescriptor)
    {
        Class<?> beanClass = beanDescriptor.getBeanClass();
        Conversation foundConversation = getConversation(conversationManager, beanClass);

        //noinspection unchecked
        return (T) foundConversation.getBean(beanClass);
    }

    protected <T> void scopeBeanEntry(WindowContextManager conversationManager, BeanEntry<T> beanEntry)
    {
        Class<?> beanClass = beanEntry.getBean().getBeanClass();
        Conversation foundConversation = getConversation(conversationManager, beanClass);

        ((EditableConversation) foundConversation).addBean(beanClass, beanEntry);
    }

    private Conversation getConversation(WindowContextManager conversationManager, Class<?> beanClass)
    {
        Class conversationGroup = getConversationGroup(beanClass);

        return conversationManager.getCurrentWindowContext().getConversation(conversationGroup);
    }

    private Class getConversationGroup(Class<?> beanClass)
    {
        ConversationGroup conversationGroupAnnotation = beanClass.getAnnotation(ConversationGroup.class);

        if (conversationGroupAnnotation != null)
        {
            return conversationGroupAnnotation.value();
        }

        if (beanClass.isAnnotationPresent(Window.class))
        {
            return Window.class;
        }
        return beanClass;
    }
}