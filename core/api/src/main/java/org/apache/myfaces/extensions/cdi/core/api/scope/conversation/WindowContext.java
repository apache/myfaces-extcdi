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

import org.apache.myfaces.extensions.cdi.core.api.config.AttributeAware;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public interface WindowContext extends AttributeAware, Serializable
{
    /**
     * @return the id of the conversation (unique for each window/tab)
     */
    String getId();

    /**
     * @param conversationGroup group of the conversation in question
     * @param qualifiers optional qualifiers for the conversation
     * @return the removed conversation - null otherwise
     */
    Conversation endConversation(Class conversationGroup, Annotation... qualifiers);

    /**
     * destroys all conversation of a group independent of the qualifiers
     * @param conversationGroup group of the conversation in question
     * @return the removed conversation - null otherwise
     */
    Set<Conversation> endConversationGroup(Class conversationGroup);

    /**
     * invalidate all conversations immediately
     * attention: window scoped beans won't get destroyed.
     * if you would like to reset the whole context including window scoped beans, use {@link #end} or
     * {@link #endConversation} + WindowScoped.class as argument
     */
    void endConversations();

    /**
     * invalidate the whole {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * it also invalidates all <i>Codi</i> conversations immediately.
     */
    void end();

    /**
     * @return configuration of the current context
     */
    WindowContextConfig getConfig();
}
