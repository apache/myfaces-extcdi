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
package org.apache.myfaces.extensions.cdi.core.impl.activation;

import java.util.logging.Logger;

import org.apache.myfaces.extensions.cdi.core.api.activation.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.activation.ProjectStageActivated;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.ActivationUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;


/**
 * <p>This class implements the logic for handling
 * {@link ProjectStageActivated} annotations.</p>
 * <p>We simply listen for all annotated types to get scanned, and
 * if it has a {@link org.apache.myfaces.extensions.cdi.core.api.activation.ProjectStageActivated} annotation but the
 * the current {@link ProjectStage} is not in the list, we simply
 * veto that bean.</p>
 *
 */
public class ActivationExtension implements Extension, Deactivatable
{
    private static final Logger LOG = Logger.getLogger(ActivationExtension.class.getName());


    protected void initProjectStage(@Observes AfterDeploymentValidation afterDeploymentValidation)
    {
        //trigger initialization
        ProjectStageProducer.getInstance();
    }

    /**
     * Check if the {@link javax.enterprise.inject.spi.AnnotatedType} is an @Alternative and
     * is not disabled for the current {@link ProjectStage}.
     */
    protected void vetoAlternativeTypes(@Observes ProcessAnnotatedType<Object> processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        CodiStartupBroadcaster.broadcastStartup();

        checkProjectStageActivated(processAnnotatedType);

        checkExpressionActivated(processAnnotatedType);

        //placed here to ensue that no validation of a deactivated class will be performance
        //TODO validateCodiImplementationRules(processAnnotatedType);
    }

    private void checkProjectStageActivated(ProcessAnnotatedType<Object> processAnnotatedType)
    {
        if (processAnnotatedType.getAnnotatedType().getJavaClass().isAnnotationPresent(ProjectStageActivated.class))
        {
            Class<? extends ProjectStage>[] activatedIn = processAnnotatedType.getAnnotatedType().getJavaClass()
                            .getAnnotation(ProjectStageActivated.class).value();

            if (!isInProjectStage(activatedIn))
            {
                // this alternative shall not get used
                processAnnotatedType.veto();

                LOG.finer("ProjectState Veto for bean with type: "
                          + processAnnotatedType.getAnnotatedType().getJavaClass() );
            }
        }
    }

    private void checkExpressionActivated(ProcessAnnotatedType<Object> processAnnotatedType)
    {
        Class<?> annotatedClass = processAnnotatedType.getAnnotatedType().getJavaClass();

        if(!ActivationUtils.isActivated(annotatedClass, PropertyExpressionInterpreter.class))
        {
            processAnnotatedType.veto();

            LOG.finer("Expression Veto for bean with type: "
                      + processAnnotatedType.getAnnotatedType().getJavaClass() );
        }
    }

    private boolean isInProjectStage(Class<? extends ProjectStage>[] activatedIn)
    {
        if (activatedIn != null && activatedIn.length > 0)
        {
            ProjectStage ps = ProjectStageProducer.getInstance().getProjectStage();
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

    /**
     * {@inheritDoc}
     */
    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
