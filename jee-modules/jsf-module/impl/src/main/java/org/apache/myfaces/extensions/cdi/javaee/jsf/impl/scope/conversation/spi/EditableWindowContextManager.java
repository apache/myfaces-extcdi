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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;

import java.util.Collection;

/**
 * @author Gerhard Petracek
 */
public interface EditableWindowContextManager extends WindowContextManager
{
    boolean activateWindowContext(String windowContextId);

    boolean activateWindowContext(EditableWindowContext windowContext);

    void resetCurrentWindowContext();

    void resetWindowContext(String windowContextId);

    void resetWindowContext(EditableWindowContext windowContext);

    void resetConversations();

    void resetConversations(String windowContextId);

    void resetConversations(EditableWindowContext windowContext);

    void removeCurrentWindowContext();

    void removeWindowContext(String windowContextId);

    void removeWindowContext(EditableWindowContext windowContext);

    Collection<EditableWindowContext> getWindowContexts();

    void destroy();
}
