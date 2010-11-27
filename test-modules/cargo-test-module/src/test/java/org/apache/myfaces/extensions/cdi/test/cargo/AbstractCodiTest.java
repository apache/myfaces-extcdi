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
package org.apache.myfaces.extensions.cdi.test.cargo;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Base class for all CODI integration tests using Cargo and HtmlUnit.
 *
 * @author Jakob Korherr
 */
public abstract class AbstractCodiTest
{

    private static final String CARGO_CONTEXT_PROPERTY = "cargo.context";
    private static final String CARGO_PORT_PROPERTY = "cargo.port";

    private static final String DEFAULT_CONTEXT = "myfaces-extcdi-cargo-test";
    private static final String DEFAULT_PORT = "8787";

    public static final String BASE_URL;
    static
    {
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

        BASE_URL = "http://localhost:" + port + "/" + context + "/";
    }


    protected WebClient webClient;

    @Before
    public void setUp() throws Exception
    {
        webClient = new WebClient(getBrowserVersion());
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

    @After
    public void tearDown() throws Exception
    {
        webClient.closeAllWindows();
        webClient = null;
    }

    /**
     * Returns the Browser to use.
     * Default is Firefox 3.6, override to change this.
     *
     * @return
     */
    public BrowserVersion getBrowserVersion()
    {
        return BrowserVersion.FIREFOX_3_6;
    }
    
}
