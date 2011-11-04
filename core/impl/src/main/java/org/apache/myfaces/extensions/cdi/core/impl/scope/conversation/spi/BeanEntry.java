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
package org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.io.Serializable;

/**
 * Stores a conversation scoped bean instance and it's configuration
 */
public interface BeanEntry<T> extends Serializable
{
    /**
     * {@link Bean} of the current entry
     * @return current bean
     */
    Bean<T> getBean();

    /**
     * {@link CreationalContext} of the current entry
     * @return creational-context of the bean
     */
    CreationalContext<T> getCreationalContext();

    /**
     * Scoped instance which was created based on the {@link Bean} of the current entry.
     * If it hasn't been created, it will be created automatically.
     * @return instance of the bean
     */
    T getBeanInstance();

    /**
     * Resets the bean instance to null
     * @return the old instance
     */
    T resetBeanInstance();

    /**
     * Flag which indicates if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.ScopeBeanEvent} is enabled
     * @return true if the event is enabled, false otherwise
     */
    boolean isScopeBeanEventEnabled();

    /**
     * Flag which indicates if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.AccessBeanEvent} is enabled
     * @return true if the event is enabled, false otherwise
     */
    boolean isAccessBeanEventEnabled();

    /**
     * Flag which indicates if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.UnscopeBeanEvent} is enabled
     * @return true if the event is enabled, false otherwise
     */
    boolean isUnscopeBeanEventEnabled();
}
