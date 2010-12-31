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
import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class CoreStartupObserver extends AbstractStartupObserver
{
    @Inject
    protected Logger logger;

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
            //module info
            StringBuilder info = new StringBuilder("[Started] MyFaces CODI (Extensions CDI) Core");
            info.append(getCodiCoreInformation());
            info.append(separator);
            info.append("project-stage: ");
            info.append(this.projectStage.toString());
            info.append(separator);
            info.append("project-stage class: ");
            info.append(this.projectStage.getClass().getName());
            info.append(separator);
            info.append(separator);

            //application info
            for(Map.Entry property : System.getProperties().entrySet())
            {
                if(property.getKey() instanceof String &&
                        ((String) property.getKey()).startsWith("org.apache.myfaces.extensions.cdi"))
                {
                    info.append("system-property-name:\t").append(property.getKey());
                    info.append(separator);

                    info.append("system-property-value:\t").append(property.getValue());
                    info.append(separator);
                    info.append(separator);
                }
            }

            for(Map.Entry<String, Serializable> contextParam : startupEvent.getApplicationParameters().entrySet())
            {
                info.append("param-name:\t\t").append(contextParam.getKey());
                info.append(separator);

                info.append("param-value:\t").append(contextParam.getValue());
                info.append(separator);
                info.append(separator);
            }

            //module config
            info.append(getConfigInfo(this.codiCoreConfig));

            this.logger.info(info.toString());
        }
        //avoid that this log harms the startup
        catch (Throwable t)
        {
            this.logger.warning("Core-Module couldn't log the current configuration." +
                                "Startup will continue!");
        }
    }

    public String getCodiCoreInformation()
    {
        if(CodiInformation.VERSION != null && !CodiInformation.VERSION.startsWith("null"))
        {
            return " v" + CodiInformation.VERSION;
        }
        return "";
    }
}
