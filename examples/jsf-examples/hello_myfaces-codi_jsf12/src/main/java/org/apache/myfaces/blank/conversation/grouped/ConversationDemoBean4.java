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
package org.apache.myfaces.blank.conversation.grouped;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;
import org.apache.myfaces.blank.conversation.grouped.qualifier.Qualifier1;
import org.apache.myfaces.blank.conversation.grouped.qualifier.Qualifier2;
import org.apache.myfaces.blank.conversation.grouped.qualifier.Qualifier3;

import javax.inject.Named;
import javax.inject.Inject;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gerhard Petracek
 */
@Named
@ViewAccessScoped
public class ConversationDemoBean4 implements Serializable
{
    private String value = "Hello view access scoped! ";
    private Date createdAt;
    private static final long serialVersionUID = -4238520498463300564L;

    @Inject
    @Qualifier1
    @ConversationGroup(ConversationGroup1.class)
    private ConversationDemoBeanWithQualifier bean1;

    @Inject
    @Qualifier3
    @ConversationGroup(ConversationGroup1.class)
    private ConversationDemoBeanWithQualifier bean2;

    @Inject
    @Qualifier3
    @ConversationGroup(ConversationGroup1.class)
    private MixedConversationDemoBean bean3;

    @Inject
    @Qualifier1
    private MixedConversationDemoBean mixedBean1;

    @Inject
    @Qualifier2
    private MixedConversationDemoBean mixedBean2;

    @PostConstruct
    public void init()
    {
        this.createdAt = new Date();
        this.bean1.getValue();
        this.bean2.getValue();
    }

    public String getValue()
    {
        return value +
                createdAt.toLocaleString() +
                " injected beans: " +
                this.bean1.getValue() + " " +
                this.bean2.getValue() + " " +
                this.bean3.getValue() + " " +
                this.mixedBean1.getValue() + " " +
                this.mixedBean2.getValue();
    }
}