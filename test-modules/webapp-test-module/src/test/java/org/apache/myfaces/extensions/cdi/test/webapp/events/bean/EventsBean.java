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
package org.apache.myfaces.extensions.cdi.test.webapp.events.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.AccessBeanEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.CloseConversationEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.CloseWindowContextEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.CreateWindowContextEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.RestartConversationEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.ScopeBeanEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.StartConversationEvent;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.UnscopeBeanEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Observers for bean-events
 */
@Named
@ApplicationScoped
public class EventsBean implements Serializable
{

    private boolean conversationStarted;
    private boolean conversationClosed;
    private boolean conversationRestarted;
    private Conversation startedConversation;
    private int conversationBeanScoped;
    private int viewaccessBeanScoped;
    private int windowBeanScoped;
    private int conversationBeanUnscoped;
    private int viewaccessBeanUnscoped;
    private int windowBeanUnscoped;
    private int conversationBeanAccessed;
    private int viewaccessBeanAccessed;
    private int windowBeanAccessed;
    private int windowContextCreated;
    private int windowContextClosed;

    @Inject
    private WindowContext windowContext;

    @PostConstruct
    public void initialize()
    {
        conversationStarted = false;
        conversationClosed = false;
        conversationRestarted = false;
        conversationBeanScoped = 0;
        viewaccessBeanScoped = 0;
        windowBeanScoped = 0;
        conversationBeanUnscoped = 0;
        viewaccessBeanUnscoped = 0;
        windowBeanUnscoped = 0;
        conversationBeanAccessed = 0;
        viewaccessBeanAccessed = 0;
        windowBeanAccessed = 0;
        windowContextCreated = 0;
        windowContextClosed = 0;
    }

    public void startConversationCalled(@Observes StartConversationEvent event)
    {
        conversationStarted = true;
        startedConversation = event.getConversation();
    }

    public void closeConversationCalled(@Observes CloseConversationEvent event)
    {
        conversationClosed = true;
    }

    public void restartConversationCalled(@Observes RestartConversationEvent event)
    {
        conversationRestarted = true;
    }

    public void beanScoped(@Observes ScopeBeanEvent event)
    {
        Serializable bean = event.getBeanInstance();

        if (bean instanceof ConversationBean)
        {
            conversationBeanScoped++;
        }
        else if (bean instanceof ViewAccessBean)
        {
            viewaccessBeanScoped++;
        }
        else if (bean instanceof WindowBean)
        {
            windowBeanScoped++;
        }
    }

    public void beanUnscoped(@Observes UnscopeBeanEvent event)
    {
        Serializable bean = event.getBeanInstance();

        if (bean instanceof ConversationBean)
        {
            conversationBeanUnscoped++;
        }
        else if (bean instanceof ViewAccessBean)
        {
            viewaccessBeanUnscoped++;
        }
        else if (bean instanceof WindowBean)
        {
            windowBeanUnscoped++;
        }
    }

    public void beanAccessed(@Observes AccessBeanEvent event)
    {
        Serializable bean = event.getBeanInstance();

        if (bean instanceof ConversationBean)
        {
            conversationBeanAccessed++;
        }
        else if (bean instanceof ViewAccessBean)
        {
            viewaccessBeanAccessed++;
        }
        else if (bean instanceof WindowBean)
        {
            windowBeanAccessed++;
        }
    }

    public void windowContextCreated(@Observes CreateWindowContextEvent event)
    {
        windowContextCreated++;
    }

    public void windowContextClosed(@Observes CloseWindowContextEvent event)
    {
        windowContextClosed++;
    }

    public String closeStartedConversationAndNavigateToTest1()
    {
        startedConversation.close();
        
        return "conversation-events-test1.xhtml";
    }

    public String resetAndStartConversationTest()
    {
        initialize();

        return "conversation-events-test2.xhtml";
    }

    public String resetAndStartScopeBeanTest()
    {
        initialize();

        return "scope-bean-event-test2.xhtml";
    }

    public String closeWindowContext()
    {
        windowContext.close();

        return null;
    }

    // Getter and Setter
    
    public boolean isConversationStarted()
    {
        return conversationStarted;
    }

    public void setConversationStarted(boolean conversationStarted)
    {
        this.conversationStarted = conversationStarted;
    }

    public Conversation getStartedConversation()
    {
        return startedConversation;
    }

    public void setStartedConversation(Conversation startedConversation)
    {
        this.startedConversation = startedConversation;
    }

    public boolean isConversationClosed()
    {
        return conversationClosed;
    }

    public void setConversationClosed(boolean conversationClosed)
    {
        this.conversationClosed = conversationClosed;
    }

    public boolean isConversationRestarted()
    {
        return conversationRestarted;
    }

    public void setConversationRestarted(boolean conversationRestarted)
    {
        this.conversationRestarted = conversationRestarted;
    }

    public int getConversationBeanScoped()
    {
        return conversationBeanScoped;
    }

    public void setConversationBeanScoped(int conversationBeanScoped)
    {
        this.conversationBeanScoped = conversationBeanScoped;
    }

    public int getViewaccessBeanScoped()
    {
        return viewaccessBeanScoped;
    }

    public void setViewaccessBeanScoped(int viewaccessBeanScoped)
    {
        this.viewaccessBeanScoped = viewaccessBeanScoped;
    }

    public int getWindowBeanScoped()
    {
        return windowBeanScoped;
    }

    public void setWindowBeanScoped(int windowBeanScoped)
    {
        this.windowBeanScoped = windowBeanScoped;
    }

    public int getConversationBeanUnscoped()
    {
        return conversationBeanUnscoped;
    }

    public void setConversationBeanUnscoped(int conversationBeanUnscoped)
    {
        this.conversationBeanUnscoped = conversationBeanUnscoped;
    }

    public int getViewaccessBeanUnscoped()
    {
        return viewaccessBeanUnscoped;
    }

    public void setViewaccessBeanUnscoped(int viewaccessBeanUnscoped)
    {
        this.viewaccessBeanUnscoped = viewaccessBeanUnscoped;
    }

    public int getWindowBeanUnscoped()
    {
        return windowBeanUnscoped;
    }

    public void setWindowBeanUnscoped(int windowBeanUnscoped)
    {
        this.windowBeanUnscoped = windowBeanUnscoped;
    }

    public int getConversationBeanAccessed()
    {
        return conversationBeanAccessed;
    }

    public void setConversationBeanAccessed(int conversationBeanAccessed)
    {
        this.conversationBeanAccessed = conversationBeanAccessed;
    }

    public int getViewaccessBeanAccessed()
    {
        return viewaccessBeanAccessed;
    }

    public void setViewaccessBeanAccessed(int viewaccessBeanAccessed)
    {
        this.viewaccessBeanAccessed = viewaccessBeanAccessed;
    }

    public int getWindowBeanAccessed()
    {
        return windowBeanAccessed;
    }

    public void setWindowBeanAccessed(int windowBeanAccessed)
    {
        this.windowBeanAccessed = windowBeanAccessed;
    }

    public int getWindowContextCreated()
    {
        return windowContextCreated;
    }

    public void setWindowContextCreated(int windowContextCreated)
    {
        this.windowContextCreated = windowContextCreated;
    }

    public int getWindowContextClosed()
    {
        return windowContextClosed;
    }

    public void setWindowContextClosed(int windowContextClosed)
    {
        this.windowContextClosed = windowContextClosed;
    }
}
