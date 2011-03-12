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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.CloseConversationGroup;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.CloseConversationGroupStrategy;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * This interceptor should be used just in case of simple use-cases.
 * It's an alternative for injecting and using the
 * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext} or
 * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation} directly.
 * 
 * @author Gerhard Petracek
 */
@CloseConversationGroup
@Interceptor
public class CloseConversationGroupInterceptor implements Serializable
{
    private static final long serialVersionUID = -2440058119479555994L;

    @Inject
    private CloseConversationGroupStrategy closeConversationGroupStrategy;

    /**
     * Interceptor methods which closes the conversation of the bean after the execution of the method
     * or if the declared exception was thrown.
     * @param invocationContext current invocation-context
     * @return result of the intercepted method
     * @throws Exception exception which might be thrown by the intercepted method
     */
    @AroundInvoke
    public Object handleCloseConversation(InvocationContext invocationContext) throws Exception
    {
        return this.closeConversationGroupStrategy.execute(invocationContext);
    }
}
