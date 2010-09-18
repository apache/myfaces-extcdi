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
import org.apache.myfaces.extensions.cdi.message.api.LocaleResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageFactory;
import org.apache.myfaces.extensions.cdi.message.impl.DefaultMessageContext;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ELProvider;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ArgumentFilter;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public class JsfAwareMessageContextProducer
{
    @Produces
    @Dependent
    @Jsf
    @Named(MESSAGE_CONTEXT)
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
                    .localeResolver(createJsfAwareLocaleResolver())
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

    private LocaleResolver createJsfAwareLocaleResolver()
    {
        return new LocaleResolver()
        {
            private static final long serialVersionUID = 5945811297524654438L;

            public Locale getLocale()
            {
                Locale locale = null;
                FacesContext facesContext = FacesContext.getCurrentInstance();
                if (facesContext != null)
                {
                    locale = facesContext.getViewRoot().getLocale();
                }
                return locale != null ? locale : Locale.getDefault();
            }
        };
    }
}
