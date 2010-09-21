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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import static org.apache.myfaces.extensions.cdi.core.api.CoreModuleBeanNames.*;
import static org.apache.myfaces.extensions.cdi.core.impl.CoreModuleBeanNames.*;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowContextManagerFactory;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
final class InstanceProducer
{
    @Produces
    @SessionScoped
    @Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)
    protected EditableWindowContextManager createWindowContextManager(ConfigResolver configResolver,
                                                                      ProjectStage projectStage)
    {
        JsfAwareWindowContextConfig jsfAwareWindowContextConfig =
                configResolver.resolve(JsfAwareWindowContextConfig.class);

        WindowContextManagerFactory windowContextManagerFactory =
                jsfAwareWindowContextConfig.getWindowContextManagerFactory();

        if(windowContextManagerFactory != null)
        {
            return windowContextManagerFactory.createWindowContextManager(jsfAwareWindowContextConfig);
        }
        return new DefaultWindowContextManager(jsfAwareWindowContextConfig, projectStage);
    }

    protected void destroyAllConversations(
            @Disposes @Named(WINDOW_CONTEXT_MANAGER_BEAN_NAME)WindowContextManager windowContextManager)
    {
        if(windowContextManager instanceof EditableWindowContextManager)
        {
            ((EditableWindowContextManager)windowContextManager).destroy();
        }
    }

    @Produces
    @Named(CURRENT_WINDOW_CONTEXT_BEAN_NAME)
    @RequestScoped
    protected WindowContext currentWindowContext(WindowContextManager windowContextManager)
    {
        return windowContextManager.getCurrentWindowContext();
    }

    @Produces
    @Named(CURRENT_WINDOW_BEAN_NAME)
    @RequestScoped
    protected Map<String, Object> currentWindow(final WindowContextManager windowContextManager)
    {
        return new HashMap<String, Object>() {
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
    protected Conversation currentConversation(final InjectionPoint injectionPoint,
                                               final WindowContextManager windowContextManager)
    {
        //for @Inject Conversation conversation;
        return new Conversation()
        {
            private static final long serialVersionUID = 7754789230388003028L;

            public void close()
            {
                findConversation().close();
            }

            public void restart()
            {
                findConversation().restart();
            }

            private Conversation findConversation()
            {
                Bean<?> bean = injectionPoint.getBean();
                Class conversationGroup = ConversationUtils.getConversationGroup(bean);

                Set<Annotation> qualifiers = bean.getQualifiers();

                return ((EditableWindowContext)windowContextManager.getCurrentWindowContext())
                        .getConversation(conversationGroup, qualifiers.toArray(new Annotation[qualifiers.size()]));
            }
        };
    }
}
