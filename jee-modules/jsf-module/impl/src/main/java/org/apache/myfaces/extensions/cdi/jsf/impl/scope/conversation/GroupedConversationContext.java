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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.AbstractGroupedConversationContext;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntryFactory;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * jsf specific parts for managing grouped conversations
 *
 * @author Gerhard Petracek
 */
class GroupedConversationContext extends AbstractGroupedConversationContext
{
    GroupedConversationContext(BeanManager beanManager)
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

    protected WindowContextManager resolveWindowContextManager()
    {
        return RequestCache.getWindowContextManager();
    }

    protected BeanEntryFactory resolveBeanEntryFactory()
    {
        return RequestCache.getBeanEntryFactory();
    }

    /**
     * @param windowContextManager the current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanDescriptor      descriptor of the requested bean
     * @return the instance of the requested bean if it exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    protected <T> T resolveBeanInstance(WindowContextManager windowContextManager, Bean<T> beanDescriptor)
    {
        if(!(windowContextManager instanceof EditableWindowContextManager))
        {
            throw ExceptionUtils.windowContextManagerNotEditableException(windowContextManager);
        }

        Class<?> beanClass = beanDescriptor.getBeanClass();
        EditableConversation foundConversation = getConversation(
                (EditableWindowContextManager)windowContextManager, beanDescriptor);

        //noinspection unchecked
        return (T)foundConversation.getBean(beanClass);
    }

    protected <T> void scopeBeanEntry(WindowContextManager windowContextManager, BeanEntry<T> beanEntry)
    {
        if(!(windowContextManager instanceof EditableWindowContextManager))
        {
            throw ExceptionUtils.windowContextManagerNotEditableException(windowContextManager);
        }

        Bean<?> bean = beanEntry.getBean();
        EditableConversation foundConversation =
                getConversation((EditableWindowContextManager)windowContextManager, bean);

        foundConversation.addBean(beanEntry);
    }

    protected ConversationConfig getConversationConfig()
    {
        return CodiUtils.getContextualReferenceByClass(ConversationConfig.class);
    }

    private EditableConversation getConversation(EditableWindowContextManager windowContextManager, Bean<?> bean)
    {
        Class conversationGroup = ConversationUtils.getConversationGroup(bean);

        Set<Annotation> qualifiers = bean.getQualifiers();

        EditableWindowContext editableWindowContext = (EditableWindowContext)windowContextManager
                .getCurrentWindowContext();

        return editableWindowContext
                .getConversation(conversationGroup, qualifiers.toArray(new Annotation[qualifiers.size()]));
    }
}
