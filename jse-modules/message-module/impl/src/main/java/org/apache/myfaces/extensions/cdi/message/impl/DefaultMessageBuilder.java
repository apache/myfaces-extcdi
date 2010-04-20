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
package org.apache.myfaces.extensions.cdi.message.impl;

import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageContextAware;
import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfigAware;
import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.Formatter;
import org.apache.myfaces.extensions.cdi.message.api.Localizable;
import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayloadKey;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
class DefaultMessageBuilder implements MessageContext.MessageBuilder, Serializable
{
    private static final long serialVersionUID = 892218539314030675L;

    private List<Serializable> argumentList;

    private Set<NamedArgument> namedArguments;
    private MessageContext messageContext;
    private Map<Class, Class<? extends MessagePayload>> messagePayload;
    private String messageTemplate;

    public DefaultMessageBuilder(MessageContext messageContext)
    {
        reset();
        this.messageContext = new UnmodifiableMessageContext(messageContext.cloneContext());
    }

    public MessageContext.MessageBuilder payload(Class<? extends MessagePayload>... messagePayload)
    {
        Class key;

        for (Class<? extends MessagePayload> payload : messagePayload)
        {
            key = payload;
            if (payload.isAnnotationPresent(MessagePayloadKey.class))
            {
                key = payload.getAnnotation(MessagePayloadKey.class).value();
            }
            this.messagePayload.put(key, payload);
        }
        return this;
    }

    public MessageContext.MessageBuilder text(String messageTemplate)
    {
        this.messageTemplate = messageTemplate;
        return this;
    }

    public MessageContext.MessageBuilder argument(Serializable... arguments)
    {
        for (Serializable argument : arguments)
        {
            if (argument instanceof NamedArgument)
            {
                this.namedArguments.add((NamedArgument) argument);
            }
            else
            {
                this.argumentList.add(argument);
            }
        }

        return this;
    }

    public MessageContext.MessageBuilder namedArgument(String name, Serializable value)
    {
        this.namedArguments.add(new DefaultNamedArgument(name, value));
        return this;
    }

    public Message add()
    {
        Message result = create();
        this.messageContext.addMessage(this.messageContext, result);
        return result;
    }

    private Message buildMessage()
    {
        if (this.messageTemplate == null)
        {
            throw new IllegalStateException("messageTemplate is missing");
        }

        Class<? extends MessagePayload> severity = getMessageSeverity();

        Message result = new DefaultMessage(this.messageTemplate, severity);

        if(result instanceof MessageContextConfigAware)
        {
            ((MessageContextConfigAware)result).setMessageContextConfig(this.messageContext.config());
        }

        addArguments(result);
        addPayload(result);

        return result;
    }

    private Class<? extends MessagePayload> getMessageSeverity()
    {
        Class<? extends MessagePayload> severity = this.messagePayload.get(MessageSeverity.class);

        if (severity == null)
        {
            severity = MessageSeverity.Info.class;
        }
        return severity;
    }

    private void addArguments(Message result)
    {
        if (!this.argumentList.isEmpty())
        {
            result.addArgument(this.argumentList.toArray(new Serializable[this.argumentList.size()]));
        }

        if (!this.namedArguments.isEmpty())
        {
            result.addArgument(this.namedArguments.toArray(new Serializable[this.namedArguments.size()]));
        }
    }

    private void addPayload(Message result)
    {
        for (Map.Entry<Class, Class<? extends MessagePayload>> entry : this.messagePayload.entrySet())
        {
            if (!MessageSeverity.class.equals(entry.getKey()))
            {
                result.addPayload(entry.getKey(), entry.getValue());
            }
        }
    }

    private void reset()
    {
        this.messageTemplate = null;
        this.messagePayload = new HashMap<Class, Class<? extends MessagePayload>>();
        this.argumentList = new ArrayList<Serializable>();
        this.namedArguments = new HashSet<NamedArgument>();
    }

    public Message create()
    {
        Message result = buildMessage();
        reset();
        return result;
    }

    public String toText()
    {
        Message baseMessage = buildMessage();

        return getMessageText(baseMessage);
    }

    public String toText(Message message)
    {
        //it isn't required to try to restore the message-context of the message
        //to use the original context (config) just call message.toString(); instead of this method
        return getMessageText(message);
    }

