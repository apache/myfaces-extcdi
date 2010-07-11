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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.EditableConversation;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class DefaultConversation implements Conversation, EditableConversation
{
    private static final long serialVersionUID = -2958548175169003298L;

    //for easier debugging
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private final Class groupKey;

    private final boolean windowScoped;

    private String lastViewId; //for access scope

    private final BeanStorage beanStorage;

    private final long conversationTimeoutInMs;

    private boolean active;

    private Date lastAccess;

    public DefaultConversation(Class<?> groupKey, int conversationTimeoutInMinutes)
    {
        this.groupKey = groupKey;
        this.windowScoped = WindowGroup.class.isAssignableFrom(groupKey);

        tryToProcessAccessViewScope(groupKey);

        this.beanStorage = new BeanStorage();
        this.conversationTimeoutInMs = conversationTimeoutInMinutes * 60000;
    }

    public boolean isActive()
    {
        return !isConversationTimedout() && this.active;
    }

    public void deactivate()
    {
        if (!this.windowScoped)
        {
            this.active = false;
        }
    }

    public void end()
    {
        this.active = false;
        this.beanStorage.resetStorage();
    }

    public void restart()
    {
        touchConversation();
        this.beanStorage.resetStorage();
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getBean(Class<T> key)
    {
        if (!isActive())
        {
            return null;
        }

        touchConversation();

        BeanEntry scopedBean = this.beanStorage.getBean(key);

        if (scopedBean == null)
        {
            return null;
        }

        return (T) scopedBean.getBeanInstance();
    }

    public <T> void addBean(Class<?> beanClass, BeanEntry<T> beanEntry)
    {
        tryToProcessAccessViewScope(beanClass);

        //TODO check if conversation is active
        touchConversation();

        //TODO
        //noinspection unchecked
        this.beanStorage.addBean(beanClass, (BeanEntry<Serializable>) beanEntry);
    }

    /*
    private BeanEntryHolder createBean(BeanEntry<Serializable> beanEntry)
    {
        return new BeanEntryHolder(beanEntry, FacesContext.getCurrentInstance().getViewRoot().getViewId());
    }
    */

    private boolean isConversationTimedout()
    {
        if (this.windowScoped)
        {
            return false;
        }

        if (this.lastViewId != null)
        {
            Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
            String fromViewId = (String) requestMap.get(AccessScopeAwareNavigationHandler.OLD_VIEW_ID_KEY);

            if (fromViewId != null && fromViewId.endsWith(this.lastViewId))
            {
                this.lastViewId = (String) requestMap.get(AccessScopeAwareNavigationHandler.NEW_VIEW_ID_KEY);
            }
            return !this.lastViewId.equals(getCurrentViewId());
        }

        return this.lastAccess == null ||
                (this.lastAccess.getTime() + this.conversationTimeoutInMs) < System.currentTimeMillis();
    }

    private void touchConversation()
    {
        this.active = true;
        this.lastAccess = new Date();

        if (this.lastViewId != null)
        {
            this.lastViewId = getCurrentViewId();
        }
    }

    private String getCurrentViewId()
    {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    private void tryToProcessAccessViewScope(Class<?> groupKey)
    {
        ConversationScoped conversationScoped = groupKey.getAnnotation(ConversationScoped.class);
        if (conversationScoped != null && ViewAccessGroup.class.isAssignableFrom(conversationScoped.value()))
        {
            this.lastViewId = getCurrentViewId();
        }
    }
}
