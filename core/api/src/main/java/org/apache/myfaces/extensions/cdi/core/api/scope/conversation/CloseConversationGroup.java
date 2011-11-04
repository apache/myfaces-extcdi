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
package org.apache.myfaces.extensions.cdi.core.api.scope.conversation;

import javax.interceptor.InterceptorBinding;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Interceptor which allows to close a conversation-group after the invocation of the intercepted method.
 * If a custom {@link RuntimeException} is specified, the conversation is just closed if the exception occurred.
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})

@Inherited
@Documented

//cdi annotations
@InterceptorBinding
public @interface CloseConversationGroup
{
    /**
     * Specifies which exception (type) will trigger the termination of the current conversation.
     * @return exception which should trigger the termination of the current conversation.
     */
    @Nonbinding
    Class<? extends RuntimeException> on() default RuntimeException.class;

    /**
     * Specifies the conversation-group (specified implicitly or via {@link ConversationGroup})
     * which should be terminated.
     * @return group of the conversation which should be terminated
     */
    @Nonbinding
    Class<?> group() default CloseConversationGroup.class;
}