    private String getMessageText(Message baseMessage)
    {
        String message = baseMessage.getTemplate();

        MessageResolver messageResolver = this.messageContext.config().getMessageResolver();
        if (messageResolver != null)
        {
            synchronized (this)
            {
                message = resolveMessage(messageResolver, baseMessage);
            }
        }

        MessageInterpolator messageInterpolator = this.messageContext.config().getMessageInterpolator();

        if (messageInterpolator != null && message != null)
        {
            synchronized (this)
            {
                return checkedResult(
                        interpolateMessage(messageInterpolator, message, baseMessage.getArguments()),
                        baseMessage);
            }
        }

        return checkedResult(message, baseMessage);
    }

    private String checkedResult(String result, Message baseMessage)
    {
        if (result == null || isKey(baseMessage.getTemplate()) || (!result.contains(" ") && result.endsWith(baseMessage.getTemplate())))
        {
            String oldTemplate = extractTemplate(baseMessage.getTemplate()); //minor performance tweak for inline-msg

            if (result == null || result.equals(oldTemplate))
            {
                return MessageResolver.MISSING_RESOURCE_MARKER + oldTemplate + MessageResolver.MISSING_RESOURCE_MARKER + getArguments(baseMessage);
            }
        }
        return result;
    }

    private String getArguments(Message message)
    {
        StringBuffer result = new StringBuffer();

        Serializable argument;
        Serializable[] arguments = message.getArguments();
        Formatter formatter;

        if(arguments == null || arguments.length == 0)
        {
            return "";
        }

        for(int i = 0; i < arguments.length; i++)
        {
            if(i == 0)
            {
                result.append(" (");
            }
            else
            {
                result.append(",");
            }

            argument = arguments[i];
            formatter = this.messageContext.config().getFormatterFactory().findFormatter(argument.getClass());

            if (formatter != null && !formatter.isDefault())
            {
                //noinspection unchecked
                result.append(formatter.format(this.messageContext, argument));
            }
            else if(argument instanceof Localizable)
            {
                result.append(((Localizable)argument).toString(this.messageContext));
            }
            else
            {
                //use default formatter (if available)
                if(formatter != null)
                {
                    //noinspection unchecked
                    result.append(formatter.format(this.messageContext, argument));
                }
                else
                {
                    result.append(argument.toString());
                }
            }
        }
        result.append(')');

        return result.toString();
    }

    private String extractTemplate(String template)
    {
        String result = getEscapedTemplate(template);

        if (isKey(result))
        {
            result = extractTemplateKey(result);
        }

        return result;
    }

    private boolean isKey(String key)
    {
        return key.startsWith("{") && key.endsWith("}");
    }

    private String extractTemplateKey(String key)
    {
        return key.substring(1, key.length() - 1);
    }

    private String resolveMessage(MessageResolver messageResolver, Message baseMessage)
    {
        if (messageResolver instanceof MessageContextAware)
        {
            ((MessageContextAware) messageResolver).setMessageContext(this.messageContext);
        }

        try
        {
            return messageResolver
                    .getMessage(baseMessage.getTemplate(), this.messageContext.getLocale(), baseMessage.getPayload());
        }
        finally
        {
            cleanupMessageContext(messageResolver);
        }
    }

    private String interpolateMessage(MessageInterpolator messageInterpolator,
                                      String messageTemplate, Serializable... arguments)
    {
        if (messageInterpolator instanceof MessageContextAware)
        {
            ((MessageContextAware) messageInterpolator).setMessageContext(this.messageContext);
        }

        try
        {
            return messageInterpolator.interpolate(getEscapedTemplate(messageTemplate), arguments);
        }
        finally
        {
            cleanupMessageContext(messageInterpolator);
        }
    }

    private String getEscapedTemplate(String messageTemplate)
    {
        //TODO
        if (messageTemplate.startsWith("\\{"))
        {
            return messageTemplate.substring(1);
        }
        return messageTemplate;
    }

    private void cleanupMessageContext(Object object)
    {
        if (object instanceof MessageContextAware)
        {
            ((MessageContextAware) object).setMessageContext(null);
        }
    }
}
