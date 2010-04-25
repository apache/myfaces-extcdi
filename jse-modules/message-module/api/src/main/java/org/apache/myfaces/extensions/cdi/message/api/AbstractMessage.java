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
    protected String messageTemplate;
    protected Set<NamedArgument> namedArguments = new HashSet<NamedArgument>();
    protected List<Serializable> arguments = new ArrayList<Serializable>();
    protected Map<Class, Class<? extends MessagePayload>> messagePayload =
            new HashMap<Class, Class<? extends MessagePayload>>();

    //optional
    @Deprecated
    //currently not used - would be useful for messages which are aware of the original context they were created in
    private MessageContextConfig messageContextConfig;

    public AbstractMessage(Message message)
    {
        this(message.getTemplate(), message.getArguments());
        this.messagePayload = message.getPayload();
    }

    public AbstractMessage(String messageTemplate, Serializable... arguments)
    {
        this.messageTemplate = messageTemplate;

        for (Serializable argument : arguments)
        {
            if (argument instanceof Class && MessagePayload.class.isAssignableFrom((Class) argument))
            {
                //TODO log warning
                //noinspection unchecked
                addPayload((Class) argument);
            }
            else
            {
                addArgument(argument);
            }
        }
        cleanup();
    }

    public AbstractMessage(String messageTemplate, Set<NamedArgument> namedArguments)
    {
        this.messageTemplate = messageTemplate;
        this.namedArguments = namedArguments;
        this.arguments = null;
    }

    public void addArgument(Serializable... arguments)
    {
        for (Serializable argument : arguments)
        {
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
    }

    private void addArgumentToMessage(Object argument)
    {
        //TODO
        if(argument instanceof Localizable && this.getMessageContextConfig() != null)
        {
            argument = ((Localizable)argument).toString(this.getMessageContextConfig().use().create());
        }

        Serializable result;

        if(argument instanceof Serializable)
        {
            result = (Serializable)argument;
        }
        else
        {
            result = argument != null ? argument.toString() : "null";
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

    public String getTemplate()
    {
        return this.messageTemplate;
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

    public void addPayload(Class<? extends MessagePayload> payload)
    {
        Class key = payload;

        if (payload.isAnnotationPresent(MessagePayloadKey.class))
        {
            key = payload.getAnnotation(MessagePayloadKey.class).value();
        }
        addPayload(key, payload);
    }

    public Map<Class, Class<? extends MessagePayload>> getPayload()
    {
        return Collections.unmodifiableMap(this.messagePayload);
    }

    public void addPayload(Class key, Class<? extends MessagePayload> payload)
    {
        if (this.messagePayload.containsKey(key))
        {
            throw new UnsupportedOperationException("it isn't allowed to override payload - key: " + key.getName());
        }
        this.messagePayload.put(key, payload);
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
        if (!messageTemplate.equals(that.messageTemplate))
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
        int result = messageTemplate.hashCode();
        result = 31 * result + (namedArguments != null ? namedArguments.hashCode() : 0);
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (messagePayload != null ? messagePayload.hashCode() : 0);
        return result;
    }
}
