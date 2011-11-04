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
package org.apache.myfaces.extensions.cdi.core.impl;

import static org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation.isClassActivated;
import org.apache.myfaces.extensions.cdi.core.api.activation.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows the deactivation of interceptors
 */
public class CodiDeactivatorExtension implements Extension, Deactivatable
{
    //don't use a static LOG
    private Logger logger =  Logger.getLogger(CodiDeactivatorExtension.class.getName());

    /**
     * TODO re-visit if we still need it
     * @param processAnnotatedType current process-annotated-type
     */
    public void filter(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        CodiStartupBroadcaster.broadcastStartup();

        filterInterceptors(processAnnotatedType);
        filterProjectStageClasses(processAnnotatedType);
    }

    protected void filterInterceptors(ProcessAnnotatedType processAnnotatedType)
    {
        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(Interceptor.class))
        {
            if(!isClassActivated(processAnnotatedType.getAnnotatedType().getJavaClass()))
            {
                processAnnotatedType.veto();
            }
        }
    }

    protected void filterProjectStageClasses(ProcessAnnotatedType processAnnotatedType)
    {
        if(!isClassActivated(ProjectStage.class))
        {
            return;
        }

        Class<?> beanClass = processAnnotatedType.getAnnotatedType().getJavaClass();
        if(ProjectStage.class.isAssignableFrom(beanClass) && !beanClass.isAnnotationPresent(Typed.class))
        {
            if(this.logger.isLoggable(Level.FINE))
            {
                this.logger.fine(beanClass.getName() + " is no normal CDI bean and it isn't annotated with @Typed()"
                      + " so an automatic veto has to be done. " +
                        "If there is a problem, it's possible to deactivate this automatic veto. " +
                        "In such a case please check the documentation or contact the community.");
            }

            processAnnotatedType.veto();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActivated()
    {
        return isClassActivated(getClass());
    }
}
