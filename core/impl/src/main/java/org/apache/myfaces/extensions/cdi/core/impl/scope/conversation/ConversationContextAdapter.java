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

import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.core.api.security.event.InvalidBeanCreationEvent;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Adapter for all CODI conversation scopes
 */
public class ConversationContextAdapter implements Context
{
    private final Class<? extends Annotation> scopeType;
    private final AbstractGroupedConversationContext conversationContext;
    private BeanManager beanManager;
    private Boolean invalidBeanCreationEventEnabled;

    /**
     * Constructor which allows to build a context for conversations for the given scope-type and scope-implementation
     * @param scope type of the scope
     * @param conversationContext implementation of the conversation scope
     * @param beanManager current bean-manager
     */
    public ConversationContextAdapter(Class<? extends Annotation> scope,
                                      AbstractGroupedConversationContext conversationContext,
                                      BeanManager beanManager)
    {
        this.scopeType = scope;
        this.conversationContext = conversationContext;
        this.beanManager = beanManager;
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
            //since the basic use-case is related to topics like security,
            //we only have to check it at the very first access - everything else would have a major performance impact
            checkForInvalidBeanAccess((Bean<T>)component);

            //cdi doesn't support to throw an exception -> create the bean in any case - codi has to handle the rest
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

    private <T> void checkForInvalidBeanAccess(Bean<T> bean)
    {
        lazyInit();

        if(!this.invalidBeanCreationEventEnabled)
        {
            return;
        }
        
        Set<SecurityViolation> violations = this.conversationContext.checkPermission(bean);
        Set<SecurityViolation> violationsToThrow = new HashSet<SecurityViolation>();

        for(SecurityViolation securityViolation : violations)
        {
            InvalidBeanCreationEvent invalidBeanCreationEvent = new InvalidBeanCreationEvent(securityViolation);
            this.beanManager.fireEvent(invalidBeanCreationEvent);

            if(invalidBeanCreationEvent.isThrowSecurityViolation())
            {
                violationsToThrow.add(securityViolation);
            }
        }

        if(!violationsToThrow.isEmpty())
        {
            throw new AccessDeniedException(violationsToThrow, DefaultErrorView.class);
        }
    }

    private void lazyInit()
    {
        if(this.invalidBeanCreationEventEnabled == null)
        {
            init();
        }
    }

    private synchronized void init()
    {
        // switch into paranoia mode
        if(this.invalidBeanCreationEventEnabled == null)
        {
            CodiCoreConfig codiCoreConfig =
                    CodiUtils.getContextualReferenceByClass(this.beanManager, CodiCoreConfig.class);

            this.invalidBeanCreationEventEnabled = codiCoreConfig.isInvalidBeanCreationEventEnabled();
        }
    }
}
