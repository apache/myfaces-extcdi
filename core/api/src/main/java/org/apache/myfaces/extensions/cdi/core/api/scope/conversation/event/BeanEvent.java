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
package org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event;

import java.io.Serializable;

/**
 * Base class for all events related to beans.
 */
public abstract class BeanEvent
{
    private final Serializable bean;

    /**
     * Constructor for creating the event for the given bean-instance
     * @param bean bean-instance (unproxied) for which the event gets created
     */
    public BeanEvent(Serializable bean)
    {
        this.bean = bean;
    }

    /**
     * The current bean instance
     * @return the current bean instance
     */
    public final Serializable getBeanInstance()
    {
        return bean;
    }
}