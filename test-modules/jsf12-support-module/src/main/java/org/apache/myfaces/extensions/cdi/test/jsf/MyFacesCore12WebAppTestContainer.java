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
package org.apache.myfaces.extensions.cdi.test.jsf;

import org.apache.myfaces.extensions.cdi.test.spi.WebAppTestContainer;

import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.apache.myfaces.test.mock.MockHttpServletResponse;
import org.apache.myfaces.test.mock.MockServletConfig;
import org.apache.myfaces.test.mock.MockHttpSession;
import org.apache.myfaces.test.mock.MockServletContext;
import org.apache.myfaces.test.mock.MockExternalContext;
import org.apache.myfaces.test.mock.MockFacesContext;
import org.apache.myfaces.test.mock.MockRenderKit;
import org.apache.myfaces.test.mock.MockApplicationFactory;
import org.apache.myfaces.test.mock.MockFacesContextFactory;
import org.apache.myfaces.test.mock.lifecycle.MockLifecycleFactory;
import org.apache.myfaces.test.mock.MockRenderKitFactory;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public class MyFacesCore12WebAppTestContainer implements WebAppTestContainer
{
    protected Application application = null;
    protected MockServletConfig config = null;
    protected ExternalContext externalContext = null;
    protected FacesContext facesContext = null;
    protected FacesContextFactory facesContextFactory = null;
    protected Lifecycle lifecycle = null;
    protected LifecycleFactory lifecycleFactory = null;
    protected RenderKit renderKit = null;
    protected MockHttpServletRequest request = null;
    protected MockHttpServletResponse response = null;
    protected MockServletContext servletContext = null;
    protected MockHttpSession session = null;

    // Thread context class loader saved and restored after each test
    private ClassLoader threadContextClassLoader = null;

    public void initEnvironment()
    {
        // Set up a new thread context class loader
        threadContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread()
                .setContextClassLoader(new URLClassLoader(new URL[0], this.getClass().getClassLoader()));
    }

    public void startContainer()
    {
        try
        {
            // Set up Servlet API Objects
            initServletObjects();

            // Set up JSF API Objects
            FactoryFinder.releaseFactories();

            initFactories();

            initJsfObjects();

            initDefaultView();

        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void stopContainer()
    {
        application = null;
        config = null;
        externalContext = null;
        lifecycle = null;
        lifecycleFactory = null;
        renderKit = null;
        request = null;
        response = null;
        servletContext = null;
        session = null;

        if (facesContext != null)
        {
            facesContext.release();
        }
        FactoryFinder.releaseFactories();

        Thread.currentThread().setContextClassLoader(threadContextClassLoader);
        threadContextClassLoader = null;
        facesContext = null;
    }

    protected void initJsfObjects() throws Exception
    {
        initExternalContext();
        initLifecycle();
        initFacesContext();
        initApplication();
        initRenderKit();
    }

    protected void initDefaultView()
    {
        UIViewRoot root = new UIViewRoot();
        root.setViewId("/viewId");
        root.setLocale(getLocale());
        root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
        facesContext.setViewRoot(root);
    }

    protected Locale getLocale()
    {
        return Locale.getDefault();
    }

    protected void initServletObjects() throws Exception
    {
        servletContext = new MockServletContext();
        config = new MockServletConfig(servletContext);
        session = new MockHttpSession();
        session.setServletContext(servletContext);
        request = new MockHttpServletRequest(session);
        request.setServletContext(servletContext);
        response = new MockHttpServletResponse();
    }

    protected void initFactories() throws Exception
    {
        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, MockFacesContextFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, MockLifecycleFactory.class.getName());
        FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, MockRenderKitFactory.class.getName());
    }

    protected void initExternalContext() throws Exception
    {
        externalContext = new MockExternalContext(servletContext, request, response);
    }

    protected void initLifecycle() throws Exception
    {
        lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    }

    protected void initFacesContext() throws Exception
    {
        facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        facesContext = facesContextFactory.getFacesContext(servletContext, request, response, lifecycle);
        if (facesContext.getExternalContext() != null)
        {
            externalContext = facesContext.getExternalContext();
        }
    }

    protected void initApplication() throws Exception
    {
        ApplicationFactory applicationFactory =
                (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        application = applicationFactory.getApplication();
        ((MockFacesContext) facesContext).setApplication(application);
    }

    protected void initRenderKit() throws Exception
    {
        RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder
                .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        renderKit = new MockRenderKit();
        renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT, renderKit);
    }
}
