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
package org.apache.myfaces.extensions.cdi.scripting.impl;

import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.AbstractStartupObserver;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.logging.Level;

/**
 * Startup observer
 */
@ApplicationScoped
public class ScriptingModuleStartupObserver extends AbstractStartupObserver
{
    protected ScriptingModuleStartupObserver()
    {
    }

    protected void logScriptingModuleConfiguration(@Observes StartupEvent startupEvent)
    {
        if(!this.codiCoreConfig.isConfigurationLoggingEnabled())
        {
            return;
        }
        
        try
        {
            String moduleVersion = detectModuleVersion();

            //module info
            StringBuilder info = new StringBuilder("[Started] MyFaces CODI Scripting-Module");
            info.append(moduleVersion);
            info.append(separator);

            this.logger.info(info.toString());
        }
        //avoid that this log harms the startup
        catch (Exception e)
        {
            this.logger.log(Level.WARNING, "Scripting-Module couldn't log the current configuration." +
                                "Startup will continue!", e);
        }
    }


    protected String detectModuleVersion()
    {
        String version = ClassUtils.getJarVersion(ScriptingModuleStartupObserver.class);

        if(version != null && !version.startsWith("null"))
        {
            return " v" + version;
        }
        return "";
    }
}
