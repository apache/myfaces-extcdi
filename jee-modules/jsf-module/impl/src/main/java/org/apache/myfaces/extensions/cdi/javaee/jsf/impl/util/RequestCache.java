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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationKey;

import javax.enterprise.inject.spi.Bean;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
public class RequestCache
{
    private static ThreadLocal<Long> windowIdCache = new ThreadLocal<Long>();

    private static ThreadLocal<WindowContextManager> windowContextManagerCache
            = new ThreadLocal<WindowContextManager>();

    private static ThreadLocal<Bean<WindowContextManager>> windowContextManagerBeanCache
            = new ThreadLocal<Bean<WindowContextManager>>();

    private static ThreadLocal<Map<ConversationKey, Conversation>> conversationCache
            = new ThreadLocal<Map<ConversationKey, Conversation>>();

    public static void resetCache()
    {
        windowContextManagerCache.remove();
        windowContextManagerBeanCache.remove();
        windowIdCache.remove();
        resetConversationCache();
    }

    public static void resetConversationCache()
    {
        conversationCache.remove();
    }

    public static WindowContextManager getWindowContextManager()
    {
        WindowContextManager windowContextManager = windowContextManagerCache.get();

        if(windowContextManager == null)
        {
            return resolveWindowContextManager(resolveWindowContextManagerBean());
        }

        /* TODO remove it after tests
        if(windowContextManager == null)
        {
            windowContextManager = CodiUtils.getOrCreateScopedInstanceOfBeanByName(
                WindowContextManager.WINDOW_CONTEXT_MANAGER_BEAN_NAME, WindowContextManager.class);

            windowContextManagerCache.set(windowContextManager);
        }
        */

        return windowContextManager;
    }

    private static WindowContextManager resolveWindowContextManager(Bean<WindowContextManager> windowContextManagerBean)
    {
        WindowContextManager windowContextManager = windowContextManagerCache.get();

        if(windowContextManager == null)
        {
            windowContextManager = CodiUtils.getOrCreateScopedInstanceOfBean(windowContextManagerBean);
            windowContextManagerCache.set(windowContextManager);
        }

        return windowContextManager;
    }

    private static Bean<WindowContextManager> resolveWindowContextManagerBean()
    {
        Bean<WindowContextManager> windowContextManagerBean = windowContextManagerBeanCache.get();

        if(windowContextManagerBean == null)
        {
            windowContextManagerBean = ConversationUtils.resolveConversationManagerBean();
            windowContextManagerBeanCache.set(windowContextManagerBean);
        }

        return windowContextManagerBean;
    }

    public static Long getCurrentWindowId()
    {
        return windowIdCache.get();
    }

    public static void setCurrentWindowId(Long windowId)
    {
        windowIdCache.set(windowId);
    }

    public static Conversation getConversation(ConversationKey conversationKey)
    {
        return getConversationCache().get(conversationKey);
    }

    public static void setConversation(ConversationKey conversationKey, Conversation conversation)
    {
        getConversationCache().put(conversationKey, conversation);
    }

    private static Map<ConversationKey, Conversation> getConversationCache()
    {
        Map<ConversationKey, Conversation> conversationMap = conversationCache.get();

        if(conversationMap == null)
        {
            conversationMap = new HashMap<ConversationKey, Conversation>();
            conversationCache.set(conversationMap);
        }
        return conversationMap;
    }
}
