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
package org.apache.myfaces.extensions.cdi.test.junit4;

import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.apache.webbeans.cditest.CdiTestContainer;
import org.apache.webbeans.cditest.owb.MockHttpSession;
import org.apache.webbeans.cditest.owb.MockServletContext;
import org.apache.webbeans.context.ContextFactory;
import org.apache.webbeans.spi.ContainerLifecycle;

import javax.enterprise.inject.ResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletRequestEvent;
import java.lang.annotation.Annotation;

/**
 * Allows JUnit tests in a web-app project
 *
 * @author Gerhard Petracek
 */
class HybridCdiTestOpenWebBeansContainer implements CdiTestContainer
{
    private ContainerLifecycle lifecycle = null;
    private MockServletContext servletContext = null;
    private MockHttpSession session = null;
    private MockHttpServletRequest request;

    public void bootContainer() throws Exception
    {
        this.servletContext = new MockServletContext();
        this.session = new MockHttpSession();
        this.request = new MockHttpServletRequest(this.session);
        this.lifecycle = new HybridStandaloneLifeCycle(this.servletContext, this.session, this.request);
        this.lifecycle.startApplication(this.servletContext);
    }

    public void shutdownContainer() throws Exception
    {
        if (this.lifecycle != null)
        {
            this.lifecycle.stopApplication(this.servletContext);
        }
    }

    public void startContexts() throws Exception
    {
        ContextFactory.initSingletonContext(this.servletContext);
        ContextFactory.initApplicationContext(this.servletContext);
        ContextFactory.initSessionContext(this.session);
        ContextFactory.initConversationContext(null);
        ContextFactory.initRequestContext(new ServletRequestEvent(this.servletContext, this.request));
    }

    public void startApplicationScope() throws Exception
    {
        ContextFactory.initApplicationContext(this.servletContext);
    }

    public void startConversationScope() throws Exception
    {
    }

    public void startCustomScope(Class<? extends Annotation> scopeClass) throws Exception
    {
    }

    public void startRequestScope() throws Exception
    {
        ContextFactory.initRequestContext(new ServletRequestEvent(this.servletContext, this.request));
    }

    public void startSessionScope() throws Exception
    {
        ContextFactory.initSessionContext(this.session);
    }

    public void stopContexts() throws Exception
    {
        ContextFactory.destroyRequestContext(new ServletRequestEvent(this.servletContext, this.request));
        ContextFactory.destroyConversationContext();
        ContextFactory.destroySessionContext(this.session);
        ContextFactory.destroyApplicationContext(this.servletContext);
        ContextFactory.destroySingletonContext(this.servletContext);

        ContextFactory.cleanUpContextFactory();
    }

    public void stopApplicationScope() throws Exception
    {
        ContextFactory.destroyApplicationContext(this.servletContext);
    }

    public void stopConversationScope() throws Exception
    {
    }

    public void stopCustomScope(Class<? extends Annotation> scopeClass) throws Exception
    {
    }

    public void stopRequestScope() throws Exception
    {
        ContextFactory.destroyRequestContext(new ServletRequestEvent(this.servletContext, this.request));
    }

    public void stopSessionScope() throws Exception
    {
        ContextFactory.destroySessionContext(this.session);
    }

    public BeanManager getBeanManager()
    {
        return this.lifecycle.getBeanManager();
    }

    public <T> T getInstance(Class<T> type, Annotation... qualifiers)
            throws ResolutionException
    {
        return null;
    }

    public Object getInstance(String name)
            throws ResolutionException
    {
        return null;
    }
}
