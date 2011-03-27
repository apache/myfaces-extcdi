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
import org.apache.myfaces.extensions.cdi.message.api.MessageContextConfigAware;
import org.apache.myfaces.extensions.cdi.message.api.MessageResolver;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;
import org.apache.myfaces.extensions.cdi.message.api.Localizable;
import org.apache.myfaces.extensions.cdi.message.api.MessageBuilder;
import org.apache.myfaces.extensions.cdi.message.api.MessageFactory;
import org.apache.myfaces.extensions.cdi.message.api.Formatter;
import org.apache.myfaces.extensions.cdi.message.api.Default;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayloadKey;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;
import org.apache.myfaces.extensions.cdi.message.api.payload.ArgumentDescriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
class DefaultMessageBuilder implements MessageBuilder
{
    private static final long serialVersionUID = 892218539314030675L;

    //ArrayList due to Serializable warning in checkstyle rules
    private ArrayList<Serializable> argumentList;

    //HashSet due to Serializable warning in checkstyle rules
    private HashSet<NamedArgument> namedArguments;
    
    private MessageContext messageContext;
    //HashMap due to Serializable warning in checkstyle rules
    private HashMap<Class, MessagePayload> messagePayload;
    private String messageDescriptor;

    private MessageFactory messageFactory;

    protected DefaultMessageBuilder()
    {
    }

    /**
     * Constructor for creating the builder which uses the given {@link MessageContext} and {@link MessageFactory}
     * @param messageContext current message-context
     * @param messageFactory current message-factory
     */
    public DefaultMessageBuilder(MessageContext messageContext, MessageFactory messageFactory)
    {
        reset();
        this.messageContext = new UnmodifiableMessageContext(messageContext.cloneContext());

        if(messageFactory != null)
        {
            this.messageFactory = messageFactory;
        }
        else
        {
            this.messageFactory = new DefaultMessageFactory();
        }
    }

