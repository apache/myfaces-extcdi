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
package org.apache.myfaces.extensions.cdi.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntryFactory;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;

import javax.enterprise.inject.Typed;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class RequestCache
{
    private static ThreadLocal<WindowContext> windowContextCache = new ThreadLocal<WindowContext>();

    private static ThreadLocal<WindowContextManager> windowContextManagerCache
            = new ThreadLocal<WindowContextManager>();

    private static ThreadLocal<BeanEntryFactory> beanEntryFactoryCache
            = new ThreadLocal<BeanEntryFactory>();

    private static ThreadLocal<Map<ConversationKey, EditableConversation>> conversationCache
            = new ThreadLocal<Map<ConversationKey, EditableConversation>>();

    protected RequestCache()
    {
    }

    /**
     * Resets all caches
     */
    public static void resetCache()
    {
        windowContextManagerCache.set(null);
        windowContextManagerCache.remove();

        beanEntryFactoryCache.set(null);
        beanEntryFactoryCache.remove();

        windowContextCache.set(null);
        windowContextCache.remove();

        resetConversationCache();
    }

    /**
     * Resets conversation caches only
     */
    public static void resetConversationCache()
    {
        conversationCache.set(null);
        conversationCache.remove();
    }

    /**
     * Exposes the cached {@link WindowContextManager}. If there isn't a cached instance a new one will be created.
     * @return current window-context-manager
     */
    public static WindowContextManager getWindowContextManager()
    {
        WindowContextManager windowContextManager = windowContextManagerCache.get();

        if(windowContextManager == null)
        {
            windowContextManager = CodiUtils.getContextualReferenceByClass(WindowContextManager.class);
            windowContextManagerCache.set(windowContextManager);
        }

        return windowContextManager;
    }

    /**
     * Exposes the cached {@link BeanEntryFactory}. If there isn't a cached instance a new one will be created.
     * @return current bean-entry-factory
     */
    public static BeanEntryFactory getBeanEntryFactory()
    {
        BeanEntryFactory beanEntryFactory = beanEntryFactoryCache.get();

        if(beanEntryFactory == null)
        {
            beanEntryFactory = CodiUtils.getContextualReferenceByClass(BeanEntryFactory.class);
            beanEntryFactoryCache.set(beanEntryFactory);
        }

        return beanEntryFactory;
    }

    /**
     * Exposes the cached {@link WindowContext}
     * @return cached window-context or null if there is no instance
     */
    public static WindowContext getCurrentWindowContext()
    {
        return windowContextCache.get();
    }

    /**
     * Caches the current {@link WindowContext}
     * @param windowContext current window-context
     */
    public static void setCurrentWindowContext(WindowContext windowContext)
    {
        windowContextCache.set(windowContext);
    }

    /**
     * Exposes the cached {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation}
     * for the given {@link ConversationKey}
     * @param conversationKey current conversation-key
     * @return cached conversation for the given key or null if there is no conversation with the given key
     */
    public static EditableConversation getConversation(ConversationKey conversationKey)
    {
        return getConversationCache().get(conversationKey);
    }

    /**
     * Caches the given {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation}
     * @param conversationKey key of the conversation
     * @param conversation current conversation
     */
    public static void setConversation(ConversationKey conversationKey, EditableConversation conversation)
    {
        getConversationCache().put(conversationKey, conversation);
    }

    private static Map<ConversationKey, EditableConversation> getConversationCache()
    {
        Map<ConversationKey, EditableConversation> conversationMap = conversationCache.get();

        if(conversationMap == null)
        {
            conversationMap = new HashMap<ConversationKey, EditableConversation>();
            conversationCache.set(conversationMap);
        }
        return conversationMap;
    }
}
