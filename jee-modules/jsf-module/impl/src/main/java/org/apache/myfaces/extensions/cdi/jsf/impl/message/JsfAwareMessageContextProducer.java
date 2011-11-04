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
package org.apache.myfaces.extensions.cdi.jsf.impl.message;

import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import static org.apache.myfaces.extensions.cdi.jsf.api.JsfModuleBeanNames.MESSAGE_CONTEXT;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageFactory;
import org.apache.myfaces.extensions.cdi.message.impl.DefaultMessageContext;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ELProvider;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ArgumentFilter;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * Producer for I18n artifacts
 */
@ApplicationScoped
public class JsfAwareMessageContextProducer
{
    /**
     * Creates a specialized {@link MessageContext} for JSF which delegates to jsf mechanisms (as fallback)
     *
     * @param defaultMessageContext  pre-configured message-context
     * @param messageFactoryInstance current message-factory (optional)
     * @param elProviderInstance     current el-provider (optional)
     * @param argumentFilterInstance current argument-filter (optional)
     * @return optimized message-context for jsf-applications
     */
    @Produces
    @Dependent
    @Jsf
    public MessageContext createContext(MessageContext defaultMessageContext,
                                        Instance<MessageFactory> messageFactoryInstance,
                                        Instance<ELProvider> elProviderInstance,
                                        Instance<ArgumentFilter> argumentFilterInstance)
    {
        MessageFactory messageFactory = null;
        ELProvider elProvider = null;
        ArgumentFilter argumentFilter = null;

        if (!messageFactoryInstance.isUnsatisfied())
        {
            messageFactory = messageFactoryInstance.get();
        }

        if (!elProviderInstance.isUnsatisfied())
        {
            elProvider = elProviderInstance.get();
        }

        if (!argumentFilterInstance.isUnsatisfied())
        {
            argumentFilter = argumentFilterInstance.get();
        }

        MessageContext result = defaultMessageContext.config()
                .use()
                .localeResolver(new JsfAwareLocaleResolver())
                .messageResolver(new JsfAwareApplicationMessagesMessageResolver())
                .messageInterpolator(new FacesMessageInterpolator(elProvider, argumentFilter))
                .addMessageHandler(new JsfAwareMessageHandler())
                .create();

        if (messageFactory != null)
        {
            return DefaultMessageContext.create(result.config(), messageFactory);
        }

        return result;
    }

    /**
     * Creates a map for using the el-map-trick.
     * @param messageContext jsf specific {@link MessageContext}
     * @return helper map for el-expressions
     */
    @Produces
    @Dependent
    @Named(MESSAGE_CONTEXT)
    public MessageHelperMap createContextForEL(final @Jsf MessageContext messageContext)
    {
        return new MessageHelperMap(messageContext);
    }
}
