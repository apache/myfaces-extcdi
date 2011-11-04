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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.examples.codi.jsf12.conversation.grouped.qualifier.Qualifier1;
import org.apache.myfaces.examples.codi.jsf12.conversation.grouped.qualifier.Qualifier3;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.io.Serializable;

/**
 * Bean with bean-producer
 */
@Dependent
public class ConversationDemoBeanWithQualifier implements Serializable
{
    private String value = "";
    private static final long serialVersionUID = -4238520498463300564L;

    protected ConversationDemoBeanWithQualifier()
    {
    }

    private ConversationDemoBeanWithQualifier(String value)
    {
        this.value = value;
    }

    @Produces
    @Qualifier1
    @ConversationScoped
    @ConversationGroup(ConversationGroup1.class)
    public ConversationDemoBeanWithQualifier createConversationDemoBean1WithQ1()
    {
        return new ConversationDemoBeanWithQualifier("Q1");
    }

    @Produces
    @Qualifier3
    @ConversationScoped
    @ConversationGroup(ConversationGroup1.class)
    public ConversationDemoBeanWithQualifier createConversationDemoBean1WithQ2()
    {
        return new ConversationDemoBeanWithQualifier("Q2");
    }

    public String getValue()
    {
        return value;
    }
}