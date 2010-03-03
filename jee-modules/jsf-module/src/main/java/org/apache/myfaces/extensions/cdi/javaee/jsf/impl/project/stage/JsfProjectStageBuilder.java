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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.project.stage;

import org.apache.myfaces.extensions.cdi.core.api.common.qualifier.Current;
import org.apache.myfaces.extensions.cdi.core.api.project.stage.ProjectStage;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.project.stage.JsfProjectStage;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

@Typed() // required workaround
public class JsfProjectStageBuilder implements JsfProjectStage
{
    private static final long serialVersionUID = 2561553908344678363L;

    private ProjectStage projectStage;

    protected JsfProjectStageBuilder()
    {
        //required for proxy libs
    }

    private JsfProjectStageBuilder(ProjectStage projectStage)
    {
        this.projectStage = projectStage;
    }

    @Produces
    public JsfProjectStage createJsfProjectStage(@Current ProjectStage projectStage)
    {
        return new JsfProjectStageBuilder(projectStage);
    }

    public boolean isDevelopment()
    {
        return this.projectStage.is(JsfProjectStageEnum.Development.getProjectStageName());
    }

    public boolean isUnitTest()
    {
        return this.projectStage.is(JsfProjectStageEnum.UnitTest.getProjectStageName());
    }

    public boolean isSystemTest()
    {
        return this.projectStage.is(JsfProjectStageEnum.SystemTest.getProjectStageName());
    }

    public boolean isProduction()
    {
        return this.projectStage.is(JsfProjectStageEnum.Production.getProjectStageName());
    }
}