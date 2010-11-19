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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Jakob Korherr
 */
@RunWith(JUnit4.class)
public class AbstractCodiTest
{

    // TODO provide initialisation for CODI tests here

    @Test
    public void homePage() throws Exception
    {
        WebClient webClient = new WebClient();

        HtmlPage page = webClient.getPage("http://localhost:8080/myfaces-extcdi-cargo-test/index.xhtml");

        final String pageAsText = page.asText();

        Assert.assertTrue(pageAsText.contains("jakobk - test"));

        webClient.closeAllWindows();
    }

}
