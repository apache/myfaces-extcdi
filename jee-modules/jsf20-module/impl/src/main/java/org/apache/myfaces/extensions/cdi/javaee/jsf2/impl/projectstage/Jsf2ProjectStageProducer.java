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
package org.apache.myfaces.extensions.cdi.javaee.jsf2.impl.projectstage;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;

import javax.faces.context.FacesContext;

/**
 * This is a JSF-2 specific version of the {@link ProjectStageProducer}.
 * In addition to it's parent class, it will first try to pickup the
 * JSF ProjectStage from the Application. If this returns the JSF
 * {@link javax.faces.application.ProjectStage#Production}, we'll go on
 * and try to figure out our own EXTCDI 
 * {@link org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage}s
 */
public class Jsf2ProjectStageProducer extends ProjectStageProducer
{
    @Override
    protected ProjectStage determineCustomProjectStage()
    {
        ProjectStage codiProjectStage = null;

        javax.faces.application.ProjectStage jsfProjectStage =
                FacesContext.getCurrentInstance().getApplication().getProjectStage();

        if (jsfProjectStage != null)
        {
            // so we need to match them to our CODI ProjectStages.
            switch(jsfProjectStage)
            {
                case UnitTest:
                    codiProjectStage = ProjectStage.UnitTest;
                    break;

                case Development:
                    codiProjectStage = ProjectStage.Development;
                    break;

                case SystemTest:
                    codiProjectStage = ProjectStage.SystemTest;
                    break;

                default:
                // we cannot match others, so codiProjectStage remains null in the default case
            }
        }

        return codiProjectStage;
    }
}
