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
package org.apache.myfaces.examples.codi.jsf12.message.advanced;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import javax.inject.Named;
import javax.inject.Inject;
import javax.enterprise.context.RequestScoped;

/**
 * @author Gerhard Petracek
 */
@Named
@RequestScoped
public class HelloAdvancedMessageBean
{
    @Inject @Jsf
    private MessageContext messageContext;

    private TestBean testBean = new TestBean();

    public void createMessage()
    {
        this.messageContext.message().text("Hello {name}").namedArgument("name", "named arguments").add();
        this.messageContext.message()
                .text("Hello {bean.text}!")
                .namedArgument("bean", this.testBean)
                .payload(MessageSeverity.WARN)
                .add();
    }
}