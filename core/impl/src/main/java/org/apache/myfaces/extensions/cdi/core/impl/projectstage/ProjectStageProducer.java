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


import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
@ApplicationScoped
public class ProjectStageProducer
{

    /** JNDI path for the ProjectStage */
    public final static String PROJECT_STAGE_JNDI_NAME = "java:comp/env/jsf/ProjectStage";

    /** System Property to set the ProjectStage, if not present via the standard way */
    public final static String JSF_PROJECT_STAGE_SYSTEM_PROPERTY_NAME = "faces.PROJECT_STAGE";

    private final static String PROJECTSTAGE_PRODUCER_PROPERTY_KEY= "extcdi.projectStageProducer"; 

    private final static Logger log = Logger.getLogger(ProjectStageProducer.class.getName());

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
    private static ProjectStageProducer psp;

    /**
     * We can only produce @Dependent scopes since an enum is final.
     * @return current ProjectStage
     */
    @Produces @Dependent @Default
    public ProjectStage getProjectStage()
    {
        return projectStage;
    }

    /**
     * <p>This factory method should only get used if there is absolutly no way
     * to get the current {@link ProjectStage} via &#064;Inject.</p>
     *
     * <p></p>
     *
     * @return the ProjectStageProducer instance.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public synchronized static ProjectStageProducer getInstance()
            throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        if (psp == null)
        {
            //X TODO I'm sure there is a common way to get the current classloader in MyFaces...
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            InputStream is = cl.getResourceAsStream("/META-INF/extcdi/extcdi.properties");
            if (is != null)
            {
                Properties props = new Properties();
                props.load(is);
                String pspClassName = props.getProperty(PROJECTSTAGE_PRODUCER_PROPERTY_KEY);
                if (pspClassName != null && pspClassName.length() > 0)
                {
                    Class<ProjectStageProducer> pspClass = (Class<ProjectStageProducer>) Class.forName(pspClassName);
                    psp = pspClass.newInstance();
                }
            }

            if (psp == null)
            {
                // if we still didn't find a customised ProjectStageProducer,
                // then we take the default one.
                psp = new ProjectStageProducer();
            }
        }

        return psp;
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

    /**
     * Read the configuration from the stated places.
     * This can be overloaded to implement own lookup mechanisms.
     *
     * This will only determine the ProjectStage if it is not yet set.
     */
    @Inject
    public void determineProjectStage()
    {
        if (projectStage == null)
        {
            projectStage = determineCustomProjectStage();
        }

        if (projectStage == null)
        {
            projectStage = getProjectStageFromJNDI();
        }

        if (projectStage == null)
        {
            projectStage = getProjectStageFromEnvironment();
        }

        // the last resort is setting it to Production
        if (projectStage == null)
        {
            projectStage = ProjectStage.Production;
        }
    }


    /**
     * This can get used to provide additional ProjectStage
     * lookup mechanisms.
     * @return the detected {@link ProjectStage} or <code>null</code> if non was found.
     */
    protected ProjectStage determineCustomProjectStage()
    {
        return null;
    }

    protected ProjectStage getProjectStageFromEnvironment()
    {
        String stageName = System.getProperty(JSF_PROJECT_STAGE_SYSTEM_PROPERTY_NAME);

        if (stageName != null)
        {
            return ProjectStage.valueOf(stageName);
        }
        return null;
    }

    protected ProjectStage getProjectStageFromJNDI()
    {
        ProjectStage ps = null;
        String stageName = null;
        try
        {
            Context ctx = new InitialContext();
            Object temp = ctx.lookup(PROJECT_STAGE_JNDI_NAME);
            if (temp != null)
            {
                if (temp instanceof String)
                {
                    stageName = (String) temp;
                }
                else
                {
                    log.log(Level.SEVERE,
                            "JNDI lookup for key " + PROJECT_STAGE_JNDI_NAME
                            + " should return a java.lang.String value");
                    return null;
                }
            }
        }
        catch (NamingException e)
        {
            // no-op
        }

        if (stageName != null)
        {
            ps = ProjectStage.valueOf(stageName);
        }

        return ps;
    }

}
