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
package org.apache.myfaces.extensions.cdi.core.test.alternative.config;

import org.apache.myfaces.extensions.cdi.core.alternative.config.AlternativeCodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.alternative.scope.conversation.config.AlternativeConversationConfig;
import org.apache.myfaces.extensions.cdi.core.alternative.scope.conversation.config.AlternativeWindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CoreConfigTest
{
    @Test
    public void testAlternativeCodiCoreConfig()
    {
        CodiCoreConfig codiCoreConfig = new AlternativeCodiCoreConfig();

        //changed via config see myfaces-extcdi.properties
        assertEquals(codiCoreConfig.isAdvancedQualifierRequiredForDependencyInjection(), false);

        assertEquals(codiCoreConfig.isConfigurationLoggingEnabled(), true);
        assertEquals(codiCoreConfig.isInvalidBeanCreationEventEnabled(), false);
    }

    @Test
    public void testAlternativeConversationConfig()
    {
        ConversationConfig conversationConfig = new AlternativeConversationConfig();

        assertEquals(conversationConfig.isConversationRequiredEnabled(), true);
        assertEquals(conversationConfig.getConversationTimeoutInMinutes(), 30);
        assertEquals(conversationConfig.isAccessBeanEventEnabled(), false);
        assertEquals(conversationConfig.isCloseConversationEventEnabled(), false);
        assertEquals(conversationConfig.isStartConversationEventEnabled(), false);
        assertEquals(conversationConfig.isRestartConversationEventEnabled(), false);
        assertEquals(conversationConfig.isScopeBeanEventEnabled(), false);
        assertEquals(conversationConfig.isUnscopeBeanEventEnabled(), false);
    }

    @Test
    public void testAlternativeWindowContextConfig()
    {
        WindowContextConfig windowContextConfig = new AlternativeWindowContextConfig();

        assertEquals(windowContextConfig.getMaxWindowContextCount(), 64);
        assertEquals(windowContextConfig.getWindowContextTimeoutInMinutes(), 60);
        assertEquals(windowContextConfig.isUrlParameterSupported(), true);
        assertEquals(windowContextConfig.isUnknownWindowIdsAllowed(), false);
        assertEquals(windowContextConfig.isAddWindowIdToActionUrlsEnabled(), false);
        assertEquals(windowContextConfig.isCloseEmptyWindowContextsEnabled(), false);
        assertEquals(windowContextConfig.isEagerWindowContextDetectionEnabled(), true);
        assertEquals(windowContextConfig.isCreateWindowContextEventEnabled(), false);
        assertEquals(windowContextConfig.isCloseWindowContextEventEnabled(), false);
    }
}
