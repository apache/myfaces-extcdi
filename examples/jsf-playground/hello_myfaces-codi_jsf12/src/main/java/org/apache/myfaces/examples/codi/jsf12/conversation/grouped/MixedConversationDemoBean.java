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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.examples.codi.jsf12.conversation.grouped.qualifier.Qualifier1;
import org.apache.myfaces.examples.codi.jsf12.conversation.grouped.qualifier.Qualifier2;
import org.apache.myfaces.examples.codi.jsf12.conversation.grouped.qualifier.Qualifier3;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gerhard Petracek
 */
@Dependent
public class MixedConversationDemoBean implements Serializable
{
    private static final long serialVersionUID = -4238520498463300564L;

    private String value;

    private Date createdAt;

    protected MixedConversationDemoBean()
    {
    }

    private MixedConversationDemoBean(String value)
    {
        this.value = value;
        this.createdAt = new Date();
    }

    @Produces
    @Qualifier1
    @WindowScoped
    public MixedConversationDemoBean createWindowScopedBean()
    {
        return new MixedConversationDemoBean("Q1@WindowScoped ");
    }

    @Produces
    @Qualifier2
    @ViewAccessScoped
    public MixedConversationDemoBean createViewAccessScopedBean()
    {
        return new MixedConversationDemoBean("Q2@ViewAccessScoped ");
    }

    @Produces
    @Qualifier3
    @ConversationScoped
    @ConversationGroup(ConversationGroup1.class)
    public MixedConversationDemoBean createGroupedConversationScopedBean()
    {
        return new MixedConversationDemoBean("Q2@ConversationScoped/@ConversationGroup ");
    }

    public String getValue()
    {
        return value + createdAt.toLocaleString();
    }
}