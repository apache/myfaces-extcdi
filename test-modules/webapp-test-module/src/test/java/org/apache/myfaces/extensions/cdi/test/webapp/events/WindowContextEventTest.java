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
import org.apache.myfaces.test.webapp.api.runner.WebappTestRunner;
import org.apache.myfaces.test.webapp.api.tester.WebappTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.faces.event.PhaseId;

@View(id = "events/windowcontext-events-test1.xhtml",
      pageBeans = {
              @PageBean(clazz = ConversationBean.class),
              @PageBean(clazz = EventsBean.class)
      }
)

@BeansXml

@WebXml("events/windowcontext-events-enabled-web.xml")

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:0.9.0"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:0.9.0"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:0.9.0"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:0.9.0"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:0.9.0"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:0.9.0"),
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
 * Test cases for the CODI WindowContext Events.
 *
 * @author Jakob Korherr
 */
public class WindowContextEventTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void createWindowContextEvent_closeWindowContextEvent() throws Exception
    {
        // a WindowContext is created for every request
        // NOTE that the closing can only be asserted on the next request,
        // because the WindowContext still needs to be open in RENDER_RESPONSE
        // after-PhaseListeners.

        tester.assertThat("#{eventsBean.windowContextCreated}").is(1).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.windowContextClosed}").is(0).after(PhaseId.RENDER_RESPONSE);

        // new request
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{eventsBean.windowContextCreated}").is(2).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.windowContextClosed}").is(1).after(PhaseId.RENDER_RESPONSE);

        // new request
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{eventsBean.windowContextCreated}").is(3).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{eventsBean.windowContextClosed}").is(2).after(PhaseId.RENDER_RESPONSE);
    }

}
