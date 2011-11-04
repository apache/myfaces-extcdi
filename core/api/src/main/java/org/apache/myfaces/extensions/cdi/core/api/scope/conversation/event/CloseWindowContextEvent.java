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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;

/**
 * Event which gets fired directly before the current {@link WindowContext} gets closed.<p/>
 * Attention: It's deactivated per default.
 */
public final class CloseWindowContextEvent extends WindowContextEvent
{
    /**
     * Constructor for creating the event for the given window-context
     * @param windowContext window-context for which the event gets created
     */
    public CloseWindowContextEvent(WindowContext windowContext)
    {
        super(windowContext);
    }
}