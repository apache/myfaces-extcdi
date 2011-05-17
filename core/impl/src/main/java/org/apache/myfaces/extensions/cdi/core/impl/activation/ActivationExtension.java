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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.myfaces.extensions.cdi.core.api.activation.ExpressionActivated;
import org.apache.myfaces.extensions.cdi.core.api.activation.ProjectStageActivated;
import org.apache.myfaces.extensions.cdi.core.api.interpreter.ExpressionInterpreter;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.api.activation.Deactivatable;

import java.util.logging.Level;
import java.util.logging.Logger;


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
            }
        }
    }

    private void checkExpressionActivated(ProcessAnnotatedType<Object> processAnnotatedType)
    {
        if (processAnnotatedType.getAnnotatedType().getJavaClass().isAnnotationPresent(ExpressionActivated.class))
        {
            ExpressionActivated expressionActivated = processAnnotatedType.getAnnotatedType().getJavaClass()
                            .getAnnotation(ExpressionActivated.class);

            String expressions = expressionActivated.value();

            Class<? extends ExpressionInterpreter> interpreter = expressionActivated.interpreter();

            if(interpreter.equals(ExpressionInterpreter.class))
            {
                interpreter = PropertyExpressionInterpreter.class;
            }

            ExpressionInterpreter<String, Boolean> expressionInterpreter =
                    ClassUtils.tryToInstantiateClass(interpreter);

            if(expressionInterpreter == null)
            {
                Logger logger = Logger.getLogger(getClass().getName());

                if(logger.isLoggable(Level.WARNING))
                {
                    logger.warning("can't instantiate " + interpreter.getClass().getName());
                }
                return;
            }

            expressions = "configName:" + expressionActivated.configName() + ";" + expressions;
            if (!expressionInterpreter.evaluate(expressions))
            {
                // this alternative shall not get used
                processAnnotatedType.veto();
            }
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
