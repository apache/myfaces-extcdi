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
package org.apache.myfaces.extensions.cdi.test.junit4;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

/**
 * @author Gerhard Petracek
 */

@RunWith(JUnit4.class)
public class InjectedProjectStageTest extends AbstractTest
{
    @Inject
    private ProjectStage projectStage;

    @Test
    public void testProjectStage()
    {
        assertEquals(this.projectStage, ProjectStage.UnitTest);
    }
}
