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
package org.apache.myfaces.extensions.cdi.example.test;

import org.apache.myfaces.extensions.cdi.test.cargo.SimplePageInteraction;
import org.apache.myfaces.extensions.cdi.test.cargo.runner.JUnit4WithCargo;
import org.apache.myfaces.extensions.cdi.example.jsf20.view.config.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Typed;

@RunWith(JUnit4WithCargo.class)
@Typed()
public class SimpleTestCase extends BaseCargoTest
{
    // NOTE that new @Test means new WebClient means new WindowContext

    @Test
    public void testSimpleRegistration() throws Exception
    {
        SimplePageInteraction pageInteraction = new SimplePageInteraction(getTestConfiguration())
                .with(Pages.SimpleRegistration.Form.class)
                .with(Pages.SimpleRegistration.Summary.class);

        pageInteraction
                .start(Pages.SimpleRegistration.Form.class)
                .useForm("mainForm");

        //prependId="false"
        pageInteraction.setValue("loginName", "codi");
        pageInteraction
                .click("register")
                .checkState(Pages.SimpleRegistration.Summary.class);

        pageInteraction.checkTextValue("loginName", "codi");

        //close conversation
        pageInteraction
                .clickOk()
                .checkState(Pages.SimpleRegistration.Form.class);

        pageInteraction.checkTextValue("loginName", "");
    }
}
