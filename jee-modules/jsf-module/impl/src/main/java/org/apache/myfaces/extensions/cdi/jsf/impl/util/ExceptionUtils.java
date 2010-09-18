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
package org.apache.myfaces.extensions.cdi.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;

import javax.faces.event.PhaseId;
import java.lang.reflect.Method;

/**
 * @author Gerhard Petracek
 */
//TODO create CODI exceptions
public class ExceptionUtils
{
    public static RuntimeException tooManyOpenWindowException(int windowContextTimeoutInMinutes)
    {
        return new RuntimeException("Too many active windows/tabs have been opened!" +
            " Please continue with one of the existing windows or wait up to "
                + windowContextTimeoutInMinutes + " minutes.");
    }

    public static RuntimeException windowContextManagerNotEditableException(WindowContextManager windowContextManager)
    {
        return new RuntimeException(windowContextManager.getClass().getName() + " has to implement "
                + EditableWindowContextManager.class.getName());
    }

    public static RuntimeException windowContextNotEditableException(WindowContext windowContext)
    {
        return new RuntimeException(windowContext.getClass().getName() + " has to implement "
                + EditableWindowContext.class.getName());
    }

    public static RuntimeException conversationNotEditableException(Conversation conversation)
    {
        return new RuntimeException(conversation.getClass().getName() + " has to implement "
                + EditableConversation.class.getName());
    }

    public static RuntimeException invalidViewException(String viewId)
    {
        return new RuntimeException("View-ID: " + viewId + " doesn't exist.");
    }

    public static IllegalArgumentException invalidPhasesCallbackMethod(Class targetClass, Method method)
    {
        return new IllegalArgumentException(targetClass.getName() + "#" + method.getName() + " is annotated with " +
                BeforePhase.class.getName() + " or " + AfterPhase.class.getName() +
                " and the method signature isn't supported. " +
                "Supported arguments: no-args or one parameter of type: " +
                PhaseId.class.getName());
    }

    public static IllegalStateException unsupportedPhasesLifecycleCallback()
    {
        return new IllegalStateException("The usage of @ + " + BeforePhase.class.getName() +
                "(PhaseId.RESTORE_VIEW) as well as @" + BeforePhase.class.getName() + "(PhaseId.ANY_PHASE) "+
                "is not supported as request-lifecycle-callback. " +
                "If you really need it, use an phases-observer-method e.g.: " +
                "protected void preRestoreView(@Observes @BeforePhase(PhaseId.RESTORE_VIEW) PhaseEvent event) ");
    }
}
