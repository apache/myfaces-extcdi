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

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.InlineViewConfigRoot;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;

import javax.faces.event.PhaseId;
import javax.enterprise.inject.Typed;
import java.lang.reflect.Method;

/**
 * @author Gerhard Petracek
 */
//TODO create CODI exceptions
@Typed()
public abstract class ExceptionUtils
{
    private ExceptionUtils()
    {
        // prevent instantiation
    }

    /**
     * Creates an exception which signals that a user opened too many windows.
     * @return exception which can be thrown
     */
    public static RuntimeException tooManyOpenWindowException()
    {
        return new UnhandledException("Too many active windows/tabs have been opened!" +
            " Please continue with one of the existing windows.");
    }

    /**
     * Creates an exception in case of a custom implementation of {@link WindowContextManager} which doesn't implement
     * {@link EditableWindowContextManager}
     * @param windowContextManager found window-context-manager instance
     * @return exception which can be thrown
     */
    public static RuntimeException windowContextManagerNotEditableException(WindowContextManager windowContextManager)
    {
        return new UnhandledException(windowContextManager.getClass().getName() + " has to implement "
                + EditableWindowContextManager.class.getName());
    }

    /**
     * Creates an exception in case of a custom implementation of {@link WindowContext} which doesn't implement
     * {@link EditableWindowContext}
     * @param windowContext found window-context instance
     * @return exception which can be thrown
     */
    public static RuntimeException windowContextNotEditableException(WindowContext windowContext)
    {
        return new UnhandledException(windowContext.getClass().getName() + " has to implement "
                + EditableWindowContext.class.getName());
    }

    /**
     * Creates an exception in case of a custom implementation of {@link Conversation} which doesn't implement
     * {@link EditableConversation}
     * @param conversation found conversation instance
     * @return exception which can be thrown
     */
    public static RuntimeException conversationNotEditableException(Conversation conversation)
    {
        return new UnhandledException(conversation.getClass().getName() + " has to implement "
                + EditableConversation.class.getName());
    }

    /**
     * Creates an exception if a method is annotated with {@link BeforePhase} or {@link AfterPhase} and
     * has an unsupported signature
     * @param targetClass class which contains the method
     * @param method annotated but invalid method
     * @return exception which can be thrown
     */
    public static IllegalArgumentException invalidPhasesCallbackMethod(Class targetClass, Method method)
    {
        return new IllegalArgumentException(targetClass.getName() + "#" + method.getName() + " is annotated with " +
                BeforePhase.class.getName() + " or " + AfterPhase.class.getName() +
                " and the method signature isn't supported. " +
                "Supported arguments: no-args or one parameter of type: " +
                PhaseId.class.getName());
    }

    /**
     * Creates an exception in case of an unsupported usage of {@link BeforePhase}
     * @return exception which can be thrown
     */
    public static IllegalStateException unsupportedPhasesLifecycleCallback()
    {
        return new IllegalStateException("The usage of @ + " + BeforePhase.class.getName() +
                "(PhaseId.RESTORE_VIEW) as well as @" + BeforePhase.class.getName() + "(PhaseId.ANY_PHASE) "+
                "is not supported as request-lifecycle-callback. " +
                "If you really need it, use an phases-observer-method e.g.: " +
                "protected void preRestoreView(@Observes @BeforePhase(PhaseId.RESTORE_VIEW) PhaseEvent event) ");
    }

    /**
     * Creates an exception if there is no conversation with the given key
     * @param conversationKey current conversation-key
     * @return exception which can be thrown
     */
    public static IllegalArgumentException conversationNotFoundException(String conversationKey)
    {
        return new IllegalArgumentException("Cannot find conversation with key: " + conversationKey);
    }

    /**
     * Creates an exception if {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page}
     * is used for a page bean and there is no class annotated with {@link InlineViewConfigRoot}
     * to mark the package root
     * @param viewDefinitionClass class with the invalid usage
     * @return exception which can be thrown
     */
    public static IllegalStateException missingInlineViewConfigRootMarkerException(
            Class<? extends ViewConfig> viewDefinitionClass)
    {
        StringBuilder message = new StringBuilder();

        message.append(viewDefinitionClass.getName());
        message.append(" is an inline view-config and no page-root marker has been found. ");
        message.append("Please remove the @Page annotation ");
        message.append("or add a marker class or interface in the root package of your page-beans ");
        message.append("and annotate it with ");
        message.append(InlineViewConfigRoot.class.getName());
        message.append(" or refactor it to normal view-configs");
        
        throw new IllegalStateException(message.toString());
    }

    /**
     * Creates an exception if there are multiple classes annotated with {@link InlineViewConfigRoot} in an web-app.
     * @param storedPageClass registered class which hosts {@link InlineViewConfigRoot}
     * @param viewConfigRootClass current class which also hosts {@link InlineViewConfigRoot}
     * @return exception which can be thrown
     */
    public static IllegalStateException ambiguousViewConfigRootException(
            Class storedPageClass, Class viewConfigRootClass)
    {
        StringBuilder message = new StringBuilder();

        message.append("Inline view-configs don't support multiple page-root markers in the same application.\n");
        message.append("Refactor to normal view-configs or remove ");
        message.append(storedPageClass.getName());
        message.append(" or ");
        message.append(viewConfigRootClass.getName());

        throw new IllegalStateException(message.toString());
    }

    /**
     * Creates an exception if there are multiple classes which represent the same view-id
     * @param viewId current view-id
     * @param newDef current view-config class
     * @param existingDef registered view-config class
     * @return exception which can be thrown
     */
    public static IllegalArgumentException ambiguousViewDefinitionException(String viewId,
                                                                            Class<? extends ViewConfig> newDef,
                                                                            Class<? extends ViewConfig> existingDef)
    {
        return new IllegalArgumentException(viewId + " is already mapped to "
                + viewId + " via " + existingDef.getName()
                + " -> a further view definition (" +
                newDef.getName() + ") is invalid");
    }

    /**
     * Creates an exception if there are multiple classes which represent the same default-error-view
     * @param newDef current view-config class
     * @param existingDef registered view-config class
     * @return exception which can be thrown
     */
    public static IllegalStateException ambiguousDefaultErrorViewDefinitionException(Class<? extends ViewConfig> newDef,
                                                                         Class<? extends ViewConfig> existingDef)
    {
        return new IllegalStateException("multiple error pages found " +
                        existingDef.getName() + " and " +
                        newDef.getName());
    }
}
