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
package org.apache.myfaces.extensions.cdi.core.test.impl.config;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import static org.testng.Assert.*;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.ConfiguredArtifactUtils;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.activation.ClassDeactivator;

import java.util.Date;

public class ConfigTest
{
    @BeforeMethod
    private void beforeTest()
    {
        reset();
    }

    @AfterMethod
    private void afterTest()
    {
        reset();
    }

    protected void reset()
    {
        new ConfiguredArtifactUtils() {
            @Override
            public void reset()
            {
                super.reset();
            }
        }.reset();

        new ProjectStageProducer() {
            private static final long serialVersionUID = -2656209286979856460L;

            @Override
            public void reset()
            {
                super.reset();
            }
        }.reset();
        System.getProperties().remove("org.apache.myfaces.extensions.cdi.ProjectStage");
        System.setProperty("org.apache.myfaces.extensions.cdi.CustomValue", "");
        System.setProperty("ext.test.CustomValue", "");
    }

    @Test
    public void testConfiguredValueViaSystemPropertyConfig()
    {
        assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.Production);

        reset();

        System.setProperty("org.apache.myfaces.extensions.cdi.ProjectStage", "Development");

        assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.Development);
    }

    @Test
    public void testLazyInitBeforeStaticCall()
    {
        assertEquals(new ProjectStageProducer(){}.getProjectStage(), ProjectStage.Production);
    }

    @Test
    public void testConfiguredClassViaSystemPropertyConfig()
    {
        assertNull(CodiUtils.lookupFromEnvironment(ClassDeactivator.class));

        System.setProperty("org.apache.myfaces.extensions.cdi.ClassDeactivator", TestClassDeactivator.class.getName());

        assertEquals(CodiUtils.lookupFromEnvironment(ClassDeactivator.class).getDeactivatedClasses().iterator().next(), TestClassDeactivator.class);
    }

    @Test
    public void testCustomConfiguredValueResolver()
    {
        assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.Production);
        assertEquals(TestConfiguredValueResolver.isCalled(), false);

        TestConfiguredValueResolver.setActivated(true);

        reset();

        assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.Production);
        assertEquals(TestConfiguredValueResolver.isCalled(), true);

        TestConfiguredValueResolver.setActivated(false);
    }

    @Test
    public void testCustomConfiguredValue()
    {
        assertEquals(CodiUtils.lookupFromEnvironment("CustomValue", String.class), "");

        reset();

        System.setProperty("org.apache.myfaces.extensions.cdi.CustomValue", "test");

        assertEquals(CodiUtils.lookupFromEnvironment("CustomValue", String.class), "test");
    }

    @Test
    public void testCustomConfiguredFormat()
    {
        assertEquals(CodiUtils.lookupFromEnvironment("ext.test.CustomValue", String.class), "");

        reset();

        System.setProperty("ext.test.CustomValue", "test");

        assertEquals(CodiUtils.lookupFromEnvironment("ext.test.CustomValue", String.class), "test");
    }

    @Test
    public void testCustomArtifact()
    {
        assertEquals(CodiUtils.lookupFromEnvironment(TestArtifact.class), null);

        reset();

        System.setProperty("org.apache.myfaces.extensions.cdi.TestArtifact", TestArtifact.class.getName());

        assertEquals(CodiUtils.lookupFromEnvironment(TestArtifact.class).getClass().getName(), TestArtifact.class.getName());

        //test cache
        Date created = CodiUtils.lookupFromEnvironment(TestArtifact.class).getCreated();
        assertEquals(CodiUtils.lookupFromEnvironment(TestArtifact.class).getCreated(), created);
    }

    @Test
    public void testServiceLoaderConfig()
    {
        assertEquals(CodiUtils.lookupFromEnvironment(TestInterface.class).getValue(), "TestImpl");
    }

    @Test
    public void testLookupConfigByConvention()
    {
        System.setProperty("ConfigTest.custom_value", "true");
        assertEquals(getCustomValue(), "true");
    }

    private String getCustomValue()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, String.class, "false");
    }

    @Test
    public void testLookupConfigByConventionDefaultValue()
    {
        assertEquals(isCustomValue(), "true");
    }

    @Test
    public void testProjectStageSetPropertyFileAndCustomResolver() throws Exception
    {
        try
        {
            assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.Production);

            reset();

            PropertyFileResolverForProjectStage.activate();
            assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.IntegrationTest);
        }
        finally
        {
            PropertyFileResolverForProjectStage.deactivate();
        }

        reset();
        assertEquals(ProjectStageProducer.getInstance().getProjectStage(), ProjectStage.Production);
    }

    private String isCustomValue()
    {
        return CodiUtils.lookupConfigFromEnvironment(null, String.class, "true");
    }
}
