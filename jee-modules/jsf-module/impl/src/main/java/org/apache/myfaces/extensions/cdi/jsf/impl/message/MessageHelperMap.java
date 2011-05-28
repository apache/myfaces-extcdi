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

import org.apache.myfaces.extensions.cdi.core.impl.util.UnmodifiableMap;
import org.apache.myfaces.extensions.cdi.message.api.Localizable;
import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import javax.enterprise.inject.Typed;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Examples for the supported syntax:    <br/>
 * #{messageContext['msgKey'].toText}    <br/>
 * #{messageContext['msgKey'].toMessage}    <br/>
 *
 * and
 * <br/>
 * for numbered arguments:<br/>
 * #{messageContext['msgKey'][bean.value].toText}
 * <br/>
 * for named arguments:<br/>
 * #{messageContext['msgKey']['argKey:#{bean.value}'].toText}
 *
 * @author Gerhard Petracek
 */
@Typed()
class MessageHelperMap extends UnmodifiableMap<String, Object>
{
    private static final long serialVersionUID = 2530702568624997067L;

    private MessageContext messageContext;

    private List<Serializable> numberedArgument = new ArrayList<Serializable>();

    private Map<String, Serializable> namedArgument = new HashMap<String, Serializable>();

    private String messageKey;

    MessageHelperMap(MessageContext messageContext)
    {
        this.messageContext = messageContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object arg)
    {
        Serializable argument;

        if(arg instanceof Serializable)
        {
            argument = (Serializable)arg;
        }
        else if(arg instanceof Localizable)
        {
            argument = ((Localizable)arg).toString(this.messageContext);
        }
        else
        {
            argument = arg.toString();
        }

        if("toText".equals(argument))
        {
            return  getToText();
        }

        if("toMessage".equals(argument))
        {
            return  getToMessage();
        }

        if(this.messageKey == null)
        {
            this.messageKey = "{" + argument + "}"; //always use a key - a hardcoded msg wouldn't be useful in this case

            return this;
        }

        String argumentKey = null;
        Object argumentValue = null;
        String stringArgument;
        if(argument instanceof String && ((String)argument).contains(":"))
        {
            stringArgument = ((String)argument);
            String[] keyValuePair = stringArgument.split(":");

            if(keyValuePair.length > 1)
            {
                String key = keyValuePair[0];

                if(!key.contains(" "))
                {
                    argumentKey = key;
                }
                argumentValue = stringArgument.substring(stringArgument.indexOf(":") + 1);
                String expression = argumentValue.toString().trim();
                if(expression.startsWith("#{") && expression.endsWith("}"))
                {
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    argumentValue = facesContext.getApplication()
                            .evaluateExpressionGet(facesContext, expression, Object.class);
                }
            }
        }

        if(argumentValue == null)
        {
            argumentValue = argument;
        }

        Serializable value;

        if(argumentValue instanceof Serializable)
        {
            value = (Serializable)argumentValue;
        }
        else if(argumentValue instanceof Localizable)
        {
            value = ((Localizable)argumentValue).toString(this.messageContext);
        }
        else if(argumentValue != null)
        {
            value = argumentValue.toString();
        }
        else
        {
            value = null;
        }

        if(argumentKey == null)
        {
            this.numberedArgument.add(value);
        }
        else
        {
            this.namedArgument.put(argumentKey, value);
        }
        return this;
    }

    /**
     * Creates the message for the current state.
     * @return message for the current state
     */
    public Message getToMessage()
    {
        Message message = this.messageContext.message().text(this.messageKey).create();

        for(Serializable argument : this.numberedArgument)
        {
            message.addArgument(argument);
        }

        for(Map.Entry<String, Serializable> entry : this.namedArgument.entrySet())
        {
            message.addArgument(new SimpleNamedArgument(entry.getKey(), entry.getValue()));
        }
        return message;
    }

    /**
     * Creates the message and returns the text of it.
     * @return text of the message
     */
    public String getToText()
    {
        Message message = getToMessage();

        return message.toString(this.messageContext);
    }
}
