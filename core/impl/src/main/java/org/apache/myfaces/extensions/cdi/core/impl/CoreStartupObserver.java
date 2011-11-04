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

import org.apache.myfaces.extensions.cdi.core.api.CodiInformation;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;

/**
 * Startup observer
 */
@ApplicationScoped
public class CoreStartupObserver extends AbstractStartupObserver
{
    @Inject
    protected ProjectStage projectStage;

    protected CoreStartupObserver()
    {
    }

    protected void logCoreConfiguration(@Observes StartupEvent startupEvent)
    {
        if(!this.codiCoreConfig.isConfigurationLoggingEnabled())
        {
            return;
        }

        try
        {
            String cdiVersion = detectActiveCdiVersion();

            //module info
            StringBuilder info = new StringBuilder("[Started] MyFaces CODI (Extensions CDI) Core");
            info.append(getCodiCoreInformation());
            info.append(separator);

            if(cdiVersion != null)
            {
                info.append(cdiVersion);
                info.append(separator);
            }
            info.append(separator);

            info.append("project-stage: ");
            info.append(this.projectStage.toString());
            info.append(separator);
            info.append("project-stage class: ");
            info.append(this.projectStage.getClass().getName());
            info.append(separator);
            info.append(separator);

            //application info
            String systemProperties = getSystemPropertiesForCodi();

            if(systemProperties != null)
            {
                info.append("system-properties:");
                info.append(separator);
                info.append(systemProperties);
            }


            String applicationParameters = getApplicationParameters(startupEvent);

            if(applicationParameters != null)
            {
                info.append("application-parameters:");
                info.append(separator);
                info.append(applicationParameters);
            }

            //module config
            info.append(getConfigInfo(this.codiCoreConfig));

            this.logger.info(info.toString());
        }
        //avoid that this log harms the startup
        catch (Exception e)
        {
            this.logger.log(Level.WARNING, "Core-Module couldn't log the current configuration." +
                                "Startup will continue!", e);
        }
    }

    private String getSystemPropertiesForCodi()
    {
        StringBuilder info = new StringBuilder("");
        for(Map.Entry property : System.getProperties().entrySet())
        {
            if(property.getKey() instanceof String &&
                    ((String) property.getKey()).startsWith("org.apache.myfaces.extensions.cdi"))
            {
                info.append("   name:\t").append(property.getKey());
                info.append(separator);

                info.append("   value:\t").append(property.getValue());
                info.append(separator);
                info.append(separator);
            }
        }
        String result = info.toString();

        if("".equals(result))
        {
            return null;
        }
        return result;
    }

    private String getApplicationParameters(StartupEvent startupEvent)
    {
        StringBuilder info = new StringBuilder("");
        for(Map.Entry<String, Serializable> contextParam : startupEvent.getApplicationParameters().entrySet())
        {
            info.append("   name:\t").append(contextParam.getKey());
            info.append(separator);

            info.append("   value:\t").append(contextParam.getValue());
            info.append(separator);
            info.append(separator);
        }

        String result = info.toString();

        if("".equals(result))
        {
            return null;
        }
        return result;
    }

    private String detectActiveCdiVersion()
    {
        BeanManager cdiClass = BeanManagerProvider.getInstance().getBeanManager();

        if(cdiClass == null)
        {
            return null;
        }

        String version = ClassUtils.getJarVersion(cdiClass.getClass());

        String description = "Used CDI implementation: ";

        if(cdiClass.getClass().getName().startsWith("org.apache"))
        {
            return description + "OpenWebBeans v" + version;
        }
        else if(cdiClass.getClass().getName().startsWith("org.jboss"))
        {
            return description + "Weld v" + version;
        }
        return null;
    }

    private String getCodiCoreInformation()
    {
        String version = CodiInformation.VERSION;
        String revision;

        if(version != null && !version.startsWith("null"))
        {
            revision = CodiInformation.REVISION;

            if(version.endsWith("-SNAPSHOT") && revision != null && !revision.startsWith("null"))
            {
                version = version.replace("-SNAPSHOT", " r");
            }
            else
            {
                revision = "";
            }

            return " v" + version + revision;
        }
        return "";
    }
}
