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
package org.apache.myfaces.extensions.cdi.test.webapp.viewaccess;

import org.apache.myfaces.extensions.cdi.test.webapp.viewaccess.bean.ViewAccessBean1;
import org.apache.myfaces.extensions.cdi.test.webapp.viewaccess.bean.ViewAccessBean2;
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

@View(id = "viewaccess/test1.xhtml",
      pageBeans = {
              @PageBean(clazz = ViewAccessBean1.class),
              @PageBean(clazz = ViewAccessBean2.class)
      }
)

@BeansXml

@WebXml("web.xml")

@WebappResource.List
({
    @WebappResource("viewaccess/test2.xhtml"),
    @WebappResource("viewaccess/test3.xhtml")
})

// TODO @WebappClass

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:0.9.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:0.9.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:0.9.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:0.9.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:0.9.4-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:0.9.4-SNAPSHOT"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-impl:jar:1.0.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-spi:jar:1.0.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-jsf:jar:1.0.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-resource:jar:1.0.0"),
    @WebappDependency("org.apache.openwebbeans:openwebbeans-web:jar:1.0.0"),
    @WebappDependency("javassist:javassist:jar:3.12.0.GA"),
    @WebappDependency("net.sf.scannotation:scannotation:jar:1.0.2")
})

@RunWith(WebappTestRunner.class)

/**
 * Test cases for the CODI ViewAccessScope.
 *
 * @author Jakob Korherr
 */
public class ViewAccessScopeTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void viewAccess_ExpireOnPage2() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:navigateToTest2");

        // no touching

        tester.click("test2Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnPage2_Redirect() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:redirectToTest2");

        // no touching

        tester.click("test2Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnPage2_Get() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:getToTest2");

        // no touching

        tester.click("test2Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnPage3() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:navigateToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:navigateToTest3");

        // no touching

        tester.click("test3Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnPage3_Redirect() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:redirectToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:redirectToTest3");

        // no touching

        tester.click("test3Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnPage3_Get() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:getToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:getToTest3");

        // no touching

        tester.click("test3Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnAlreadyTouchedPage_SecondNonTouchingVisit() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:navigateToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:navigateToTest3");

        // touch on page3
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:emptyCommand");

        // no touching

        tester.click("test3Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:navigateToTest2");

        // no touching

        tester.click("test2Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet after we came here from page3.xhtml
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnAlreadyTouchedPage_SecondNonTouchingVisit_Redirect() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:redirectToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:redirectToTest3");

        // touch on page3
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:emptyCommand");

        // no touching

        tester.click("test3Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:redirectToTest2");

        // no touching

        tester.click("test2Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet after we came here from page3.xhtml
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_ExpireOnAlreadyTouchedPage_SecondNonTouchingVisit_Get() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:getToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:getToTest3");

        // touch on page3
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:emptyCommand");

        // no touching

        tester.click("test3Form:emptyCommand");

        // it must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test3Form:getToTest2");

        // no touching

        tester.click("test2Form:emptyCommand");

        // now it must be gone, we didn't access the bean on this view yet after we came here from page3.xhtml
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Bean1_ExpireOnPage2_Bean2_ExpireOnPage3() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:viewAccessBean1_input");
        tester.input("bean2_value1").into("testForm:viewAccessBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:navigateToTest2");

        // only touch bean2 on page2
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        // no touching

        tester.click("test2Form:emptyCommand");

        // bean1 must be gone by now
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        // bean2 must still exist, because we already accessed the bean on this view once
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:navigateToTest3");

        // no touching

        tester.click("test3Form:emptyCommand");

        // bean1 must still be gone
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        // now bean2 must be gone, we didn't access the bean on this view yet
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Close_InjectedConversation() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:viewAccessBean1_closeConversation");

        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Restart_InjectedConversation() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:viewAccessBean1_restartConversation");

        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Close_InjectedConversation_OnPage2() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:navigateToTest2");

        // touch on page2
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:viewAccessBean1_closeConversation");

        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Close_InjectedConversation_TwoBeans() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:viewAccessBean1_input");
        tester.input("bean2_value1").into("testForm:viewAccessBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:viewAccessBean1_closeConversation");

        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Close_WindowContext_TwoBeans() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:viewAccessBean1_input");
        tester.input("bean2_value1").into("testForm:viewAccessBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:viewAccessBean1_closeWindowContext");

        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_CloseConversations_WindowContext_TwoBeans() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("bean1_value1").into("testForm:viewAccessBean1_input");
        tester.input("bean2_value1").into("testForm:viewAccessBean2_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("bean1_value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is("bean2_value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:viewAccessBean1_closeConversationsWindowContext");

        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{viewAccessBean2.input}").is(ViewAccessBean2.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void viewAccess_Close_ViewAccessScopedGroup_WindowContext_ShouldNotWork() throws Exception
    {
        tester.assertThat("#{viewAccessBean1.input}").is(ViewAccessBean1.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewAccessBean1_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:viewAccessBean1_closeViewAccessScopedGroup");

        // scope should not be closed
        tester.assertThat("#{viewAccessBean1.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
    }

}
