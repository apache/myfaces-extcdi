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

import org.apache.myfaces.extensions.cdi.core.api.activation.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.AbstractGroupedConversationContext;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.ConversationContextAdapter;

import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.event.Observes;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * extension for registering the adapter for grouped conversations
 *
 * @author Gerhard Petracek
 */
public class GroupedConversationContextExtension implements Extension, Deactivatable
{
    /**
     * Adds codi scopes to the container
     * @param event after-bean-discovery event
     * @param manager current bean-manager
     */
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager)
    {
        if(!isActivated())
        {
            return;
        }

        //here we don't need CodiStartupBroadcaster.broadcastStartup(); see #validateScopes

        AbstractGroupedConversationContext codiConversationContext = new GroupedConversationContext(manager);
        event.addContext(new ConversationContextAdapter(WindowScoped.class, codiConversationContext, manager));
        event.addContext(new ConversationContextAdapter(ConversationScoped.class, codiConversationContext, manager));
        event.addContext(new ConversationContextAdapter(ViewAccessScoped.class, codiConversationContext, manager));
    }

    /**
     * Validates the correct usage of codi scopes.
     * @param processBean current process-bean
     */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public void validateScopes(@Observes ProcessBean processBean)
    {
        if(!isActivated())
        {
            return;
        }

        CodiStartupBroadcaster.broadcastStartup();

        Bean<?> bean = processBean.getBean();
        Set<Annotation> qualifiers = bean.getQualifiers();

        Class<? extends Annotation> annotationType;
        for(Annotation qualifier : qualifiers)
        {
            annotationType = qualifier.annotationType();

            if(ConversationGroup.class.isAssignableFrom(annotationType) &&
                    !ConversationScoped.class.isAssignableFrom(bean.getScope()))
            {
                String errorMessage = "Definition error in class: " + bean.getBeanClass().getName() +
                        "\nIt isn't allowed to use @" + ConversationGroup.class.getName() +
                        " in combination with @" + bean.getScope().getName() +
                        ".\nInstead of @" + bean.getScope().getName() + " you can use @" +
                        ConversationScoped.class.getName() + " or you have to remove the usage of @" +
                        ConversationGroup.class.getName();
                processBean.addDefinitionError(new IllegalStateException(errorMessage));
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
