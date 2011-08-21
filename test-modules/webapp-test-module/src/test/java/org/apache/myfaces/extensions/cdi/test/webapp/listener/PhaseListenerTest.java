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
package org.apache.myfaces.extensions.cdi.test.webapp.listener;

import org.apache.myfaces.extensions.cdi.test.webapp.listener.bean.PhaseListenerBean;
import org.apache.myfaces.extensions.cdi.test.webapp.listener.phase.AllPhaseListener;
import org.apache.myfaces.extensions.cdi.test.webapp.listener.phase.ValidationPhaseListener;
import org.apache.myfaces.test.webapp.api.annotation.BeansXml;
import org.apache.myfaces.test.webapp.api.annotation.PageBean;
import org.apache.myfaces.test.webapp.api.annotation.Tester;
import org.apache.myfaces.test.webapp.api.annotation.View;
import org.apache.myfaces.test.webapp.api.annotation.WebXml;
import org.apache.myfaces.test.webapp.api.annotation.WebappDependency;
import org.apache.myfaces.test.webapp.api.runner.WebappTestRunner;
import org.apache.myfaces.test.webapp.api.tester.ServerSideCode;
import org.apache.myfaces.test.webapp.api.tester.WebappTester;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@View(id = "listener/phase-listener-test1.xhtml",
      pageBeans = {
              @PageBean(clazz = AllPhaseListener.class),
              @PageBean(clazz = ValidationPhaseListener.class),
              @PageBean(clazz = PhaseListenerBean.class)
      }
)

@BeansXml

@WebXml("web.xml")

@WebappDependency.List
({
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-api:jar:1.0.2-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.core:myfaces-extcdi-core-impl:jar:1.0.2-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-api:jar:1.0.2-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-jsf20-module-impl:jar:1.0.2-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-api:jar:1.0.2-SNAPSHOT"),
    @WebappDependency("org.apache.myfaces.extensions.cdi.modules:myfaces-extcdi-message-module-impl:jar:1.0.2-SNAPSHOT"),
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
 * Test cases for the CODI PhaseListener support.
 *
 * @author Jakob Korherr
 */
public class PhaseListenerTest
{

    @Tester
    private static WebappTester tester;

    @Test
    public void jsfPhaseListener_AnyPhase_Called() throws Exception
    {
        // implicit initial request (including initial redirect)

        // new request
        tester.click("testForm:emptyCommand");

        // new request - to do assertions
        tester.click("testForm:emptyCommand");

        // all phases must have been called
        // NOTE that there are also assertions in AllPhaseListener
        List<Boolean> assertList = Arrays.asList(true, true, true, true, true, true);

        tester.assertThat(new ServerSideCode()
        {

            public Object execute() throws Exception
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                Map<String, Object> applicationMap = facesContext
                        .getExternalContext().getApplicationMap();


                return applicationMap.get(AllPhaseListener.BEFORE_CALLED);
            }
            
        }).is(assertList).before(PhaseId.RESTORE_VIEW);

        tester.assertThat(new ServerSideCode()
        {

            public Object execute() throws Exception
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                Map<String, Object> applicationMap = facesContext
                        .getExternalContext().getApplicationMap();


                return applicationMap.get(AllPhaseListener.AFTER_CALLED);
            }

        }).is(assertList).before(PhaseId.RESTORE_VIEW);
    }

    @Test
    public void jsfPhaseListener_SpecificPhase_Called() throws Exception
    {
        // implicit initial request (including initial redirect)

        // new request
        tester.click("testForm:emptyCommand");

        // new request - to do assertions
        tester.click("testForm:emptyCommand");

        // only phase PROCESS_VALIDATIONS must have been called
        // NOTE that there are also assertions in ValidationPhaseListener
        List<Boolean> assertList = Arrays.asList(false, false, true, false, false, false);

        tester.assertThat(new ServerSideCode()
        {

            public Object execute() throws Exception
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                Map<String, Object> applicationMap = facesContext
                        .getExternalContext().getApplicationMap();


                return applicationMap.get(ValidationPhaseListener.BEFORE_CALLED);
            }

        }).is(assertList).before(PhaseId.RESTORE_VIEW);

        tester.assertThat(new ServerSideCode()
        {

            public Object execute() throws Exception
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                Map<String, Object> applicationMap = facesContext
                        .getExternalContext().getApplicationMap();


                return applicationMap.get(ValidationPhaseListener.AFTER_CALLED);
            }

        }).is(assertList).before(PhaseId.RESTORE_VIEW);
    }

    @Test
    public void observes_PhaseEvent_AnyPhase_Called() throws Exception
    {
        // implicit initial request (including initial redirect)

        // new request
        tester.click("testForm:emptyCommand");

        // new request - to do assertions
        tester.click("testForm:emptyCommand");

        // all phases must have been called
        // NOTE that there are also assertions in the PhaseListenerBean
        List<Boolean> assertList = Arrays.asList(true, true, true, true, true, true);

        tester.assertThat("#{phaseListenerBean.beforeAnyCalled}").is(assertList).before(PhaseId.RESTORE_VIEW);
        tester.assertThat("#{phaseListenerBean.afterAnyCalled}").is(assertList).before(PhaseId.RESTORE_VIEW);
    }

    @Test
    public void observes_PhaseEvent_SpecificPhase_Called() throws Exception
    {
        // implicit initial request (including initial redirect)

        // new request
        tester.click("testForm:emptyCommand");

        // new request - to do assertions
        tester.click("testForm:emptyCommand");

        // only INVOKE_APPLICATION must have been called
        // NOTE that there are also assertions in the PhaseListenerBean
        List<Boolean> assertList = Arrays.asList(false, false, false, false, true, false);

        tester.assertThat("#{phaseListenerBean.beforeInvokeApplicationCalled}").is(assertList).before(PhaseId.RESTORE_VIEW);
        tester.assertThat("#{phaseListenerBean.afterInvokeApplicationCalled}").is(assertList).before(PhaseId.RESTORE_VIEW);
    }

}
