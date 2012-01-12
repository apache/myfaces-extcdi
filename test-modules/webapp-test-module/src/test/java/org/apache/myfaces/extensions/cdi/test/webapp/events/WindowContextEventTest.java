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
import org.apache.myfaces.extensions.cdi.test.webapp.events.config.DefaultWindowContextConfig;
import org.apache.myfaces.extensions.cdi.test.webapp.events.config.WindowContextEventEnabledConfig;
import org.apache.myfaces.test.webapp.api.annotation.BeansXml;
import org.apache.myfaces.test.webapp.api.annotation.PageBean;
import org.apache.myfaces.test.webapp.api.annotation.Tester;
import org.apache.myfaces.test.webapp.api.annotation.View;
import org.apache.myfaces.test.webapp.api.annotation.WebXml;
import org.apache.myfaces.test.webapp.api.annotation.WebappDependency;
import org.apache.myfaces.test.webapp.api.runner.WebappTestRunner;
import org.apache.myfaces.test.webapp.api.tester.WebappTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.faces.event.PhaseId;

@View(id = "events/windowcontext-events-test1.xhtml",
      pageBeans = {
              @PageBean(clazz = ConversationBean.class),
              @PageBean(clazz = EventsBean.class),
              @PageBean(clazz = DefaultWindowContextConfig.class),
              @PageBean(clazz = WindowContextEventEnabledConfig.class)
      }
)

@BeansXml("events/windowcontext-events-enabled-beans.xml")

@WebXml("web.xml")

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
 * Test cases for the CODI WindowContext Events.
 */
public class WindowContextEventTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void createWindowContextEvent_closeWindowContextEvent() throws Exception
    {
        tester.assertThat("#{eventsBean.windowContextCreated}").is(1).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.windowContextClosed}").is(0).after(PhaseId.RENDER_RESPONSE);

        // new request
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{eventsBean.windowContextCreated}").is(1).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.windowContextClosed}").is(0).after(PhaseId.RENDER_RESPONSE);

        // manually close WindowContext - must also fire CloseWindowContextEvent
        tester.click("testForm:closeWindowContext");

        tester.assertThat("#{eventsBean.windowContextCreated}").is(1).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.windowContextClosed}").is(1).after(PhaseId.RENDER_RESPONSE);
    }

}
