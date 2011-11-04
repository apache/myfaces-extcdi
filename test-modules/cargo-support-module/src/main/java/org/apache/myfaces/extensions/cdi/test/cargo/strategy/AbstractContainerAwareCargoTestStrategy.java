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
package org.apache.myfaces.extensions.cdi.test.cargo.strategy;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.myfaces.extensions.cdi.test.cargo.TestConfiguration;
import org.apache.myfaces.extensions.cdi.test.strategy.AbstractJsfAwareTestStrategy;

import javax.enterprise.inject.Typed;

/**
 * Defines the basic env. for Cargo tests for JSF applications
 */
//TODO cleanup
@Typed()
public abstract class AbstractContainerAwareCargoTestStrategy extends AbstractJsfAwareTestStrategy
{
    private static final String CARGO_CONTEXT_PROPERTY = "cargo.context";
    private static final String CARGO_PORT_PROPERTY = "cargo.port";

    private static final String DEFAULT_CONTEXT = "cargo-test";
    private static final String DEFAULT_PORT = "8080";

    protected static String baseUrl = null;

    protected WebClient webClient;

    protected TestConfiguration getTestConfiguration()
    {
        return new TestConfiguration()
        {
            public WebClient getWebClient()
            {
                return webClient;
            }

            public String getBaseURL()
            {
                initBaseURL();
                return baseUrl;
            }

            public boolean isCheckWindowId()
            {
                return true;
            }
        };
    }

    protected void initBaseURL()
    {
        if(baseUrl != null)
        {
            return;
        }

        baseUrl = getCustomBaseURL();

        if(baseUrl != null)
        {
            return;
        }

        String port = System.getProperty(CARGO_PORT_PROPERTY);
        if (port == null)
        {
            port = DEFAULT_PORT;
        }

        String context = System.getProperty(CARGO_CONTEXT_PROPERTY);
        if (context == null)
        {
            context = DEFAULT_CONTEXT;
        }

        baseUrl = "http://localhost:" + port + "/" + context + "/";
    }

    protected String getCustomBaseURL()
    {
        //override if needed
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //@Before
    public void before()
    {
        super.before();

        webClient = new WebClient(getBrowserVersion());
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //@After
    public void after()
    {
        super.after();

        webClient.closeAllWindows();
        webClient = null;
    }

    /**
     * Returns the Browser to use.
     * Default is Firefox 3.6, override to change this.
     *
     * @return default browser
     */
    public BrowserVersion getBrowserVersion()
    {
        return BrowserVersion.FIREFOX_3_6;
    }
}
