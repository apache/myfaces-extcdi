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


import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;

/**
 * <p>Produces {@link ProjectStage} configurations.</p>
 *
 * <p>The producer will try to detect the currently active ProjectStage on startup
 * and use that for all generated fields.</p>
 * <p>In case a JSF runtime is not available (e.g. in unit tests) we do all the
 * determining ourself (but in the same way as MyFaces does it!)</p>
 *
 * <p>Usage:</p>
 * Simply inject the current ProjectStage into any bean:
 * <pre>
 * public class MyBean {
 *   private @Inject ProjectStage projectStage;
 *
 *   public void fn() {
 *     if(projectStage == ProjectStage.Production) {
 *        // do some prodution stuff...
 *     }
 *   }
 * }
 * </pre>
 *
 * TODO move jsf specific parts
 */
@Typed()
public class ProjectStageProducer
{
    private final static String PROJECTSTAGE_PRODUCER_PROPERTY_KEY
            = "org.apache.myfaces.extensions.cdi.ProjectStageProducer";

    private static final String PROJECTSTAGE_PRODUCER_JNDI_NAME = "java:comp/env/myfaces-codi/ProjectStageProducer";

    private final static String PROJECTSTAGE_PROPERTY_KEY
            = "org.apache.myfaces.extensions.cdi.ProjectStage";

    private static final String PROJECTSTAGE_JNDI_NAME = "java:comp/env/myfaces-codi/ProjectStage";

    /**
     * ProjectStageProducers must only be created by subclassing producers
     */
    protected ProjectStageProducer()
    {
    }

    /**
     * The detected ProjectStage
     */
    private static ProjectStage projectStage;

    /**
     * for the singleton factory
     */
    private static ProjectStageProducer projectStageProducer;

    /**
     * We can only produce @Dependent scopes since an enum is final.
     * @return current ProjectStage
     */
    @Produces
    @Dependent
    @Default
    public ProjectStage getProjectStage()
    {
        if(projectStage == null)
        {
            synchronized (ProjectStageProducer.class)
            {
                projectStage = resolveProjectStage();
            }

            if(projectStage == null)
            {
                projectStage = ProjectStage.Production;
            }
        }
        return projectStage;
    }

    /**
     * <p>This factory method should only get used if there is absolutly no way
     * to get the current {@link ProjectStage} via &#064;Inject.</p>
     *
     * <p></p>
     *
     * @return the ProjectStageProducer instance.
     */
    public synchronized static ProjectStageProducer getInstance()
    {
        if (projectStageProducer == null)
        {
            projectStageProducer = CodiUtils.lookupFromEnvironment(
                    PROJECTSTAGE_PRODUCER_PROPERTY_KEY, PROJECTSTAGE_PRODUCER_JNDI_NAME, ProjectStageProducer.class);

            if(projectStageProducer == null)
            {
                //workaround to avoid the usage of a service loader
                projectStageProducer = ClassUtils.tryToInstantiateClassForName(
                        "org.apache.myfaces.extensions.cdi.jsf2.impl.projectstage.JsfProjectStageProducer",
                        ProjectStageProducer.class);
            }

            if (projectStageProducer == null)
            {
                // if we still didn't find a customised ProjectStageProducer,
                // then we take the default one.
                projectStageProducer = new ProjectStageProducer();
            }
            projectStageProducer.init();
        }

        return projectStageProducer;
    }

    /**
     * This function can be used to manually set the ProjectStage for the application.
     * This is e.g. useful in unit tests.
     * @param ps the ProjectStage to set
     */
    public static void setProjectStage(ProjectStage ps)
    {
        projectStage = ps;
    }

    private void init()
    {
        if (projectStage == null)
        {
            projectStage = resolveProjectStage();
        }

        // the last resort is setting it to Production
        if (projectStage == null)
        {
            projectStage = ProjectStage.Production;
        }
    }


    protected ProjectStage resolveProjectStage()
    {
        String stageName = CodiUtils
                .lookupFromEnvironment(PROJECTSTAGE_PROPERTY_KEY, PROJECTSTAGE_JNDI_NAME, String.class);

        if (stageName != null)
        {
            return ProjectStage.valueOf(stageName);
        }
        return null;
    }
}
