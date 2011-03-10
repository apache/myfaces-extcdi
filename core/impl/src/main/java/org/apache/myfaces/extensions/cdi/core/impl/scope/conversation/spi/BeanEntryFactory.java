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

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.context.spi.CreationalContext;

/**
 * @author Gerhard Petracek
 */
public interface BeanEntryFactory
{
    /**
     * Creates a new {@link BeanEntry} which will be used for storing all data needed for creating and managing
     * a scoped bean
     *
     * @param bean current bean
     * @param creationalContext context for the current bean
     * @param scopeBeanEventEnabled flag which indicates if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.ScopeBeanEvent} should be fired
     * @param accessBeanEventEnabled flag which indicates if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.AccessBeanEvent} should be fired
     * @param unscopeBeanEventEnabled flag which indicates if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.UnscopeBeanEvent} should be fired
     * @param <T> current type
     * @return entry which will be stored by one of the CODI scopes
     */
    <T> BeanEntry<T> createBeanEntry(Bean<T> bean,
                                     CreationalContext<T> creationalContext,
                                     boolean scopeBeanEventEnabled,
                                     boolean accessBeanEventEnabled,
                                     boolean unscopeBeanEventEnabled);
}
