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
package org.apache.myfaces.extensions.cdi.test.spi;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Interface for handling a CDI container
 *
 * @author Gerhard Petracek
 */
public interface CdiTestContainer extends TestContainer
{
    /**
     * Starts all contexts of the container
     */
    void startContexts();

    /**
     * Stops all contexts of the container
     */
    void stopContexts();

    /**
     * Resolves the current {@link BeanManager}
     * @return current bean-manager
     */
    BeanManager getBeanManager();

    /**
     * Performs manual dependency injection.
     * It's needed for classes which aren't managed by CDI
     * @param instance instance which isn't managed by CDI
     * @param <T> current type
     * @return the given instance with injected beans
     */
    <T> T injectFields(T instance);
}
