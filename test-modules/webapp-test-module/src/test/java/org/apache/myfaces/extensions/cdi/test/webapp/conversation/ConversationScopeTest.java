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
package org.apache.myfaces.extensions.cdi.test.webapp.conversation;

import org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean.ConversationBean1;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean.ConversationBeanWithInjectedQualifierBeans;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean.ConversationBeanWithQualifier;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean.ConversationGroupBean1;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean.ConversationGroupBean2;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.group.ConversationGroup1;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier1;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier2;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier3;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier4;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier5;
import org.apache.myfaces.test.webapp.api.annotation.BeansXml;
import org.apache.myfaces.test.webapp.api.annotation.PageBean;
import org.apache.myfaces.test.webapp.api.annotation.Tester;
import org.apache.myfaces.test.webapp.api.annotation.View;
import org.apache.myfaces.test.webapp.api.annotation.WebXml;
import org.apache.myfaces.test.webapp.api.annotation.WebappDependency;
import org.apache.myfaces.test.webapp.api.annotation.WebappResource;
import org.apache.myfaces.test.webapp.api.runner.WebappTestRunner;
import org.apache.myfaces.test.webapp.api.tester.ServerSideCode;
import org.apache.myfaces.test.webapp.api.tester.WebappTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

@View(id = "conversation/test1.xhtml",
      pageBeans = {
              @PageBean(clazz = ConversationBean1.class),
              @PageBean(clazz = ConversationGroupBean1.class),
              @PageBean(clazz = ConversationGroupBean2.class),
              @PageBean(clazz = ConversationGroup1.class),
              @PageBean(clazz = ConversationBeanWithInjectedQualifierBeans.class),
              @PageBean(clazz = ConversationBeanWithQualifier.class),
              @PageBean(clazz = ConversationQualifier1.class),
              @PageBean(clazz = ConversationQualifier2.class)
      }
)

@BeansXml

@WebXml("web.xml")

@WebappResource.List
({
    @WebappResource("conversation/test2.xhtml"),
    @WebappResource("conversation/test3.xhtml")
})

// TODO @WebappClass

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:1.0.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:1.0.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:1.0.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:1.0.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:1.0.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:1.0.4-SNAPSHOT"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-impl:jar:1.1.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-spi:jar:1.1.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-jsf:jar:1.1.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-resource:jar:1.1.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-web:jar:1.1.0"),
    @WebappDependency("javassist:javassist:jar:3.12.0.GA"),
    @WebappDependency("net.sf.scannotation:scannotation:jar:1.0.2")
})

@RunWith(WebappTestRunner.class)

/**
 * Test cases for the CODI ConversationScope.
 */
