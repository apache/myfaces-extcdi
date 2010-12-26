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
import org.apache.webbeans.cditest.owb.MockHttpSession;
import org.apache.webbeans.cditest.owb.MockServletContext;
import org.apache.webbeans.context.ContextFactory;
import org.apache.webbeans.el.ELContextStore;
import org.apache.webbeans.lifecycle.StandaloneLifeCycle;

import javax.servlet.ServletRequestEvent;

/**
 * @author Gerhard Petracek
 */
class HybridStandaloneLifeCycle extends StandaloneLifeCycle
{
    private MockServletContext servletContext;
    private MockHttpSession session;

    private MockHttpServletRequest request;

    public HybridStandaloneLifeCycle(MockServletContext servletContext,
                                      MockHttpSession session,
                                      MockHttpServletRequest request)
    {
        this.servletContext = servletContext;
        this.session = session;
        this.request = request;
    }

    @Override
    public void beforeStartApplication(Object object)
    {
        ContextFactory.initRequestContext(new ServletRequestEvent(this.servletContext, this.request));
        ContextFactory.initSessionContext(this.session);
        ContextFactory.initConversationContext(null);
        ContextFactory.initApplicationContext(this.servletContext);
        ContextFactory.initSingletonContext(this.servletContext);
    }

    @Override
    public void beforeStopApplication(Object endObject)
    {
        ContextFactory.destroyRequestContext(new ServletRequestEvent(this.servletContext, this.request));
        ContextFactory.destroySessionContext(this.session);
        ContextFactory.destroyConversationContext();
        ContextFactory.destroyApplicationContext(this.servletContext);
        ContextFactory.destroySingletonContext(this.servletContext);

        ContextFactory.cleanUpContextFactory();

        // clean up the EL caches after each request
        ELContextStore elStore = ELContextStore.getInstance(false);
        if (elStore != null)
        {
            elStore.destroyELContextStore();
        }
    }
}
