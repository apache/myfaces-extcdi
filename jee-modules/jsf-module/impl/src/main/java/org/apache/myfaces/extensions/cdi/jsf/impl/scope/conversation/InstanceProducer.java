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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import static org.apache.myfaces.extensions.cdi.core.api.CoreModuleBeanNames.*;
import static org.apache.myfaces.extensions.cdi.core.impl.CoreModuleBeanNames.*;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.util.UnmodifiableMap;
import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.getOrCreateScopedInstanceOfBeanByClass;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowContextManagerFactory;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.*;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
final class InstanceProducer
{
    @Produces
    @SessionScoped
    @Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)
    protected EditableWindowContextManager createWindowContextManager(WindowContextConfig windowContextConfig,
                                                                      ConversationConfig conversationConfig,
                                                                      ProjectStage projectStage,
                                                                      BeanManager beanManager)
    {
        WindowContextManagerFactory windowContextManagerFactory =
                getOrCreateScopedInstanceOfBeanByClass(beanManager, WindowContextManagerFactory.class, true);

        if(windowContextManagerFactory != null)
        {
            return windowContextManagerFactory.createWindowContextManager(windowContextConfig, conversationConfig);
        }
        return new DefaultWindowContextManager(windowContextConfig, conversationConfig, projectStage, beanManager);
    }

    protected void destroyAllConversations(
            @Disposes @Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)WindowContextManager windowContextManager)
    {
        if(windowContextManager instanceof EditableWindowContextManager)
        {
            ((EditableWindowContextManager)windowContextManager).closeAllWindowContexts();
        }
        RequestCache.resetCache();
    }

    @Produces
    @Named(CURRENT_WINDOW_CONTEXT_BEAN_NAME)
    @RequestScoped
    protected EditableWindowContext currentWindowContext(WindowContextManager windowContextManager)
    {
        WindowContext windowContext = windowContextManager.getCurrentWindowContext();

        if(windowContext instanceof EditableWindowContext)
        {
            return (EditableWindowContext)windowContext;
        }

        throw windowContextNotEditableException(windowContext);
    }

    @Produces
    @Named(CURRENT_WINDOW_BEAN_NAME)
    @RequestScoped
    protected Map<String, Object> currentWindow(final WindowContextManager windowContextManager)
    {
        return new UnmodifiableMap<String, Object>() {
            private static final long serialVersionUID = 2356468240049980467L;

            @Override
            public Object get(Object key)
            {
                if(key == null || !(key instanceof String))
                {
                    return null;
                }

                String attributeKey = key.toString();

                if("id".equalsIgnoreCase(attributeKey))
                {
                    return windowContextManager.getCurrentWindowContext().getId();
                }

                if("useNewId".equalsIgnoreCase(key.toString()))
                {
                    return ""; //return an empty string as signal for ConversationUtils#resolveWindowContextId
                }
                return windowContextManager.getCurrentWindowContext().getAttribute(attributeKey, Object.class);
            }
        };
    }

    @Produces
    @Dependent
    protected EditableConversation currentConversation(InjectionPoint injectionPoint,
                                               WindowContextManager windowContextManager)
    {
        //for @Inject Conversation conversation;
        return new InjectableConversation(injectionPoint.getBean(), windowContextManager);
    }
}
