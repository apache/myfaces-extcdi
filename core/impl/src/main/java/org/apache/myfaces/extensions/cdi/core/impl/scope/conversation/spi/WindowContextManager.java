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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;

import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
public interface WindowContextManager extends Serializable
{
    /**
     * Key for storing the window-id e.g. in URLs
     */
    String WINDOW_CONTEXT_ID_PARAMETER_KEY = "windowId";

    /**
     * Value which can be used as "window-id" by external clients which aren't aware of windows.
     * It deactivates e.g. the redirect for the initial request.
     */
    String AUTOMATED_ENTRY_POINT_PARAMETER_KEY = "automatedEntryPoint";

    /**
     * Resolves the current {@link WindowContext}
     * @return current window-context
     */
    WindowContext getCurrentWindowContext();

    /**
     * Resolves the {@link WindowContext} for the given window-id
     * @param windowContextId window-id
     * @return window-context of the given window-id
     */
    WindowContext getWindowContext(String windowContextId);
}
