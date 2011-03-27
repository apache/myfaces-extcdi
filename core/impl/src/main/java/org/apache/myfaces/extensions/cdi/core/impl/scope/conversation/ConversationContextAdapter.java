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
package org.apache.myfaces.extensions.cdi.core.impl.scope.conversation;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
public class ConversationContextAdapter implements Context
{
    private final Class<? extends Annotation> scopeType;
    private final AbstractGroupedConversationContext conversationContext;

    /**
     * Constructor which allows to build a context for conversations for the given scope-type and scope-implementation
     * @param scope type of the scope
     * @param conversationContext implementation of the conversation scope
     */
    public ConversationContextAdapter(Class<? extends Annotation> scope,
                                      AbstractGroupedConversationContext conversationContext)
    {
        this.scopeType = scope;
        this.conversationContext = conversationContext;
    }

    /** {@inheritDoc} */
    public Class<? extends Annotation> getScope()
    {
        return this.scopeType;
    }

    /**
     * @param component         descriptor of the bean
     * @param creationalContext context for creating a bean
     * @return a scoped bean-instance
     */
    public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext)
    {
        if (component instanceof Bean)
        {
            return this.conversationContext.create((Bean<T>)component, creationalContext);
        }

        Class invalidComponentClass = component.create(creationalContext).getClass();
        throw new IllegalStateException(invalidComponentClass + " is no valid conversation scoped bean");
    }

    /**
     * @param component descriptor of the bean
     * @return an instance of the requested bean if it already exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    public <T> T get(Contextual<T> component)
    {
        if (component instanceof Bean)
        {
            return this.conversationContext.resolve((Bean<T>)component);
        }
        throw new IllegalStateException(component.getClass() + " is no valid conversation scoped bean");
    }

    /** {@inheritDoc} */
    public boolean isActive()
    {
        return this.conversationContext.isActive();
    }
}
