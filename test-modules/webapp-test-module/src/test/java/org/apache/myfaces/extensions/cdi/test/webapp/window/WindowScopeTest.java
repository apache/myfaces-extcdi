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
package org.apache.myfaces.extensions.cdi.test.webapp.window;

import org.apache.myfaces.extensions.cdi.test.webapp.window.bean.WindowBean1;
import org.apache.myfaces.extensions.cdi.test.webapp.window.bean.WindowBean2;
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

@View(id = "window/test1.xhtml",
      pageBeans = {
              @PageBean(clazz = WindowBean1.class),
              @PageBean(clazz = WindowBean2.class)
      }
)

@BeansXml

@WebXml("web.xml")

@WebappResource.List
({
    @WebappResource("window/test2.xhtml"),
    @WebappResource("window/test3.xhtml")
})

// TODO @WebappClass

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:1.0.0-SNAPSHOT"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-impl:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-spi:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-jsf:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-resource:jar:1.0.0-alpha-2"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-web:jar:1.0.0-alpha-2"),  
    @WebappDependency("javassist:javassist:jar:3.12.0.GA"),
    @WebappDependency("net.sf.scannotation:scannotation:jar:1.0.2")
})

@RunWith(WebappTestRunner.class)

/**
 * Test cases for the CODI WindowScope.
 *
 * @author Jakob Korherr
 */
public class WindowScopeTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void windowScope_Normal_Navigation() throws Exception
    {
        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:windowBean1_input");
        tester.input("bean2_value1").into("testForm:windowBean2_input");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:navigateToTest3");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:navigateToTest1");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowScope_Redirect_Navigation() throws Exception
    {
        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:windowBean1_input");
        tester.input("bean2_value1").into("testForm:windowBean2_input");
        tester.click("testForm:redirectToTest2");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:redirectToTest3");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:redirectToTest1");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowScope_Get_Navigation() throws Exception
    {
        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:windowBean1_input");
        tester.input("bean2_value1").into("testForm:windowBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:getToTest2");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:getToTest3");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:getToTest1");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);
    }
    
    @Test
    public void windowScope_Close_WindowContext() throws Exception
    {
        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:windowBean1_input");
        tester.input("bean2_value1").into("testForm:windowBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:windowBean1_closeWindowContext");

        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void windowScope_Close_WindowScopedGroup() throws Exception
    {
        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:windowBean1_input");
        tester.input("bean2_value1").into("testForm:windowBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{windowBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:windowBean1_closeWindowScopedGroup");

        tester.assertThat("#{windowBean1.input}").is(WindowBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{windowBean2.input}").is(WindowBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

}