public class ConversationScopeTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void conversation_Normal_Navigation() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:inputConversationBean1");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:navigateToTest3");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:navigateToTest1");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void conversation_Redirect_Navigation() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:inputConversationBean1");
        tester.click("testForm:redirectToTest2");

        tester.assertThat(new ServerSideCode(){

            public Object execute() throws Exception
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                return facesContext.isPostback();
            }

        }).is(false).after(PhaseId.RENDER_RESPONSE); // ensure redirect
        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:redirectToTest3");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:redirectToTest1");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void conversation_Get_Navigation() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:inputConversationBean1");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:getToTest2");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:getToTest3");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:getToTest1");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void injectedConversation_Close() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:inputConversationBean1");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.input("value2").into("testForm:inputConversationBean1");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{conversationBean1.input}").is("value2").after(PhaseId.RENDER_RESPONSE);

        tester.input("value3").into("test2Form:inputConversationBean1");
        tester.click("test2Form:conversationBean1_closeConversation");

        tester.assertThat("#{conversationBean1.input}").is("value3").after(PhaseId.UPDATE_MODEL_VALUES);
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void injectedConversation_Restart() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:inputConversationBean1");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.input("value2").into("testForm:inputConversationBean1");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{conversationBean1.input}").is("value2").after(PhaseId.RENDER_RESPONSE);

        tester.input("value3").into("test2Form:inputConversationBean1");
        tester.click("test2Form:conversationBean1_restartConversation");

        tester.assertThat("#{conversationBean1.input}").is("value3").after(PhaseId.UPDATE_MODEL_VALUES);
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_ImplicitConversationGroup_Close() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:inputConversationBean1");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.input("value2").into("testForm:inputConversationBean1");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{conversationBean1.input}").is("value2").after(PhaseId.RENDER_RESPONSE);

        tester.input("value3").into("test2Form:inputConversationBean1");
        tester.click("test2Form:conversationBean1_closeOwnConversationGroup");

        tester.assertThat("#{conversationBean1.input}").is("value3").after(PhaseId.UPDATE_MODEL_VALUES);
        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void injectedConversation_ConversationGroup_Close() throws Exception
    {
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:inputConversationGroupBean1");
        tester.input("bean2_value1").into("testForm:inputConversationGroupBean2");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationGroupBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationGroupBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationGroupBean1_closeConversation");

        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void injectedConversation_ConversationGroup_Restart() throws Exception
    {
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:inputConversationGroupBean1");
        tester.input("bean2_value1").into("testForm:inputConversationGroupBean2");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationGroupBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationGroupBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationGroupBean1_restartConversation");

        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void injectedConversation_TwoConversationGroups_GroupClose() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("group1_value1").into("testForm:inputConversationBean1");
        tester.input("group2_bean1_value1").into("testForm:inputConversationGroupBean1");
        tester.input("group2_bean2_value1").into("testForm:inputConversationGroupBean2");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is("group2_bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("group2_bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is("group2_bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("group2_bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationGroupBean1_closeConversation");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBean1_closeConversation");

        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_TwoConversationGroups_CloseOtherGroup() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("group1_value1").into("testForm:inputConversationBean1");
        tester.input("group2_bean1_value1").into("testForm:inputConversationGroupBean1");
        tester.input("group2_bean2_value1").into("testForm:inputConversationGroupBean2");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is("group2_bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("group2_bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is("group2_bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("group2_bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBean1_closeConversationGroup1");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBean1_closeConversation");

        tester.assertThat("#{conversationBean1.input}").is(ConversationBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void conversationBeans_WithQualifiers() throws Exception
    {
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.qualifier}").is(ConversationQualifier1.class).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.qualifier}").is(ConversationQualifier2.class).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.qualifier}").is(ConversationQualifier2.class).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.qualifier}").is(ConversationQualifier3.class).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.qualifier}").is(ConversationQualifier4.class).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.qualifier2}").is(ConversationQualifier5.class).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_Close_Qualifier1_EmptyQualifier() throws Exception
    {
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier2_default_value1").into("testForm:inputConversationBeanWithQualifier2DefaultValues");
        tester.input("qualifier2_non_default_value1").into("testForm:inputConversationBeanWithQualifier2NonDefaultValues");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier1");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_Close_Qualifier2_DefaultValues() throws Exception
    {
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier2_default_value1").into("testForm:inputConversationBeanWithQualifier2DefaultValues");
        tester.input("qualifier2_non_default_value1").into("testForm:inputConversationBeanWithQualifier2NonDefaultValues");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier2DefaultValues");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_Close_Qualifier2_NonDefaultValues() throws Exception
    {
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier2_default_value1").into("testForm:inputConversationBeanWithQualifier2DefaultValues");
        tester.input("qualifier2_non_default_value1").into("testForm:inputConversationBeanWithQualifier2NonDefaultValues");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier2NonDefaultValuesNonMatching");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier2NonDefaultValuesMatching");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_Close_Qualifier3_NonBinding() throws Exception
    {
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier3_value1").into("testForm:inputConversationBeanWithQualifier3");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is("qualifier3_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier3");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is(null).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_Close_Qualifier4And5() throws Exception
    {
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier4And5_value1").into("testForm:inputConversationBeanWithQualifier4And5");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is("qualifier4And5_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier4");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is("qualifier4And5_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier5");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is("qualifier4And5_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBeanWithInjectedQualifierBeans_closeConversationWithQualifier4And5");

        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is(null).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_Close_WithDifferentActiveConversationScopedBeans() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("group1_value1").into("testForm:inputConversationBean1");
        tester.input("group2_bean1_value1").into("testForm:inputConversationGroupBean1");
        tester.input("group2_bean2_value1").into("testForm:inputConversationGroupBean2");
        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier2_default_value1").into("testForm:inputConversationBeanWithQualifier2DefaultValues");
        tester.input("qualifier2_non_default_value1").into("testForm:inputConversationBeanWithQualifier2NonDefaultValues");
        tester.input("qualifier3_value1").into("testForm:inputConversationBeanWithQualifier3");
        tester.input("qualifier4And5_value1").into("testForm:inputConversationBeanWithQualifier4And5");

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is("group2_bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("group2_bean2_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is("qualifier3_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is("qualifier4And5_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBean1_closeWindowContext");

        tester.assertThat("#{conversationBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is(null).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowContext_CloseConversations_WithDifferentActiveConversationScopedBeans() throws Exception
    {
        tester.assertThat("#{conversationBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is(null).after(PhaseId.RENDER_RESPONSE);

        tester.input("group1_value1").into("testForm:inputConversationBean1");
        tester.input("group2_bean1_value1").into("testForm:inputConversationGroupBean1");
        tester.input("group2_bean2_value1").into("testForm:inputConversationGroupBean2");
        tester.input("qualifier1_value1").into("testForm:inputConversationBeanWithQualifier1");
        tester.input("qualifier2_default_value1").into("testForm:inputConversationBeanWithQualifier2DefaultValues");
        tester.input("qualifier2_non_default_value1").into("testForm:inputConversationBeanWithQualifier2NonDefaultValues");
        tester.input("qualifier3_value1").into("testForm:inputConversationBeanWithQualifier3");
        tester.input("qualifier4And5_value1").into("testForm:inputConversationBeanWithQualifier4And5");

        tester.click("testForm:emptyCommand");

        tester.assertThat("#{conversationBean1.input}").is("group1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is("group2_bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is("group2_bean2_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is("qualifier1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is("qualifier2_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is("qualifier2_non_default_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is("qualifier3_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is("qualifier4And5_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:conversationBean1_closeConversationsWindowContext");

        tester.assertThat("#{conversationBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean1.input}").is(ConversationGroupBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationGroupBean2.input}").is(ConversationGroupBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier1.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2DefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier2NonDefaultValues.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier3.input}").is(null).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{conversationBeanWithInjectedQualifierBeans.beanWithQualifier4And5.input}").is(null).after(PhaseId.RENDER_RESPONSE);
    }

}
