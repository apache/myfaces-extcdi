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
import org.apache.myfaces.extensions.cdi.test.webapp.events.bean.ViewAccessBean;
import org.apache.myfaces.extensions.cdi.test.webapp.events.bean.WindowBean;
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

@View(id = "events/scope-bean-event-test1.xhtml",
      pageBeans = {
              @PageBean(clazz = ConversationBean.class),
              @PageBean(clazz = ViewAccessBean.class),
              @PageBean(clazz = WindowBean.class),
              @PageBean(clazz = EventsBean.class)
      }
)

@BeansXml

@WebXml("web.xml") // use normal web.xml --> events are deactivated

@WebappResource.List
({
    @WebappResource("events/scope-bean-event-test2.xhtml")
})

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:1.0.0-SNAPSHOT"),
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
 * Test cases for the CODI ScopeBeanEvent, if it is deactivated (default).
 *
 * @author Jakob Korherr
 */
public class ScopeBeanEventDeactivatedTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void scopeBeanEvent_ConversationScoped_Deactivated() throws Exception
    {
        // reset EventsBean and navigate to scope-bean-event-test2.xhtml
        tester.click("testForm:eventsBean_resetAndStartScopeBeanTest");

        // manually reference conversationBean
        tester.assertThat("#{conversationBean.input}").is(ConversationBean.INPUT_DEFAULT_VALUE).before(PhaseId.RENDER_RESPONSE);

        // conversationBean must have been scoped once
        tester.assertThat("#{eventsBean.conversationBeanScoped}").is(0).after(PhaseId.RENDER_RESPONSE);

        // close conversation
        tester.click("test2Form:conversationBean_closeConversation");

        // manually reference conversationBean
        tester.assertThat("#{conversationBean.input}").is(ConversationBean.INPUT_DEFAULT_VALUE).before(PhaseId.RENDER_RESPONSE);

        // conversationBean must have been scoped again
        tester.assertThat("#{eventsBean.conversationBeanScoped}").is(0).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void scopeBeanEvent_ViewAccessScoped_Deactivated() throws Exception
    {
        // reset EventsBean and navigate to scope-bean-event-test2.xhtml
        tester.click("testForm:eventsBean_resetAndStartScopeBeanTest");

        // manually reference viewAccessBean
        tester.assertThat("#{viewAccessBean.input}").is(ViewAccessBean.INPUT_DEFAULT_VALUE).before(PhaseId.RENDER_RESPONSE);

        // viewaccessBean must have been scoped once
        tester.assertThat("#{eventsBean.viewaccessBeanScoped}").is(0).after(PhaseId.RENDER_RESPONSE);

        // close windowContext
        tester.click("test2Form:viewAccessBean_closeWindowContext");

        // manually reference viewAccessBean
        tester.assertThat("#{viewAccessBean.input}").is(ViewAccessBean.INPUT_DEFAULT_VALUE).before(PhaseId.RENDER_RESPONSE);

        // viewaccessBean must have been scoped again
        tester.assertThat("#{eventsBean.viewaccessBeanScoped}").is(0).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void scopeBeanEvent_WindowScoped_Deactivated() throws Exception
    {
        // reset EventsBean and navigate to scope-bean-event-test2.xhtml
        tester.click("testForm:eventsBean_resetAndStartScopeBeanTest");

        // manually reference windowBean
        tester.assertThat("#{windowBean.input}").is(WindowBean.INPUT_DEFAULT_VALUE).before(PhaseId.RENDER_RESPONSE);

        // windowBean must have been scoped once
        tester.assertThat("#{eventsBean.windowBeanScoped}").is(0).after(PhaseId.RENDER_RESPONSE);

        // close window context
        tester.click("test2Form:windowBean_closeWindowContext");

        // manually reference windowBean
        tester.assertThat("#{windowBean.input}").is(WindowBean.INPUT_DEFAULT_VALUE).before(PhaseId.RENDER_RESPONSE);

        // windowBean must have been scoped again
        tester.assertThat("#{eventsBean.windowBeanScoped}").is(0).after(PhaseId.RENDER_RESPONSE);
    }

}
