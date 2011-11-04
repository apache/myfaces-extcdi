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
package org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier1;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier2;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier3;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier4;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier5;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.io.Serializable;


/**
 * Producer for conversation scoped beans with different qualifiers
 */
@Dependent
public class ConversationBeanWithQualifier implements Serializable
{

    private String _input;
    private Class<?> _qualifier;
    private Class<?> _qualifier2;

    protected ConversationBeanWithQualifier()
    {
        // needed to instantiate class by CDI for Producers
    }

    private ConversationBeanWithQualifier(Class<?> qualifier)
    {
        _qualifier = qualifier;
    }

    private ConversationBeanWithQualifier(Class<?> qualifier1, Class<?> qualifier2)
    {
        _qualifier = qualifier1;
        _qualifier2 = qualifier2;
    }

    public Class<?> getQualifier()
    {
        return _qualifier;
    }

    public Class<?> getQualifier2()
    {
        return _qualifier2;
    }

    public String getInput()
    {
        return _input;
    }

    public void setInput(String input)
    {
        _input = input;
    }

    @Produces
    @ConversationQualifier1
    @ConversationScoped
    public ConversationBeanWithQualifier createConversationBeanWithQualifier1()
    {
        return new ConversationBeanWithQualifier(ConversationQualifier1.class);
    }

    @Produces
    @ConversationQualifier2
    @ConversationScoped
    public ConversationBeanWithQualifier createConversationBeanWithQualifier2DefaultValues()
    {
        return new ConversationBeanWithQualifier(ConversationQualifier2.class);
    }

    @Produces
    @ConversationQualifier2(value = "test value", number = 4711)
    @ConversationScoped
    public ConversationBeanWithQualifier createConversationBeanWithQualifier2NonDefaultValues()
    {
        return new ConversationBeanWithQualifier(ConversationQualifier2.class);
    }

    @Produces
    @ConversationQualifier3(value = "test value", input = "") // input is @Nonbinding
    @ConversationScoped
    public ConversationBeanWithQualifier createConversationBeanWithQualifier3()
    {
        return new ConversationBeanWithQualifier(ConversationQualifier3.class);
    }

    @Produces
    @ConversationQualifier4
    @ConversationQualifier5
    @ConversationScoped
    public ConversationBeanWithQualifier createConversationBeanWithQualifier4And5()
    {
        return new ConversationBeanWithQualifier(
                ConversationQualifier4.class, ConversationQualifier5.class);
    }

    /*@Produces
    @ConversationQualifier4
    @ConversationScoped
    public ConversationBeanWithQualifier createConversationBeanWithQualifier4() //TODO ambiguous?
    {
        return new ConversationBeanWithQualifier(
                ConversationQualifier4.class);
    } */

}
