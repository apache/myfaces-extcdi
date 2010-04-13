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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.grouped;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.grouped.spi.ConversationManager;
import org.apache.myfaces.extensions.cdi.core.api.tools.annotate.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.grouped.AbstractGroupedConversationContextAdapter;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;
import java.util.Set;

/**
 * jsf specific parts for managing grouped conversations
 *
 * @author Gerhard Petracek
 */
public class GroupedConversationContextAdapter extends AbstractGroupedConversationContextAdapter
{
    public GroupedConversationContextAdapter(BeanManager beanManager)
    {
        super(beanManager);
    }

    /**
     * @return true as soon as JSF is active
     * the {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationContext}
     * will be created automatically
     */
    public boolean isActive()
    {
        return FacesContext.getCurrentInstance().getExternalContext().getSession(false) != null;
    }

    /**
     * @return the descriptor of a custom {@link ConversationManager} with the qualifier {@link Jsf} or
     * the descriptor of the default implementation provided by this module
     */
    protected Bean<ConversationManager> resolveConversationManagerBean()
    {
        Set<?> conversationManagerBeans = this.beanManager.getBeans(
                ConversationManager.class, DefaultAnnotation.of(Jsf.class));

        if(conversationManagerBeans.isEmpty())
        {
            conversationManagerBeans = getDefaultConversationManager();
        }

        if(conversationManagerBeans.size() != 1)
        {
            throw new IllegalStateException(conversationManagerBeans.size() + " conversation-managers were found");
        }
        //noinspection unchecked
        return (Bean<ConversationManager>)conversationManagerBeans.iterator().next();
    }
}