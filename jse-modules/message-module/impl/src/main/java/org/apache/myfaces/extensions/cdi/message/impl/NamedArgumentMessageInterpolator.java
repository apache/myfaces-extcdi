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

import org.apache.myfaces.extensions.cdi.message.api.Localizable;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.MessageInterpolator;
import org.apache.myfaces.extensions.cdi.message.api.NamedArgument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class NamedArgumentMessageInterpolator implements MessageInterpolator
{
    private static final long serialVersionUID = -8511553781756269753L;

    public String interpolate(MessageContext messageContext, String messageText, Serializable... arguments)
    {
        NamedArgument[] namedArguments = extractNamedArguments(arguments);

        String name;
        Serializable value;
        for(NamedArgument namedArgument : namedArguments)
        {
            name = "{" + namedArgument.getName() + "}";

            if(messageText.contains(name))
            {
                value = namedArgument.getValue();

                if(value instanceof Localizable)
                {
                    value = ((Localizable)value).toString(messageContext);
                }
                else if(value == null)
                {
                    value = "null";
                }

                messageText = messageText.replace(name, value.toString());
            }
        }
        return messageText;
    }

    private NamedArgument[] extractNamedArguments(Serializable[] arguments)
    {
        List<NamedArgument> result = new ArrayList<NamedArgument>();

        for (Serializable argument : arguments)
        {
            if (argument instanceof NamedArgument)
            {
                result.add((NamedArgument)argument);
            }
        }

        return result.toArray(new NamedArgument[result.size()]);
    }
}
