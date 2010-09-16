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
package org.apache.myfaces.examples.codi.jsf12.message.client;

import org.apache.myfaces.examples.codi.jsf12.message.client.model.Client;
import org.apache.myfaces.examples.codi.jsf12.message.client.qualifier.ClientQualifier;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;

import javax.enterprise.inject.Produces;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * @author Gerhard Petracek
 */
public class ClientAwareMessageResolverProducer
{
    @Produces
    @RequestScoped
    @ClientQualifier
    public MessageContext createClientAwareContext(@Jsf MessageContext messageContext,
                                                   Client client)
    {
        MessageResolver clientAwareMessageResolver = createMessageResolverForClient(client.getId());

        return messageContext.config().use().messageResolver(clientAwareMessageResolver).create();
    }

    private MessageResolver createMessageResolverForClient(final String currentClientId)
    {
        return new MessageResolver()
        {
            private static final long serialVersionUID = -4009930342736825281L;

            public String getMessage(MessageContext messageContext,
                                     String messageDescriptor,
                                     Map<Class, MessagePayload> messagePayload)
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();

                try
                {
                    return facesContext.getApplication()
                            .getResourceBundle(facesContext, currentClientId).getString(messageDescriptor);
                }
                catch (MissingResourceException e)
                {
                    return "???" + messageDescriptor + "???";
                }
            }
        };
    }
}
