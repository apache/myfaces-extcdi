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
package org.apache.myfaces.extensions.cdi.jsf2.impl;

import static org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage.*;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStageActivated;
import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent;
import org.apache.myfaces.extensions.cdi.core.impl.AbstractStartupObserver;
import org.apache.myfaces.extensions.cdi.core.impl.util.ProxyUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * @author Gerhard Petracek
 */
@ProjectStageActivated({Production.class, Development.class, UnitTest.class, SystemTest.class})
@ApplicationScoped
public class ProjectStageObserver extends AbstractStartupObserver
{
    @Inject
    private ProjectStage projectStage;

    protected ProjectStageObserver()
    {
    }

    protected void checkProjectStage(@Observes StartupEvent startupEvent)
    {
        javax.faces.application.ProjectStage jsfProjectStage =
                FacesContext.getCurrentInstance().getApplication().getProjectStage();

        if(!ProxyUtils.getUnproxiedClass(this.projectStage.getClass()).getSimpleName().equals(
                jsfProjectStage.name()
        ))
        {
            this.logger.warning("The value of the JSF 2 project stage (" +
                    jsfProjectStage.name() +
                    ") is different from the CODI project stage (" +
                    this.projectStage +
                    ")");
        }
    }
}
