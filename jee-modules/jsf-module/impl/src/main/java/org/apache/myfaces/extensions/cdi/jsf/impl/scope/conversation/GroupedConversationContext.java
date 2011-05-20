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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.api.tools.InvocationOrderComparator;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.AbstractGroupedConversationContext;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.AnyLiteral;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntry;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.BeanEntryFactory;
import org.apache.myfaces.extensions.cdi.core.api.security.BeanCreationDecisionVoter;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.WeldCache;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * jsf specific parts for managing grouped conversations
 *
 * @author Gerhard Petracek
 */
class GroupedConversationContext extends AbstractGroupedConversationContext
{
    private List<BeanCreationDecisionVoter> beanCreationDecisionVoters;

    private final boolean useFallback;

    GroupedConversationContext(BeanManager beanManager)
    {
        super(beanManager);

        this.useFallback = !beanManager.getClass().getName().startsWith("org.apache.webbeans.");
    }

    /**
     * @return true as soon as JSF is active
     *         the {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         will be created automatically
     */
    public boolean isActive()
    {
        return FacesContext.getCurrentInstance().getExternalContext().getSession(false) != null;
    }

    protected <T> Set<SecurityViolation> checkPermission(Bean<T> bean)
    {
        lazyInit();

        Set<SecurityViolation> violations = new HashSet<SecurityViolation>();

        for(BeanCreationDecisionVoter beanCreationDecisionVoter : this.beanCreationDecisionVoters)
        {
            Set<SecurityViolation> currentViolations = beanCreationDecisionVoter.checkPermission(bean);

            if(currentViolations != null)
            {
                violations.addAll(currentViolations);
            }
        }

        return violations;
    }

    private void lazyInit()
    {
        if(this.beanCreationDecisionVoters == null)
        {
            init();
        }
    }

    private synchronized void init()
    {
        // switch into paranoia mode
        if(this.beanCreationDecisionVoters == null)
        {
            this.beanCreationDecisionVoters = new ArrayList<BeanCreationDecisionVoter>();

            Set<? extends Bean> foundBeans =
                    this.beanManager.getBeans(BeanCreationDecisionVoter.class, new AnyLiteral());

            Bean<?> foundBean;
            Set<Bean<?>> beanSet;
            for(Bean<?> currentBean : foundBeans)
            {
                beanSet = new HashSet<Bean<?>>(1);
                beanSet.add(currentBean);
                foundBean = this.beanManager.resolve(beanSet);
                this.beanCreationDecisionVoters.add(
                        CodiUtils.getContextualReference(this.beanManager,
                                                         BeanCreationDecisionVoter.class,
                                                         (Bean<BeanCreationDecisionVoter>)foundBean));
            }
            Collections
                    .sort(this.beanCreationDecisionVoters, new InvocationOrderComparator<BeanCreationDecisionVoter>());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected WindowContextManager resolveWindowContextManager()
    {
        return RequestCache.getWindowContextManager();
    }

    /**
     * {@inheritDoc}
     */
    protected BeanEntryFactory resolveBeanEntryFactory()
    {
        return RequestCache.getBeanEntryFactory();
    }

    /**
     * @param windowContextManager the current
     * {@link org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager}
     * @param beanDescriptor      descriptor of the requested bean
     * @return the instance of the requested bean if it exists in the current
     *         {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext}
     *         null otherwise
     */
    protected <T> T resolveBeanInstance(WindowContextManager windowContextManager, Bean<T> beanDescriptor)
    {
        if(!(windowContextManager instanceof EditableWindowContextManager))
        {
            throw ExceptionUtils.windowContextManagerNotEditableException(windowContextManager);
        }

        Class<?> beanClass = beanDescriptor.getBeanClass();
        EditableConversation foundConversation = getConversation(
                (EditableWindowContextManager)windowContextManager, beanDescriptor);

        //noinspection unchecked
        return (T)foundConversation.getBean(beanClass);
    }

    /**
     * {@inheritDoc}
     */
    protected <T> void scopeBeanEntry(WindowContextManager windowContextManager, BeanEntry<T> beanEntry)
    {
        if(!(windowContextManager instanceof EditableWindowContextManager))
        {
            throw ExceptionUtils.windowContextManagerNotEditableException(windowContextManager);
        }

        Bean<?> bean = beanEntry.getBean();
        EditableConversation foundConversation =
                getConversation((EditableWindowContextManager)windowContextManager, bean);

        foundConversation.addBean(beanEntry);
    }

    /**
     * {@inheritDoc}
     */
    protected ConversationConfig getConversationConfig()
    {
        return CodiUtils.getContextualReferenceByClass(ConversationConfig.class);
    }

    private EditableConversation getConversation(EditableWindowContextManager windowContextManager, Bean<?> bean)
    {
        Class conversationGroup = ConversationUtils.getConversationGroup(bean);

        Set<Annotation> qualifiers = bean.getQualifiers();

        EditableWindowContext editableWindowContext = (EditableWindowContext)RequestCache.getCurrentWindowContext();

        if(editableWindowContext == null)
        {
            editableWindowContext = (EditableWindowContext)windowContextManager.getCurrentWindowContext();
            //also done by the default implementation but this also ensures that custom impls are fast
            RequestCache.setCurrentWindowContext(editableWindowContext);
        }

        try
        {
            if(this.useFallback)
            {
                WeldCache.setBean(bean);
            }

            return editableWindowContext
                    .getConversation(conversationGroup, qualifiers.toArray(new Annotation[qualifiers.size()]));
        }
        finally
        {
            if(this.useFallback)
            {
                WeldCache.resetBean();
            }
        }
    }
}
