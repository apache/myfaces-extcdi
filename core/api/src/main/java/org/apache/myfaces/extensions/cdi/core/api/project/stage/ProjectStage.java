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
package org.apache.myfaces.extensions.cdi.core.api.project.stage;

import org.apache.myfaces.extensions.cdi.core.api.common.qualifier.Current;

import javax.enterprise.inject.Produces;
import java.io.Serializable;

public class ProjectStage implements Serializable
{
    private static final long serialVersionUID = 8372440985154139133L;

    public static final String DEFAULT_STAGE = "Production";

    private String value;

    protected ProjectStage()
    {
        //required for proxy libs
    }

    private ProjectStage(String value)
    {
        this.value = value;
    }

    @Produces
    @Current
    public ProjectStage createProjectStage(ProjectStageResolver projectStageResolver)
    {
        return new ProjectStage(projectStageResolver.getCurrentProjectStageName());
    }

    public boolean is(String projectStage)
    {
        return this.value.equals(projectStage);
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
