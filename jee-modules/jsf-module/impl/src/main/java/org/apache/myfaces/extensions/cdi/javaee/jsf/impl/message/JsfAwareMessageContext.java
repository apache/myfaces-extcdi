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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.message;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfig;
import org.apache.myfaces.extensions.cdi.message.api.MessageFactory;
import org.apache.myfaces.extensions.cdi.message.api.LocaleResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageFilter;
import org.apache.myfaces.extensions.cdi.message.api.MessageBuilder;
import org.apache.myfaces.extensions.cdi.message.impl.DefaultMessageContext;
import org.apache.myfaces.extensions.cdi.message.impl.spi.ELProvider;

import javax.inject.Named;
import javax.inject.Inject;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import java.util.Locale;
import java.util.Set;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
@Jsf
@Named("messageContext")
@Dependent
public class JsfAwareMessageContext implements MessageContext
{
    private static final long serialVersionUID = 3497205985879944059L;

    @Inject
    private Instance<MessageFactory> messageFactoryInstance;

    @Inject
    private Instance<ELProvider> elProviderInstance;

    private MessageContext messageContext;

    @PostConstruct
    public void init()
    {
        MessageFactory messageFactory = null;
        ELProvider elProvider = null;

        if (!this.messageFactoryInstance.isUnsatisfied())
        {
            messageFactory = this.messageFactoryInstance.get();
        }

        if(!this.elProviderInstance.isUnsatisfied())
        {
            elProvider = this.elProviderInstance.get();
        }

        if (messageFactory != null)
        {
            this.messageContext = new DefaultMessageContext(messageFactory);
        }
        else
        {
            this.messageContext = new DefaultMessageContext();
        }

        this.messageContext.config().change()
                .localeResolver(createJsfAwareLocaleResolver())
                .messageResolver(new JsfAwareApplicationMessagesMessageResolver())
                .messageInterpolator(new FacesMessageInterpolator(elProvider))
                .addMessageHandler(new JsfAwareMessageHandler());
    }

    private LocaleResolver createJsfAwareLocaleResolver()
    {
        return new LocaleResolver()
        {
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

    public MessageBuilder message()
    {
        return this.messageContext.message();
    }

    public MessageContextConfig config()
    {
        return this.messageContext.config();
    }

    public <T extends MessageContext> T typed(Class<T> contextType)
    {
        return this.messageContext.typed(contextType);
    }

    public MessageContext cloneContext()
    {
        return this.messageContext.cloneContext();
    }

    public void addMessage(Message message)
    {
        this.messageContext.addMessage(message);
    }

    public Locale getLocale()
    {
        return this.messageContext.getLocale();
    }

    public void addMessage(MessageContext messageContext, Message message)
    {
        this.messageContext.addMessage(messageContext, message);
    }

    public void addMessageFilter(MessageFilter... messageFilters)
    {
        this.messageContext.addMessageFilter(messageFilters);
    }

    public Set<MessageFilter> getMessageFilters()
    {
        return this.messageContext.getMessageFilters();
    }

    public void removeMessage(Message message)
    {
        this.messageContext.removeMessage(message);
    }

    public void removeAllMessages()
    {
        this.messageContext.removeAllMessages();
    }

    public List<Message> getMessages()
    {
        return this.messageContext.getMessages();
    }
}
