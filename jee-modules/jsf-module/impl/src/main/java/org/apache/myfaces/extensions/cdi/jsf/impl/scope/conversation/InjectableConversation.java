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

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * {@inheritDoc}
 */
@Typed()
public class InjectableConversation implements EditableConversation
{
    private static final long serialVersionUID = 7754789230388003028L;

    private final Class conversationGroup;

    private final Set<Annotation> qualifiers;

    private transient EditableWindowContext editableWindowContext;

    protected InjectableConversation(Bean<?> bean, WindowContextManager windowContextManager)
    {
        this.conversationGroup = ConversationUtils.getConversationGroup(bean);
        this.qualifiers = bean.getQualifiers();
        this.editableWindowContext = (EditableWindowContext) windowContextManager.getCurrentWindowContext();
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        findConversation().close();
    }

    /**
     * {@inheritDoc}
     */
    public void restart()
    {
        findConversation().restart();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive()
    {
        return findConversation().isActive();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getActiveState()
    {
        return findConversation().getActiveState();
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate()
    {
        findConversation().deactivate();
    }

    /**
     * {@inheritDoc}
     */
    public <T> void addBean(BeanEntry<T> beanInstance)
    {
        findConversation().addBean(beanInstance);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getBean(Class<T> key)
    {
        return findConversation().getBean(key);
    }

    /**
     * {@inheritDoc}
     */
    public <T> Set<Class<T>> getBeanSubGroup(Class<T> key)
    {
        return findConversation().getBeanSubGroup(key);
    }

    /**
     * {@inheritDoc}
     */
    public <T> BeanEntry<T> removeBeanEntry(Class<T> type)
    {
        return findConversation().removeBeanEntry(type);
    }

    protected EditableConversation findConversation()
    {
        if(this.editableWindowContext == null)
        {
            this.editableWindowContext = (EditableWindowContext) ConversationUtils
                    .getWindowContextManager().getCurrentWindowContext();
        }
        return this.editableWindowContext.getConversation(this.conversationGroup,
                                                          this.qualifiers.toArray(new Annotation[qualifiers.size()]));
    }
}
