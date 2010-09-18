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
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.inject.Typed;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class DefaultConversation implements EditableConversation
{
    private static final long serialVersionUID = -2958548175169003298L;

    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private final ConversationKey conversationKey;
    private ConversationExpirationEvaluator expirationEvaluator;

    private final BeanStorage beanStorage = new BeanStorage();

    public DefaultConversation(ConversationKey conversationKey, ConversationExpirationEvaluator expirationEvaluator)
    {
        this.conversationKey = conversationKey;
        this.expirationEvaluator = expirationEvaluator;
    }

    //just for a better performance to avoid frequent calls to the {@link #expirationEvaluator}
    private boolean active;

    public boolean isActive()
    {
        return !isConversationExpired() && this.active;
    }

    public boolean getActiveState()
    {
        return active;
    }

    public void deactivate()
    {
        this.expirationEvaluator.expire();
        if (this.expirationEvaluator.isExpired())
        {
            this.active = false;
        }
    }

    public void end()
    {
        if(this.active)
        {
            this.active = false;
            this.beanStorage.resetStorage();
            RequestCache.resetConversationCache();
        }
    }

    public void restart()
    {
        touchConversation();
        this.beanStorage.resetStorage();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getBean(Class<T> key)
    {
        if (!this.active)
        {
            return null;
        }

        BeanEntry scopedBean = this.beanStorage.getBean(key);

        if (scopedBean == null)
        {
            return null;
        }

        touchConversation();

        return (T) scopedBean.getBeanInstance();
    }

    public <T> void addBean(BeanEntry<T> beanEntry)
    {
        //TODO check if conversation is active
        touchConversation();

        //TODO
        //noinspection unchecked
        this.beanStorage.addBean((BeanEntry<Serializable>) beanEntry);
    }

    private boolean isConversationExpired()
    {
        return this.expirationEvaluator.isExpired();
    }

    private void touchConversation()
    {
        this.active = true;

        this.expirationEvaluator.touch();
    }

    //just for test-cases (to expire a conversation manually)
    public ConversationExpirationEvaluator getExpirationEvaluator()
    {
        return expirationEvaluator;
    }
}
