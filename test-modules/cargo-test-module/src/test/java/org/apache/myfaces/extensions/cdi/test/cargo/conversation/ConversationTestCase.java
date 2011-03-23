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

import org.apache.myfaces.extensions.cdi.test.cargo.SimplePageInteraction;
import org.apache.myfaces.extensions.cdi.test.cargo.runner.JUnit4WithCargo;
import org.apache.myfaces.extensions.cdi.test.cargo.view.config.Pages;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Typed;

/**
 * @author Jakob Korherr
 */
@RunWith(JUnit4WithCargo.class)
@Typed()
public class ConversationTestCase extends BaseConversationTest
{

    // NOTE that new @Test means new WebClient means new WindowContext

    @Test
    public void testConversationDialog() throws Exception
    {
        SimplePageInteraction pageInteraction = new SimplePageInteraction(getTestConfiguration())
                .with(Pages.Conversation.Conversation1.class)
                .with(Pages.Conversation.Conversation2.class)
                .with(Pages.Conversation.Conversation3.class)
                .with(Pages.Conversation.Result.class)
                .start(Pages.Conversation.Conversation1.class)
                .useForm("conversation1");

        pageInteraction.setValue("conversation1:value1", "1");
        pageInteraction
                .click("conversation1:nextPage")
                .checkState(Pages.Conversation.Conversation2.class)
                .useForm("conversation2");

        pageInteraction.setValue("conversation2:value2", "2")
                .click("conversation2:nextPage")
                .checkState(Pages.Conversation.Conversation3.class)
                .useForm("conversation3");

        pageInteraction.setValue("conversation3:value3", "3")
                .click("conversation3:submit")
                .checkState(Pages.Conversation.Result.class);

        pageInteraction.checkTextValue("value1", "1");
        pageInteraction.checkTextValue("value2", "2");
        pageInteraction.checkTextValue("value3", "3");

        pageInteraction.click("refresh")
                .checkState(Pages.Conversation.Result.class);

        pageInteraction.checkTextValue("value1", "1");
        pageInteraction.checkTextValue("value2", "2");
        pageInteraction.checkTextValue("value3", "3");

        pageInteraction.click("back")
                .checkState(Pages.Conversation.Conversation3.class)
                .useForm("conversation3");

        pageInteraction.click("conversation3:back")
                .checkState(Pages.Conversation.Conversation2.class)
                .useForm("conversation2");

        pageInteraction.setValue("conversation2:value2", "new2")
                .click("conversation2:nextPage")
                .checkState(Pages.Conversation.Conversation3.class)
                .useForm("conversation3");

        pageInteraction.setValue("conversation3:value3", "new3")
                .click("conversation3:submit")
                .checkState(Pages.Conversation.Result.class);

        pageInteraction.checkTextValue("value1", "1");
        pageInteraction.checkTextValue("value2", "new2");
        pageInteraction.checkTextValue("value3", "new3");

        // close conversation
        pageInteraction.useForm("form").click("form:closeConversation");

        pageInteraction.checkTextValue("value1", "");
        pageInteraction.checkTextValue("value2", "");
        pageInteraction.checkTextValue("value3", "");
    }

    @Test
    public void testConversationDialogRestart() throws Exception
    {
        SimplePageInteraction pageInteraction = new SimplePageInteraction(getTestConfiguration())
                .with(Pages.Conversation.Conversation1.class)
                .with(Pages.Conversation.Conversation2.class)
                .with(Pages.Conversation.Conversation3.class)
                .with(Pages.Conversation.Result.class)
                .start(Pages.Conversation.Conversation1.class)
                .useForm("conversation1");

        pageInteraction.setValue("conversation1:value1", "1")
                .click("conversation1:nextPage")
                .checkState(Pages.Conversation.Conversation2.class)
                .useForm("conversation2");

        pageInteraction.setValue("conversation2:value2", "2")
                .click("conversation2:nextPage")
                .checkState(Pages.Conversation.Conversation3.class)
                .useForm("conversation3");

        pageInteraction.setValue("conversation3:value3", "3")
                .click("conversation3:submit")
                .checkState(Pages.Conversation.Result.class);

        pageInteraction.checkTextValue("value1", "1");
        pageInteraction.checkTextValue("value2", "2");
        pageInteraction.checkTextValue("value3", "3");

        pageInteraction.useForm("form").click("form:restartConversation");

        pageInteraction.checkTextValue("value1", "");
        pageInteraction.checkTextValue("value2", "");
        pageInteraction.checkTextValue("value3", "");
    }
}
