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
package org.apache.myfaces.extensions.cdi.core.impl.projectstage;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStageActivated;


/**
 * <p>This class implements the logic for handling
 * {@link ProjectStageActivated} annotations.</p>
 * <p>We simply listen for all annotated types to get scanned, and
 * if it has a {@link ProjectStageActivated} annotation but the
 * the current {@link ProjectStage} is not in the list, we simply
 * veto that bean.</p>
 *
 * <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class ProjectStageActivationExtension implements Extension
{

    private ProjectStage projectStage = null;

    protected ProjectStage getProjectStage()
    {
        if (projectStage == null)
        {
            // we have to do this manually since we don't have any
            // injection mechanism in place at this point since
            // the CDI container has not yet been started...
            ProjectStageProducer psp = new ProjectStageProducer();
            psp.determineProjectStage();
            projectStage = psp.getProjectStage();
        }
        return projectStage;
    }

    /**
     * Check if the {@link javax.enterprise.inject.spi.AnnotatedType} is an @Alternative and
     * is not disabled for the current {@link ProjectStage}.
     */
    protected void vetoAlternativeTypes(@Observes ProcessAnnotatedType<Object> pat)
    {
        if (pat.getAnnotatedType().getJavaClass().isAnnotationPresent(ProjectStageActivated.class))
        {
            Class<? extends ProjectStage>[] activatedIn =
                    pat.getAnnotatedType().getJavaClass().getAnnotation(ProjectStageActivated.class).value();
            if (isInProjectStage(activatedIn))
            {
                return;
            }

            // otherwise this alternative shall not get used
            pat.veto();
        }
    }

    private boolean isInProjectStage(Class<? extends ProjectStage>[] activatedIn)
    {
        if (activatedIn != null && activatedIn.length > 0)
        {
            ProjectStage ps = getProjectStage();
            for (Class<? extends ProjectStage> activated : activatedIn)
            {
                if (ps.getClass().equals(activated))
                {
                    return true;
                }
            }
        }

        return false;
    }


}