    /**
     * {@inheritDoc}
     */
    public MessageBuilder payload(MessagePayload... messagePayload)
    {
        Class key;

        for (MessagePayload payload : messagePayload)
        {
            key = payload.getClass();
            if (payload.getClass().isAnnotationPresent(MessagePayloadKey.class))
            {
                key = payload.getClass().getAnnotation(MessagePayloadKey.class).value();
            }
            this.messagePayload.put(key, payload);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MessageBuilder text(String messageDescriptor)
    {
        this.messageDescriptor = messageDescriptor;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MessageBuilder argument(Serializable... arguments)
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

    /**
     * {@inheritDoc}
     */
    public MessageBuilder namedArgument(String name, Serializable value)
    {
        this.namedArguments.add(new DefaultNamedArgument(name, value));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Message add()
    {
        Message result = create();
        this.messageContext.addMessage(this.messageContext, result);
        return result;
    }

    private Message buildMessage()
    {
        if (this.messageDescriptor == null)
        {
            throw new IllegalStateException("messageDescriptor is missing");
        }

        Message result = createNewMessage();

        if(result instanceof MessageContextConfigAware && this.messageContext != null)
        {
            ((MessageContextConfigAware)result).setMessageContextConfig(this.messageContext.config());
        }

        addArguments(result);
        addPayload(result);

        return result;
    }

    private Message createNewMessage()
    {
        if(this.messageFactory != null)
        {
            return this.messageFactory.create(this.messageDescriptor, getMessageSeverity());
        }
        return new DefaultMessage(this.messageDescriptor, getMessageSeverity());
    }

    private MessagePayload getMessageSeverity()
    {
        MessagePayload severity = this.messagePayload.get(MessageSeverity.class);

        if (severity == null)
        {
            severity = MessageSeverity.INFO;
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
        for (Map.Entry<Class, MessagePayload> entry : this.messagePayload.entrySet())
        {
            if (!MessageSeverity.class.equals(entry.getKey()))
            {
                result.addPayload(entry.getKey(), entry.getValue());
            }
        }
    }

    protected void reset()
    {
        this.messageDescriptor = null;
        this.messagePayload = new HashMap<Class, MessagePayload>();
        this.argumentList = new ArrayList<Serializable>();
        this.namedArguments = new HashSet<NamedArgument>();
    }

    /**
     * {@inheritDoc}
     */
    public Message create()
    {
        Message result = buildMessage();
        reset();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String toText()
    {
        Message baseMessage = buildMessage();

        return getMessageText(baseMessage);
    }

    private String getMessageText(Message baseMessage)
    {
        String message = baseMessage.getDescriptor();

        MessageResolver messageResolver = this.messageContext.config().getMessageResolver();
        if (messageResolver != null)
        {
            message = resolveMessage(messageResolver, baseMessage);
        }

        MessageInterpolator messageInterpolator = this.messageContext.config().getMessageInterpolator();

        if (messageInterpolator != null && message != null)
        {
            return checkedResult(
                    interpolateMessage(messageInterpolator,
                                       message,
                                       restoreArguments(baseMessage, this.messageContext)),
                    baseMessage);
        }

        return checkedResult(message, baseMessage);
    }

    private String checkedResult(String result, Message baseMessage)
    {
        if (result == null || isKey(baseMessage.getDescriptor()) || isKeyWithoutMarkers(result, baseMessage))
        {
            String oldTemplate = extractTemplate(baseMessage.getDescriptor()); //minor performance tweak for inline-msg

            if (result == null || result.equals(oldTemplate))
            {
                return MessageResolver.MISSING_RESOURCE_MARKER + oldTemplate +
                       MessageResolver.MISSING_RESOURCE_MARKER + getArguments(baseMessage);
            }
        }
        return result;
    }

    private boolean isKeyWithoutMarkers(String result, Message baseMessage)
    {
        return (!result.contains(" ") && result.endsWith(baseMessage.getDescriptor()));
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

            if (formatter != null && !isDefaultFormatter(formatter.getClass()))
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

    //see javadoc of {@link ArgumentDescriptor} for more details
    private Serializable[] restoreArguments(Message baseMessage, MessageContext messageContext)
    {
        if (messageContext == null || baseMessage.getArguments() == null)
        {
            return baseMessage.getArguments();
        }
        List<Serializable> result = new ArrayList<Serializable>(baseMessage.getArguments().length);

        for (Serializable argument : baseMessage.getArguments())
        {
            if (isNumberedArgumentValueALazyArgument(argument))
            {
                resolveAndProcessLazyNumberedArgument(messageContext, result, (String)argument);
            }
            else if (argument instanceof NamedArgument && isNamedArgumentValueALazyArgument(((NamedArgument) argument)))
            {
                resolveAndProcessLazyNamedArgument(messageContext, result, (NamedArgument) argument);
            }
            else
            {
                result.add(argument);
            }
        }
        return result.toArray(new Serializable[result.size()]);
    }

    private boolean isNumberedArgumentValueALazyArgument(Serializable argument)
    {
        return argument instanceof String && isKey((String) argument);
    }

    private boolean isNamedArgumentValueALazyArgument(NamedArgument namedArgument)
    {
        return namedArgument.getValue() instanceof String && isKey((String) namedArgument.getValue());
    }

    private void resolveAndProcessLazyNumberedArgument(
            MessageContext messageContext, List<Serializable> result, String argument)
    {
        String resolvedArgumentValue = resolveValueOfArgumentDescriptor(messageContext, argument);

        result.add(resolvedArgumentValue);
    }

    private void resolveAndProcessLazyNamedArgument(
            MessageContext messageContext, List<Serializable> result, NamedArgument argument)
    {
        String namedArgumentValue = (String) argument.getValue();

        String resolvedNamedArgumentValue = resolveValueOfArgumentDescriptor(messageContext, namedArgumentValue);


        result.add(new DefaultNamedArgument(argument.getName(), resolvedNamedArgumentValue));
    }

    private String resolveValueOfArgumentDescriptor(MessageContext messageContext, String argumentAsKey)
    {
        return messageContext.message()
                .text(argumentAsKey)
                .payload(ArgumentDescriptor.PAYLOAD)
                .toText();
    }

    private boolean isDefaultFormatter(Class<? extends Formatter> formatterClass)
    {
        return formatterClass.isAnnotationPresent(Default.class);
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
        return messageResolver.getMessage(this.messageContext, baseMessage.getDescriptor(), baseMessage.getPayload());
    }

    private String interpolateMessage(MessageInterpolator messageInterpolator,
                                      String messageDescriptor, Serializable... arguments)
    {
        return messageInterpolator.interpolate(this.messageContext, getEscapedTemplate(messageDescriptor), arguments);
    }

    private String getEscapedTemplate(String messageDescriptor)
    {
        //TODO
        if (messageDescriptor.startsWith("\\{"))
        {
            return messageDescriptor.substring(1);
        }
        return messageDescriptor;
    }

    protected MessageContext getMessageContext()
    {
        return this.messageContext;
    }

    protected void setMessageFactory(MessageFactory messageFactory)
    {
        this.messageFactory = messageFactory;
    }
}
