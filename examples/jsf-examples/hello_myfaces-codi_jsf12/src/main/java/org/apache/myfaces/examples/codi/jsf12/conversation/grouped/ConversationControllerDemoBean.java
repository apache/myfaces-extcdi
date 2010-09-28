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
package org.apache.myfaces.examples.codi.jsf12.conversation.grouped;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.CloseConversationGroup;

import javax.enterprise.inject.Model;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Model
public class ConversationControllerDemoBean implements Serializable
{
    private static final long serialVersionUID = -233781580529667341L;
    private Integer value;

    /**
     * Alternative to {@link ConversationDemoBean1#endGroup1()} for simple use-cases
     * @return outcome
     */
    @CloseConversationGroup(group = ConversationGroup1.class)
    public String closeGroup1()
    {
        return null;
    }

    @CloseConversationGroup(group = ConversationGroup1.class, on = NullPointerException.class)
    public String closeGroup1OnException()
    {
        //forced exception - will be cached by the interceptor
        return this.value.toString();
    }
}
