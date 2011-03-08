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

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;

import java.util.Collection;

/**
 * @author Gerhard Petracek
 */
public interface EditableWindowContextManager extends WindowContextManager
{
    /**
     * Activates the {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext} with has
     * the given window-id. If there is no
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * which has the given id a new context will be created automatically.
     * @param windowContextId window-id
     * @return true if the context was created successfully, false otherwise
     */
    boolean activateWindowContext(String windowContextId);

    /**
     * Activates the given {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * @param windowContext window-context which has to be activated
     * @return true if the context was created successfully, false otherwise
     */
    boolean activateWindowContext(EditableWindowContext windowContext);

    /*
    void resetCurrentWindowContext();

    void resetWindowContext(String windowContextId);

    void resetWindowContext(EditableWindowContext windowContext);
    */

    /**
     * Restarts all conversations of the current
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     */
    void restartConversations();

    /**
     * Restarts all conversations of the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * with the given window-id
     * @param windowContextId current window-id
     */
    void restartConversations(String windowContextId);

    /**
     * Restarts all conversations of the given
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * @param windowContext window-context which will be restarted (the conversations of it)
     */
    void restartConversations(EditableWindowContext windowContext);

    /**
     * Closes all conversations of the current
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     */
    void closeCurrentWindowContext();

    /**
     * Closes all conversations of the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * with the given window-id
     * @param windowContextId current window-id
     */
    void closeWindowContext(String windowContextId);

    /**
     * Closes all conversations of the given
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     * @param windowContext window-context which will be closed (the conversations of it)
     */
    void closeWindowContext(EditableWindowContext windowContext);

    /**
     * Exposes all {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext} instances for
     * the current user(-session)
     * @return all window-contexts available in the current user(-session)
     */
    Collection<EditableWindowContext> getWindowContexts();

    /**
     * Closes all {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext} of the current
     * user(-session)
     */
    void closeAllWindowContexts();

    /**
     * Evaluates if the {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext} with the
     * given window-id is currently active
     * @param windowContextId current window-id
     * @return true if the window-context with the given id is active, false otherwise
     */
    boolean isWindowContextActive(String windowContextId);
}
