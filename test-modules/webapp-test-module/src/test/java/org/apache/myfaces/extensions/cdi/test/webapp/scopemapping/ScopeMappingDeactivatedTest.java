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
package org.apache.myfaces.extensions.cdi.test.webapp.scopemapping;

import org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.bean.CdiBean;
import org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.bean.JsfApplicationScopedBean;
import org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.bean.JsfRequestScopedBean;
import org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.bean.JsfSessionScopedBean;
import org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.bean.JsfViewScopedBean;
import org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.deactivator.ScopeMappingDeactivator;
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

@View(id = "scopemapping/test1.xhtml",
      pageBeans = {
              @PageBean(clazz = JsfApplicationScopedBean.class),
              @PageBean(clazz = JsfSessionScopedBean.class),
              @PageBean(clazz = JsfRequestScopedBean.class),
              @PageBean(clazz = JsfViewScopedBean.class),
              @PageBean(clazz = CdiBean.class),
              @PageBean(clazz = ScopeMappingDeactivator.class)
      }
)

@BeansXml

@WebXml("scopemapping/deactivated-web.xml")

@WebappResource.List
({
    @WebappResource("scopemapping/test2.xhtml"),
    @WebappResource("scopemapping/test3.xhtml")
})

// TODO @WebappClass

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:1.0.3-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:1.0.3-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:1.0.3-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:1.0.3-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:1.0.3-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:1.0.3-SNAPSHOT"),
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
 * Test cases for the deactivation of EXTCDI-67 jsf2 scopes should be mapped
 * to cdi scopes automatically.
 * If this feature is deactivated, it should not map the scopes!
 */
public class ScopeMappingDeactivatedTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void scopemapping_deactivated_ApplicationScope() throws Exception
    {
        tester.assertThat("#{applicationBean.input}").is(JsfApplicationScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfApplicationScopedBean.input}").is(JsfApplicationScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:applicationBean_input");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{applicationBean.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfApplicationScopedBean.input}").is(JsfApplicationScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void scopemapping_deactivated_SessionScope() throws Exception
    {
        tester.assertThat("#{sessionBean.input}").is(JsfSessionScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfSessionScopedBean.input}").is(JsfSessionScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:sessionBean_input");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{sessionBean.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfSessionScopedBean.input}").is(JsfSessionScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void scopemapping_deactivated_RequestScope() throws Exception
    {
        tester.assertThat("#{requestBean.input}").is(JsfRequestScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfRequestScopedBean.input}").is(JsfRequestScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:requestBean_input");
        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{requestBean.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfRequestScopedBean.input}").is(JsfRequestScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.click("test2Form:emptyCommand");

        tester.assertThat("#{requestBean.input}").is(JsfRequestScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfRequestScopedBean.input}").is(JsfRequestScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

    @Test
    public void scopemapping_deactivated_ViewScope() throws Exception
    {
        tester.assertThat("#{viewBean.input}").is(JsfViewScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfViewScopedBean.input}").is(JsfViewScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.input("value1").into("testForm:viewBean_input");
        tester.click("testForm:emptyCommand");

        tester.assertThat("#{viewBean.input}").is("value1").after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfViewScopedBean.input}").is(JsfViewScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);

        tester.click("testForm:navigateToTest2");

        tester.assertThat("#{viewBean.input}").is(JsfViewScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
        tester.assertThat("#{cdiBean.jsfViewScopedBean.input}").is(JsfViewScopedBean.INPUT_DEFAULT_VALUE).after(PhaseId.RENDER_RESPONSE);
    }

}
