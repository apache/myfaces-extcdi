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
package org.apache.myfaces.extensions.cdi.jsf.test.impl.projectstage;

import org.apache.myfaces.extensions.cdi.core.api.ClassDeactivator;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.jsf.impl.projectstage.JsfProjectStageProducer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;


/**
 */
public class ProjectStageProducerTest
{
    @Test
    public void testProjectStageSetByEnvironmentOverruledByJsf() throws Exception
    {
        ClassDeactivation.setClassDeactivator(new ClassDeactivator()
        {
            private static final long serialVersionUID = -3347461101187613055L;

            public Set<Class> getDeactivatedClasses()
            {
                return Collections.emptySet();
            }
        });

        String envName = "faces.PROJECT_STAGE";
        String oldEnvVal = "" + System.getProperty(envName);
        try
        {
            System.setProperty(envName, "SystemTest");

            ProjectStageProducer psp = JsfProjectStageProducer.getInstance();
            Assert.assertNotNull(psp);

            ProjectStageProducer.setProjectStage(null);

            ProjectStage ps = psp.getProjectStage();
            Assert.assertNotNull(ps);
            Assert.assertEquals(ps, ProjectStage.SystemTest);
            Assert.assertTrue(ps == ProjectStage.SystemTest);
        }
        finally
        {
            System.setProperty(envName, oldEnvVal);
        }
    }
}
