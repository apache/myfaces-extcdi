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
package org.apache.myfaces.extensions.cdi.message.api;

import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayloadKey;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractMessage implements Message, MessageContextConfigAware<Message>
{
    protected String messageDescriptor;
    protected Set<NamedArgument> namedArguments = new HashSet<NamedArgument>();
    protected List<Serializable> arguments = new ArrayList<Serializable>();
    protected Map<Class, MessagePayload> messagePayload = new HashMap<Class, MessagePayload>();

    //optional
    @Deprecated
    //currently not used - would be useful for messages which are aware of the original context they were created in
    private MessageContextConfig messageContextConfig;

    public AbstractMessage(Message message)
    {
        this(message.getDescriptor(), message.getArguments());
        this.messagePayload = message.getPayload();
    }

    public AbstractMessage(String messageDescriptor, Serializable... arguments)
    {
        this.messageDescriptor = messageDescriptor;

        for (Serializable argument : arguments)
        {
            if (argument instanceof MessagePayload)
            {
                //TODO log warning
                //noinspection unchecked
                addPayload((MessagePayload) argument);
            }
            else
            {
                addArgument(argument);
            }
        }
        cleanup();
    }

    public AbstractMessage(String messageDescriptor, Set<NamedArgument> namedArguments)
    {
        this.messageDescriptor = messageDescriptor;
        this.namedArguments = namedArguments;
        this.arguments = null;
    }

    public Message addArgument(Serializable... arguments)
    {
        for (Serializable currentArgument : arguments)
        {
            Serializable argument = currentArgument;

            if(argument == null)
            {
                argument = "null";
            }

            if(isHiddenArgument(argument))
            {
                addHiddenArgument(argument);
            }
            else
            {
                addArgumentToMessage(argument);
            }
        }
        return this;
    }

    private void addArgumentToMessage(Object argument)
    {
        Serializable result;

        if(argument instanceof Serializable)
        {
            result = (Serializable)argument;
        }
        else
        {
            result = processNoneSerializableArgument(argument);
        }
        checkArgument(result);
        if (argument instanceof NamedArgument)
        {
            addNamedArgument((NamedArgument) argument);
        }
        else
        {
            addNumberedArgument(result);
        }
    }

    /**
     * if the argument isn't serializable we aren't allowed to store the instance itself
     * -> we have to store the string instead
     * (if we are aware of the context and the parameter implements {@link Localizable} we use the localized value)
     *
     * @param argument current argument
     * @return the string value of the argument
     */
    private String processNoneSerializableArgument(Object argument)
    {
        if(argument instanceof Localizable && this.getMessageContextConfig() != null)
        {
            return ((Localizable)argument).toString(this.getMessageContextConfig().use().create());
        }
        else
        {
            return argument != null ? argument.toString() : "null";
        }
    }

    private boolean isHiddenArgument(Serializable argument)
    {
        return argument != null && argument.getClass().isArray();
    }

    private void addHiddenArgument(Serializable argument)
    {
        for(Object current : ((Object[])argument))
        {
            addArgumentToMessage(current);
        }
    }

    private void checkArgument(Serializable argument)
    {
        if (argument instanceof NamedArgument)
        {
            //noinspection SuspiciousMethodCalls
            if (this.namedArguments != null)
            {
                for (NamedArgument namedArgument : this.namedArguments)
                {
                    if (namedArgument.getName().equals(((NamedArgument) argument).getName()))
                    {
                        throw new UnsupportedOperationException(
                                "it isn't allowed to override arguments - argument name: " +
                                        ((NamedArgument) argument).getName());
                    }
                }
            }
        }
    }

    protected void addNamedArgument(NamedArgument namedArgument)
    {
        if (this.namedArguments == null)
        {
            this.namedArguments = new HashSet<NamedArgument>();
        }

        this.namedArguments.add(namedArgument);
    }

    protected void addNumberedArgument(Serializable argument)
    {
        if (this.arguments == null)
        {
            this.arguments = new ArrayList<Serializable>();
        }

        this.arguments.add(argument);
    }

    public String getDescriptor()
    {
        return this.messageDescriptor;
    }

    public Serializable[] getArguments()
    {
        if (this.namedArguments == null && this.arguments == null)
        {
            return new Serializable[]{};
        }

        if (this.namedArguments == null)
        {
            return this.arguments.toArray(new Serializable[this.arguments.size()]);
        }
        else if (this.arguments == null)
        {
            return this.namedArguments.toArray(new Serializable[this.namedArguments.size()]);
        }

        List<Serializable> mergedArguments =
                new ArrayList<Serializable>(this.namedArguments.size() + this.arguments.size());

        mergedArguments.addAll(this.namedArguments);
        mergedArguments.addAll(this.arguments);
        return mergedArguments.toArray(new Serializable[mergedArguments.size()]);
    }

    public void addPayload(MessagePayload payload)
    {
        Class key = payload.getClass();

        if (payload.getClass().isAnnotationPresent(MessagePayloadKey.class))
        {
            key = payload.getClass().getAnnotation(MessagePayloadKey.class).value();
        }
        addPayload(key, payload);
    }

    public Map<Class, MessagePayload> getPayload()
    {
        return Collections.unmodifiableMap(this.messagePayload);
    }

    public Message addPayload(Class key, MessagePayload payload)
    {
        if (this.messagePayload.containsKey(key))
        {
            throw new UnsupportedOperationException("it isn't allowed to override payload - key: " + key.getName());
        }
        this.messagePayload.put(key, payload);
        return this;
    }

    public Message setMessageContextConfig(MessageContextConfig messageContextConfig)
    {
        this.messageContextConfig = messageContextConfig;
        return this;
    }

    public MessageContextConfig getMessageContextConfig()
    {
        return this.messageContextConfig;
    }

    private void cleanup()
    {
        if (this.namedArguments != null && this.namedArguments.isEmpty())
        {
            this.namedArguments = null;
        }

        if (this.arguments != null && this.arguments.isEmpty())
        {
            this.arguments = null;
        }
    }

    /*
    * generated
    */

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof AbstractMessage))
        {
            return false;
        }

        AbstractMessage that = (AbstractMessage) o;

        if (messagePayload != null ? !messagePayload.equals(that.messagePayload) : that.messagePayload != null)
        {
            return false;
        }
        if (!messageDescriptor.equals(that.messageDescriptor))
        {
            return false;
        }
        if (namedArguments != null ? !namedArguments.equals(that.namedArguments) : that.namedArguments != null)
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = messageDescriptor.hashCode();
        result = 31 * result + (namedArguments != null ? namedArguments.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (messagePayload != null ? messagePayload.hashCode() : 0);
        return result;
    }
}
