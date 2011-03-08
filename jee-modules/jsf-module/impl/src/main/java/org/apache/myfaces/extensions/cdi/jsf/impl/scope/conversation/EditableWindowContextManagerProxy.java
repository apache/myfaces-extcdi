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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowContextManagerFactory;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.Collection;

import static org.apache.myfaces.extensions.cdi.core.impl.CoreModuleBeanNames.WINDOW_CONTEXT_MANAGER_BEAN_NAME;

/**
 * @author Gerhard Petracek
 */
@SessionScoped
@Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)
public class EditableWindowContextManagerProxy implements EditableWindowContextManager
{
    private static final long serialVersionUID = -7650399459577468233L;

    private EditableWindowContextManager editableWindowContextManager;

    protected EditableWindowContextManagerProxy()
    {
    }

    /**
     * Workaround for a producer-method which produces a session-scoped instance.
     * Workaround for Weld 1.x
     *
     * @param windowContextConfig current windowContextConfig
     * @param conversationConfig current conversationConfig
     * @param beanManager current beanManager
     */
    @Inject
    public EditableWindowContextManagerProxy(WindowContextConfig windowContextConfig,
                                             ConversationConfig conversationConfig,
                                             BeanManager beanManager)
    {
        WindowContextManagerFactory windowContextManagerFactory =
                CodiUtils.getContextualReferenceByClass(beanManager, WindowContextManagerFactory.class, true);

        if(windowContextManagerFactory != null)
        {
            this.editableWindowContextManager =
                    windowContextManagerFactory.createWindowContextManager(windowContextConfig, conversationConfig);
        }
        else
        {
            this.editableWindowContextManager =
                    new DefaultWindowContextManager(windowContextConfig, conversationConfig,
                            ProjectStageProducer.getInstance().getProjectStage() /*due to a weld issue*/,
                            beanManager);
        }
    }

    @PreDestroy
    protected void preDestroy()
    {
        this.editableWindowContextManager.closeAllWindowContexts();
        RequestCache.resetCache();
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public boolean activateWindowContext(String windowContextId)
    {
        return editableWindowContextManager.activateWindowContext(windowContextId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean activateWindowContext(EditableWindowContext windowContext)
    {
        return editableWindowContextManager.activateWindowContext(windowContext);
    }

    /**
     * {@inheritDoc}
     */
    public void restartConversations()
    {
        editableWindowContextManager.restartConversations();
    }

    /**
     * {@inheritDoc}
     */
    public void restartConversations(String windowContextId)
    {
        editableWindowContextManager.restartConversations(windowContextId);
    }

    /**
     * {@inheritDoc}
     */
    public void restartConversations(EditableWindowContext windowContext)
    {
        editableWindowContextManager.restartConversations(windowContext);
    }

    /**
     * {@inheritDoc}
     */
    public void closeCurrentWindowContext()
    {
        editableWindowContextManager.closeCurrentWindowContext();
    }

    /**
     * {@inheritDoc}
     */
    public void closeWindowContext(String windowContextId)
    {
        editableWindowContextManager.closeWindowContext(windowContextId);
    }

    /**
     * {@inheritDoc}
     */
    public void closeWindowContext(EditableWindowContext windowContext)
    {
        editableWindowContextManager.closeWindowContext(windowContext);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<EditableWindowContext> getWindowContexts()
    {
        return editableWindowContextManager.getWindowContexts();
    }

    /**
     * {@inheritDoc}
     */
    public void closeAllWindowContexts()
    {
        editableWindowContextManager.closeAllWindowContexts();
    }

    /**
     * {@inheritDoc}
     */
    public WindowContext getCurrentWindowContext()
    {
        return editableWindowContextManager.getCurrentWindowContext();
    }

    /**
     * {@inheritDoc}
     */
    public WindowContext getWindowContext(String windowContextId)
    {
        return editableWindowContextManager.getWindowContext(windowContextId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWindowContextActive(String windowContextId)
    {
        return editableWindowContextManager.isWindowContextActive(windowContextId);
    }
}
