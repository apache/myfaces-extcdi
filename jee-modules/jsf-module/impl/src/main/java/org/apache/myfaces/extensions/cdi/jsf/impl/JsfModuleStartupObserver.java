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
package org.apache.myfaces.extensions.cdi.jsf.impl;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.AbstractStartupObserver;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.jsf.api.config.JsfModuleConfig;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.inject.Inject;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class JsfModuleStartupObserver extends AbstractStartupObserver
{
    @Inject
    private JsfModuleConfig jsfModuleConfig;

    @Inject
    private WindowContextConfig windowContextConfig;

    @Inject
    private ConversationConfig conversationConfig;

    @Inject
    @Jsf
    private MessageContext messageContext;

    protected JsfModuleStartupObserver()
    {
    }

    protected void logJsfModuleConfiguration(@Observes StartupEvent startupEvent)
    {
        if(!this.codiCoreConfig.isConfigurationLoggingEnabled())
        {
            return;
        }
        
        try
        {
            String jsfModuleVersion = detectJsfModuleVersion();
            String jsfVersion = detectActiveJsfVersion();

            //module info
            StringBuilder info = new StringBuilder("[Started] MyFaces CODI JSF-Module ");
            info.append(jsfModuleVersion);
            info.append(separator);

            if(jsfVersion != null)
            {
                info.append(jsfVersion);
                info.append(separator);
            }
            info.append(separator);

            //module config
            info.append(getConfigInfo(this.jsfModuleConfig));
            info.append(getConfigInfo(this.windowContextConfig));
            info.append(getConfigInfo(this.conversationConfig));

            info.append(this.messageContext.config().toString()); //TODO

            this.logger.info(info.toString());
        }
        //avoid that this log harms the startup
        catch (Exception t)
        {
            this.logger.warning("JSF-Module couldn't log the current configuration." +
                                "Startup will continue!");
        }
    }

    protected String detectActiveJsfVersion()
    {
        //In JSF 1.2+ this artifact isn't wrapped by custom implementations (because it's deprecated)
        //-> usually it's the version of the implementation
        
        @SuppressWarnings({"deprecation"})
        PropertyResolver anyJsfClass = FacesContext.getCurrentInstance().getApplication().getPropertyResolver();

        if(anyJsfClass == null)
        {
            return null;
        }

        String version = ClassUtils.getJarVersion(anyJsfClass.getClass());

        String description = "Used JSF implementation: ";

        if(anyJsfClass.getClass().getName().startsWith("org.apache.myfaces"))
        {
            return description + "MyFaces Core v" + version;
        }
        else if(anyJsfClass.getClass().getName().startsWith("com.sun.faces"))
        {
            return description + "Mojarra v" + version;
        }
        return null;
    }

    protected String detectJsfModuleVersion()
    {
        String version = ClassUtils.getJarVersion(JsfModuleStartupObserver.class);

        if(version != null && !version.startsWith("null"))
        {
            return "v" + version + " for JSF " + getTargetJsfVersion();
        }
        return " for JSF " + getTargetJsfVersion();
    }

    protected String getTargetJsfVersion()
    {
        return "1.2";
    }
}
