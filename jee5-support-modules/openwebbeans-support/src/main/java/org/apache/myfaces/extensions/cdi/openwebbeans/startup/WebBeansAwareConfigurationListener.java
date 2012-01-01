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
package org.apache.myfaces.extensions.cdi.openwebbeans.startup;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.activation.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEventBroadcaster;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.webbeans.servlet.WebBeansConfigurationListener;
import org.apache.webbeans.spi.ContainerLifecycle;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Allows to bootstrap the JavaEE5 + OpenWebBeans environment in a controlled manner.
 */
@InvocationOrder(1)
public class WebBeansAwareConfigurationListener extends WebBeansConfigurationListener
        implements StartupEventBroadcaster, Deactivatable
{
    protected final Logger logger = Logger.getLogger(getClass().getName());

    protected static Map<ClassLoader, Boolean> initialized
            = new ConcurrentHashMap<ClassLoader, Boolean>();

    protected static Map<ClassLoader, ContainerLifecycle> storedContainerLifecycle
            = new ConcurrentHashMap<ClassLoader, ContainerLifecycle>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        if (!isInitialized())
        {
            this.logger.info("Controlled OpenWebBeans bootstrapping.");

            super.contextInitialized(event);

            storeContainerLifecycle();

            markAsInitialized();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestInitialized(ServletRequestEvent event)
    {
        storeContainerLifecycle();
        super.requestInitialized(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
        storeContainerLifecycle();
        super.contextDestroyed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDestroyed(ServletRequestEvent event)
    {
        storeContainerLifecycle();
        super.requestDestroyed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionCreated(HttpSessionEvent event)
    {
        storeContainerLifecycle();
        super.sessionCreated(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event)
    {
        storeContainerLifecycle();
        super.sessionDestroyed(event);
    }

    /**
     * {@inheritDoc}
     */
    public void broadcastStartup()
    {
        if(!isActivated())
        {
            return;
        }
        
        if (isInitialized())
        {
            return;
        }

        logger.info("Controlled MyFaces ExtCDI bootstrapping.");

        //In this case the JSF impl has been invoked too soon

        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext != null && facesContext.getExternalContext() != null)
        {
            ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();

            //force bootstrapping of OWB
            contextInitialized(new ServletContextEvent(servletContext));
        }
        markAsInitialized();
    }

    protected void markAsInitialized()
    {
        initialized.put(getClassLoader(), Boolean.TRUE);
    }

    protected boolean isInitialized()
    {
        ClassLoader classLoader = getClassLoader();

        return Boolean.TRUE.equals(initialized.get(classLoader));
    }

    protected void storeContainerLifecycle()
    {
        ClassLoader classLoader = getClassLoader();
        if (this.lifeCycle != null)
        {
            storedContainerLifecycle.put(classLoader, this.lifeCycle);
        }
        else
        {
            this.lifeCycle = storedContainerLifecycle.get(classLoader);
        }
    }

    private ClassLoader getClassLoader()
    {
        return ClassUtils.getClassLoader(null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
