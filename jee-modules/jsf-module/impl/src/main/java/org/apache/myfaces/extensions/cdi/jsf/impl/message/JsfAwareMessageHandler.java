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

import org.apache.myfaces.extensions.cdi.message.api.AbstractMessageHandler;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageWithSeverity;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
class JsfAwareMessageHandler extends AbstractMessageHandler
{
    private static final long serialVersionUID = -7193428173462936712L;

    @Override
    protected void processMessage(MessageContext messageContext, Message message)
    {
        MessagePayload severity = MessageSeverity.INFO;

        if (message instanceof MessageWithSeverity)
        {
            severity = ((MessageWithSeverity) message).getSeverity();
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(convertSeverity(severity),
                        message.toString(messageContext),
                        message.toString(messageContext)));
    }

    private FacesMessage.Severity convertSeverity(MessagePayload payload)
    {
        if (MessageSeverity.INFO.equals(payload))
        {
            return FacesMessage.SEVERITY_INFO;
        }
        if (MessageSeverity.WARN.equals(payload))
        {
            return FacesMessage.SEVERITY_WARN;
        }
        if (MessageSeverity.ERROR.equals(payload))
        {
            return FacesMessage.SEVERITY_ERROR;
        }
        if (MessageSeverity.FATAL.equals(payload))
        {
            return FacesMessage.SEVERITY_FATAL;
        }
        throw new IllegalArgumentException(payload.getClass().getName());
    }

    public void removeMessage(Message message)
    {
        throw new UnsupportedOperationException("not implemented");
    }

    public void removeAllMessages()
    {
        throw new UnsupportedOperationException("not implemented");
    }

    public List<Message> getMessages()
    {
        throw new UnsupportedOperationException("not implemented");
    }
}