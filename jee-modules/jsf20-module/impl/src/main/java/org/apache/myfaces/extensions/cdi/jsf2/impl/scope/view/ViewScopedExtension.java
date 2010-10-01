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
package org.apache.myfaces.extensions.cdi.jsf2.impl.scope.view;


import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.utils.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.faces.bean.ViewScoped;


/**
 * <p>This CDI extension enables support for the new JSF-2 &#064;ViewScoped annotation.</p>
 *
 * <p>2 steps are necessary:</p>
 * <ol>
 * <li>We have to manually add the &#064;ViewScoped annotation
 * as a passivating {@link javax.enterprise.context.NormalScope},
 * since this annotation is neither a JSR-330 annotation
 * nor a JSR-299 annotation and therefor doesn't get picked up automatically. This has to be
 * before the bean scanning starts.</li>
 * <li>After the bean scanning succeeded, we register the
 *  {@link javax.enterprise.context.spi.Context} for the
 * ViewScoped. The {@link ViewScopedContext} is responsible for actually storing all our
 * &#064;ViewScoped contextual instances in the JSF ViewMap.</li>
 * </ol>
 *
 * <p>The extension automatically detects if we are in the
 * {@link ProjectStage#UnitTest} and uses
 * </p>
 */
public class ViewScopedExtension implements Extension, Deactivatable
{
    public void addViewScoped(@Observes BeforeBeanDiscovery beforeBeanDiscovery)
    {
        if(!isActivated())
        {
            return;
        }

        beforeBeanDiscovery.addScope(ViewScoped.class, true, true);
    }

    public void registerViewContext(@Observes AfterBeanDiscovery afterBeanDiscovery)
    {
        if(!isActivated())
        {
            return;
        }

        // we need to do this manually because there is no dependency injection in place
        // at this time
        ProjectStageProducer psp = null;
        try
        {
            psp = ProjectStageProducer.getInstance();
        }
        catch (Exception e)
        {
            if (!(e instanceof RuntimeException))
            {
                throw new RuntimeException(e);
            }
        }
        psp.determineProjectStage();
        ProjectStage projectStage = psp.getProjectStage();

        if (projectStage == ProjectStage.UnitTest)
        {
            // for unit tests, we use the mock context
            afterBeanDiscovery.addContext(new MockViewScopedContext());
        }
        else
        {
            // otherwise we use the real JSF ViewMap context
            afterBeanDiscovery.addContext(new ViewScopedContext());
        }
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}