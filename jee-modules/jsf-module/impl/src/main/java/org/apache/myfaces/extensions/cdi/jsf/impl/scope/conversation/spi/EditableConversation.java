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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;

import java.util.Set;

/**
 * Allows to implement custom implementations of {@link Conversation}
 */
public interface EditableConversation extends Conversation
{
    /**
     * @return evaluates and returns if the conversation is active
     */
    boolean isActive();

    /**
     * @return returns if the conversation is active (without evaluation)
     */
    boolean getActiveState();

    /**
     * has to expire a conversation. if the conversation is expired afterwards it has to be inactive
     */
    void deactivate();

    /**
     * Adds a {@link BeanEntry} which represents a scoped bean to the current conversation
     * @param beanInstance bean instance which should be added to the conversation
     * @param <T> tpye of the bean
     */
    <T> void addBean(BeanEntry<T> beanInstance);

    /**
     * @param key class of the requested bean
     * @param <T> type of the requested bean
     * @return an instance of the requested bean if the conversation is active - null otherwise
     */
    <T> T getBean(Class<T> key);

    /**
     * @param key class of the requested sub-group
     * @param <T> type of the requested group
     * @return a set of bean-types which are stored in the current conversation for the given group
     */
    <T> Set<Class<T>> getBeanSubGroup(Class<T> key);

    /**
     * Allows to remove a bean of the given type
     * @param type type of the bean
     * @param <T> target type
     * @return the bean entry of the removed bean or null if there was no bean in the conversation
     */
    <T> BeanEntry<T> removeBeanEntry(Class<T> type);

    //TODO
    //Set<BeanEntry<?>> getBeans();
}
