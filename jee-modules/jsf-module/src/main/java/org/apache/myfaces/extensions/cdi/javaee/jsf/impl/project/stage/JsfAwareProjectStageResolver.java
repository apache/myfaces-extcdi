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

import org.apache.myfaces.extensions.cdi.core.api.project.config.ConfigManager;
import org.apache.myfaces.extensions.cdi.core.api.project.config.InitParameter;
import org.apache.myfaces.extensions.cdi.core.api.project.config.InitParameterNames;
import org.apache.myfaces.extensions.cdi.core.api.project.stage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.project.stage.ProjectStageResolver;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.qualifier.Jsf;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

@Alternative
public class JsfAwareProjectStageResolver implements ProjectStageResolver
{
    //TODO @Inject @Jsf @InitParameter doesn't work!?
    private ConfigManager<String, String> configManager;

    protected JsfAwareProjectStageResolver()
    {
        //required for proxy libs
    }

    @Inject
    public JsfAwareProjectStageResolver(
            @InitParameter(Jsf.class) /*TODO @Jsf*/ ConfigManager<String, String> configManager)
    {
        this.configManager = configManager;
    }

    public String getCurrentProjectStageName()
    {
        String projectStageName = this.configManager.getValue(InitParameterNames.APPLICATION_PROJECT_STAGE);

        if (isProjectStageAvailable(projectStageName))
        {
            return projectStageName;
        }

        return ProjectStage.DEFAULT_STAGE;
    }

    private boolean isProjectStageAvailable(String independentProjectStageName)
    {
        return !(independentProjectStageName == null || "".equals(independentProjectStageName));
    }
}