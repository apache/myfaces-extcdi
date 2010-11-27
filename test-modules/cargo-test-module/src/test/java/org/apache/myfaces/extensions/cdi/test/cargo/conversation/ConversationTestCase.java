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
package org.apache.myfaces.extensions.cdi.test.cargo.conversation;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.myfaces.extensions.cdi.test.cargo.AbstractCodiTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Jakob Korherr
 */
@RunWith(JUnit4.class)
public class ConversationTestCase extends AbstractCodiTest
{

    // NOTE that new @Test means new WebClient means new WindowContext

    @Test
    public void testConversationDialog() throws Exception
    {
        HtmlPage page = webClient.getPage(BASE_URL + "conversation/conversation1.xhtml");

        // page 1
        HtmlForm form = page.getFormByName("conversation1");
        form.getInputByName("conversation1:value1").setValueAttribute("1");
        page = form.getInputByName("conversation1:nextPage").click();

        // page 2
        form = page.getFormByName("conversation2");
        form.getInputByName("conversation2:value2").setValueAttribute("2");
        page = form.getInputByName("conversation2:nextPage").click();

        // page 3
        form = page.getFormByName("conversation3");
        form.getInputByName("conversation3:value3").setValueAttribute("3");
        page = form.getInputByName("conversation3:submit").click();

        // result page
        Assert.assertEquals("1", page.getElementById("value1").getTextContent());
        Assert.assertEquals("2", page.getElementById("value2").getTextContent());
        Assert.assertEquals("3", page.getElementById("value3").getTextContent());

        // GET request
        page = page.getElementById("refresh").click();

        Assert.assertEquals("1", page.getElementById("value1").getTextContent());
        Assert.assertEquals("2", page.getElementById("value2").getTextContent());
        Assert.assertEquals("3", page.getElementById("value3").getTextContent());

        // GET request to page 3
        page = page.getElementById("back").click();

        // POST request to page 2
        page = page.getElementById("conversation3:back").click();

        // page 2
        form = page.getFormByName("conversation2");
        form.getInputByName("conversation2:value2").setValueAttribute("new2");
        page = form.getInputByName("conversation2:nextPage").click();

        // page 3
        form = page.getFormByName("conversation3");
        form.getInputByName("conversation3:value3").setValueAttribute("new3");
        page = form.getInputByName("conversation3:submit").click();

        // result page
        Assert.assertEquals("1", page.getElementById("value1").getTextContent());
        Assert.assertEquals("new2", page.getElementById("value2").getTextContent());
        Assert.assertEquals("new3", page.getElementById("value3").getTextContent());

        // close conversation
        form = page.getFormByName("form");
        page = form.getInputByName("form:closeConversation").click();

        // values must be empty
        Assert.assertEquals("", page.getElementById("value1").getTextContent());
        Assert.assertEquals("", page.getElementById("value2").getTextContent());
        Assert.assertEquals("", page.getElementById("value3").getTextContent());
    }

    @Test
    public void testConversationDialogRestart() throws Exception
    {
        HtmlPage page = webClient.getPage(BASE_URL + "conversation/conversation1.xhtml");

        // page 1
        HtmlForm form = page.getFormByName("conversation1");
        form.getInputByName("conversation1:value1").setValueAttribute("1");
        page = form.getInputByName("conversation1:nextPage").click();

        // page 2
        form = page.getFormByName("conversation2");
        form.getInputByName("conversation2:value2").setValueAttribute("2");
        page = form.getInputByName("conversation2:nextPage").click();

        // page 3
        form = page.getFormByName("conversation3");
        form.getInputByName("conversation3:value3").setValueAttribute("3");
        page = form.getInputByName("conversation3:submit").click();

        // result page
        Assert.assertEquals("1", page.getElementById("value1").getTextContent());
        Assert.assertEquals("2", page.getElementById("value2").getTextContent());
        Assert.assertEquals("3", page.getElementById("value3").getTextContent());

        // restart conversation
        form = page.getFormByName("form");
        page = form.getInputByName("form:restartConversation").click();

        // values must be empty
        Assert.assertEquals("", page.getElementById("value1").getTextContent());
        Assert.assertEquals("", page.getElementById("value2").getTextContent());
        Assert.assertEquals("", page.getElementById("value3").getTextContent());
    }

}
