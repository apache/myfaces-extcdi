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
package org.apache.myfaces.extensions.cdi.jsf.impl.projectstage;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.inject.Typed;
import java.util.logging.Level;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class JsfProjectStageProducer extends ProjectStageProducer
{
    private static final long serialVersionUID = 2378537865206165557L;

    /** web.xml Property to set the ProjectStage */
    private static final String JSF_PROJECT_STAGE_CONFIG_PROPERTY_NAME = "javax.faces.PROJECT_STAGE";

    /** System Property to set the ProjectStage, if not present via the standard way */
    private static final String JSF_PROJECT_STAGE_SYSTEM_PROPERTY_NAME = "faces.PROJECT_STAGE";

    /** JNDI path for the ProjectStage */
    private static final String PROJECT_STAGE_JNDI_NAME = "java:comp/env/jsf/ProjectStage";

    @Override
    protected ProjectStage resolveProjectStage()
    {
        // we first try to resolve the JSF standard configuration settings.
        // this is needed to comply with the JSF spec if JSF is used

        //web.xml support isn't covert by the default implementations
        String stageName = CodiUtils.lookupFromEnvironment(JSF_PROJECT_STAGE_CONFIG_PROPERTY_NAME, String.class);

        if(stageName == null)
        {
            stageName = CodiUtils.lookupFromEnvironment(JSF_PROJECT_STAGE_SYSTEM_PROPERTY_NAME, String.class);
        }

        if(stageName == null)
        {
            stageName = CodiUtils.lookupFromEnvironment(PROJECT_STAGE_JNDI_NAME, String.class);
        }

        if(stageName != null)
        {
            Class jsfProjectStageClass = ClassUtils.tryToLoadClassForName("javax.faces.application.ProjectStage");

            if(jsfProjectStageClass == null && LOG.isLoggable(Level.WARNING))
            {
                LOG.warning("a jsf2 project stage is used but jsf2 isn't in the classpath");
            }

            //check if the jsf project-stage should be ignored
            if(jsfProjectStageClass != null && !ClassDeactivation.isClassActivated(jsfProjectStageClass))
            {
                stageName = null;
            }
        }

        if (stageName != null)
        {
            return ProjectStage.valueOf(stageName);
        }

        return super.resolveProjectStage();
    }
}
