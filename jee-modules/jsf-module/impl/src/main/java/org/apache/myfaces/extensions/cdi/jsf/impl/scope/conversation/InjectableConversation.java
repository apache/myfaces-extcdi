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
 * @author Gerhard Petracek
 */
@Typed()
public class InjectableConversation implements EditableConversation
{
    private static final long serialVersionUID = 7754789230388003028L;

    protected final Bean<?> bean;
    protected final EditableWindowContext editableWindowContext;

    protected InjectableConversation(Bean<?> bean, WindowContextManager windowContextManager)
    {
        this.bean = bean;
        this.editableWindowContext = (EditableWindowContext) windowContextManager.getCurrentWindowContext();
    }

    public void close()
    {
        findConversation().close();
    }

    public void restart()
    {
        findConversation().restart();
    }

    public boolean isActive()
    {
        return findConversation().isActive();
    }

    public boolean getActiveState()
    {
        return findConversation().getActiveState();
    }

    public void deactivate()
    {
        findConversation().deactivate();
    }

    public <T> void addBean(BeanEntry<T> beanInstance)
    {
        findConversation().addBean(beanInstance);
    }

    public <T> T getBean(Class<T> key)
    {
        return findConversation().getBean(key);
    }

    protected EditableConversation findConversation()
    {
        Class conversationGroup = ConversationUtils.getConversationGroup(this.bean);

        Set<Annotation> qualifiers = this.bean.getQualifiers();

        return this.editableWindowContext.getConversation(conversationGroup,
                                                          qualifiers.toArray(new Annotation[qualifiers.size()]));
    }
}
