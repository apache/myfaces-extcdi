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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.CloseConversationEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.RestartConversationEvent;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
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

    //all implementations will be serializable
    private BeanManager beanManager;

    private final BeanStorage beanStorage;

    private final boolean closeConversationEventEnable;
    private final boolean restartConversationEventEnable;

    public DefaultConversation(ConversationKey conversationKey,
                               ConversationExpirationEvaluator expirationEvaluator,
                               ConversationConfig conversationConfig,
                               BeanManager beanManager)
    {
        this.conversationKey = conversationKey;
        this.expirationEvaluator = expirationEvaluator;
        this.beanManager = beanManager;
        this.beanStorage = new BeanStorage(this.beanManager);

        this.closeConversationEventEnable = conversationConfig.isCloseConversationEventEnabled();
        this.restartConversationEventEnable = conversationConfig.isRestartConversationEventEnabled();

        if(this.expirationEvaluator instanceof ConversationAware)
        {
            ((ConversationAware)this.expirationEvaluator).setConversation(this);
        }
    }

    //just for a better performance to avoid frequent calls to the {@link #expirationEvaluator}
    private boolean active;

    /**
     * {@inheritDoc}
     */
    public boolean isActive()
    {
        return !isConversationExpired() && this.active;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getActiveState()
    {
        return active;
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate()
    {
        this.expirationEvaluator.expire();
        if (this.expirationEvaluator.isExpired())
        {
            endConversation();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        fireCloseConversationEvent();

        if(this.active)
        {
            endConversation();
        }
    }

    private void endConversation()
    {
        this.active = false;
        this.beanStorage.resetStorage();
        RequestCache.resetConversationCache();
    }

    /**
     * {@inheritDoc}
     */
    public void restart()
    {
        fireRestartConversationEvent();
        touchConversation();
        this.beanStorage.resetStorage();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * just for test-cases (to expire a conversation manually)
     * @return current conversation-expiration-evaluator
     */
    public ConversationExpirationEvaluator getExpirationEvaluator()
    {
        return expirationEvaluator;
    }

    private void fireCloseConversationEvent()
    {
        if(this.closeConversationEventEnable)
        {
            this.beanManager.fireEvent(new CloseConversationEvent(this));
        }
    }

    private void fireRestartConversationEvent()
    {
        if(this.restartConversationEventEnable)
        {
            this.beanManager.fireEvent(new RestartConversationEvent(this));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        result.append("\n*** conversation - start ***\n");
        result.append(this.conversationKey);

        result.append("\n");
        result.append("active state: ");
        result.append(getActiveState());
        result.append("\n");

        result.append(this.beanStorage);

        result.append("\n");
        result.append("expiration-evaluator: ");
        result.append(this.expirationEvaluator.getClass().getName());
        result.append("\n*** conversation - end ***");

        return result.toString();
    }
}
