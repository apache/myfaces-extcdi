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
package org.apache.myfaces.extensions.cdi.test.webapp.events;

import org.apache.myfaces.extensions.cdi.test.webapp.events.bean.ConversationBean;
import org.apache.myfaces.extensions.cdi.test.webapp.events.bean.EventsBean;
import org.apache.myfaces.test.webapp.api.annotation.BeansXml;
import org.apache.myfaces.test.webapp.api.annotation.PageBean;
import org.apache.myfaces.test.webapp.api.annotation.Tester;
import org.apache.myfaces.test.webapp.api.annotation.View;
import org.apache.myfaces.test.webapp.api.annotation.WebXml;
import org.apache.myfaces.test.webapp.api.annotation.WebappDependency;
import org.apache.myfaces.test.webapp.api.annotation.WebappResource;
import org.apache.myfaces.test.webapp.api.runner.WebappTestRunner;
import org.apache.myfaces.test.webapp.api.tester.WebappTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.faces.event.PhaseId;

@View(id = "events/conversation-events-test1.xhtml",
      pageBeans = {
              @PageBean(clazz = ConversationBean.class),
              @PageBean(clazz = EventsBean.class)
      }
)

@BeansXml

@WebXml("events/conversation-events-enabled-web.xml")

@WebappResource.List
({
    @WebappResource("events/conversation-events-test2.xhtml")
})

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:0.9.1-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:0.9.1-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:0.9.1-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:0.9.1-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:0.9.1-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:0.9.1-SNAPSHOT"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-impl:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-spi:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-jsf:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-resource:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-web:jar:1.0.0-alpha-2"),
    @WebappDependency("javassist:javassist:jar:3.12.0.GA"),
    @WebappDependency("net.sf.scannotation:scannotation:jar:1.0.2")/*,
    @WebappDependency("org.os890.codi.addon:web-xml-config:jar:1.0.0-alpha")*/
})

@RunWith(WebappTestRunner.class)

/**
 * Test cases for the CODI ConversationEvents.
 *
 * @author Jakob Korherr
 */
public class ConversationEventTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void startConversationEvent_Implicit_Start() throws Exception
    {
        // reset EventsBean and navigate to conversation-events-test2.xhtml 
        tester.click("testForm:eventsBean_resetAndStartConversationTest");

        // conversation-events-test2.xhtml references conversationBean, thus the conversation must have been started
        tester.assertThat("#{eventsBean.conversationStarted}").is(true).after(PhaseId.RENDER_RESPONSE);

        tester.input("value").into("testForm:conversationBean_input");
        tester.click("testForm:eventsBean_closeStartedConversationAndNavigateToTest1");

        // value must be in the conversation bean
        tester.assertThat("#{conversationBean.input}").is("value").after(PhaseId.UPDATE_MODEL_VALUES);

        // conversation instance from the event must be able to close the conversation
        tester.assertThat("#{conversationBean.input}").is(ConversationBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void closeConversationEvent_Injected_Conversation_Close() throws Exception
    {
        // reset EventsBean and navigate to conversation-events-test2.xhtml
        tester.click("testForm:eventsBean_resetAndStartConversationTest");

        // conversation started, but didn't close yet
        tester.assertThat("#{eventsBean.conversationClosed}").is(false).after(PhaseId.RENDER_RESPONSE);

        tester.input("value").into("testForm:conversationBean_input");
        tester.click("testForm:conversationBean_closeConversation");

        // value must be in the conversation bean and the conversation must still be open
        tester.assertThat("#{conversationBean.input}").is("value").after(PhaseId.UPDATE_MODEL_VALUES);
        tester.assertThat("#{eventsBean.conversationClosed}").is(false).after(PhaseId.UPDATE_MODEL_VALUES);

        // conversation must be closed now
        tester.assertThat("#{conversationBean.input}").is(ConversationBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.conversationClosed}").is(true).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void closeConversationEvent_WindowContext_Close() throws Exception
    {
        // reset EventsBean and navigate to conversation-events-test2.xhtml
        tester.click("testForm:eventsBean_resetAndStartConversationTest");

        // conversation started, but didn't close yet
        tester.assertThat("#{eventsBean.conversationClosed}").is(false).after(PhaseId.RENDER_RESPONSE);

        tester.input("value").into("testForm:conversationBean_input");
        tester.click("testForm:conversationBean_closeWindowContext");

        // value must be in the conversation bean and the conversation must still be open
        tester.assertThat("#{conversationBean.input}").is("value").after(PhaseId.UPDATE_MODEL_VALUES);
        tester.assertThat("#{eventsBean.conversationClosed}").is(false).after(PhaseId.UPDATE_MODEL_VALUES);

        // conversation must be closed now
        tester.assertThat("#{conversationBean.input}").is(ConversationBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.conversationClosed}").is(true).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void restartConversationEvent_Injected_Conversation_Restart() throws Exception
    {
        // reset EventsBean and navigate to conversation-events-test2.xhtml
        tester.click("testForm:eventsBean_resetAndStartConversationTest");

        // conversation started, but didn't restart yet
        tester.assertThat("#{eventsBean.conversationRestarted}").is(false).after(PhaseId.RENDER_RESPONSE);

        tester.input("value").into("testForm:conversationBean_input");
        tester.click("testForm:conversationBean_restartConversation");

        // value must be in the conversation bean and the conversation must still be open
        tester.assertThat("#{conversationBean.input}").is("value").after(PhaseId.UPDATE_MODEL_VALUES);
        tester.assertThat("#{eventsBean.conversationRestarted}").is(false).after(PhaseId.UPDATE_MODEL_VALUES);

        // conversation must be restarted now
        tester.assertThat("#{conversationBean.input}").is(ConversationBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.conversationRestarted}").is(true).after(PhaseId.RENDER_RESPONSE);
    }

}
