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
package org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.NavigationMode;

/**
 * @author Gerhard Petracek
 */
public class ViewConfigTest
{
    private TestableViewConfigExtension viewConfigExtension = new TestableViewConfigExtension();

    @Test
    public void testSimpleCase()
    {
        viewConfigExtension.addPageDefinition(SimpleView.class);

        assertEquals(ViewConfigCache.getViewDefinition(SimpleView.class).getViewId(), "/simpleView.xhtml");
    }

    @Test
    public void testSimpleManualCase()
    {
        viewConfigExtension.addPageDefinition(SimpleViewWithManualName.class);

        assertEquals(ViewConfigCache.getViewDefinition(SimpleViewWithManualName.class).getViewId(),
                                                       "/simpleManualPage.xhtml");
    }

    @Test
    public void testSimpleNestedCase()
    {
        viewConfigExtension.addPageDefinition(SimpleNestedViewConfig.Page1.class);
        viewConfigExtension.addPageDefinition(SimpleNestedViewConfig.Page2.class);

        assertEquals(ViewConfigCache.getViewDefinition(SimpleNestedViewConfig.Page1.class).getViewId(),
                                                       "/simpleNestedViewConfig/page1.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(SimpleNestedViewConfig.Page2.class).getViewId(),
                                                       "/simpleNestedViewConfig/page2.xhtml");
    }

    @Test
    public void testNestedViewConfigWithManualNamesAndSharedBasePath1()
    {
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath1.Page1.class);
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath1.Page2.class);

        assertEquals(ViewConfigCache.getViewDefinition(
                NestedViewConfigWithManualNamesAndSharedBasePath1.Page1.class).getViewId(),
                "/manual/page1.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(
                NestedViewConfigWithManualNamesAndSharedBasePath1.Page2.class).getViewId(),
                "/manual/page2.xhtml");
    }

    @Test
    public void testNestedViewConfigWithManualNamesAndSharedBasePath2()
    {
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath2.Page1.class);
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath2.Page2.class);

        assertEquals(ViewConfigCache.getViewDefinition(
                NestedViewConfigWithManualNamesAndSharedBasePath2.Page1.class).getViewId(),
                "/manual/page3.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(
                NestedViewConfigWithManualNamesAndSharedBasePath2.Page2.class).getViewId(),
                "/manual/page4.xhtml");
    }

    @Test
    public void testVirtualNesting()
    {
        viewConfigExtension.addPageDefinition(VirtualNesting.Page1.class);
        viewConfigExtension.addPageDefinition(VirtualNesting.Page2.class);

        assertEquals(ViewConfigCache.getViewDefinition(VirtualNesting.Page1.class).getViewId(),
                                                       "/page1.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(VirtualNesting.Page2.class).getViewId(),
                                                       "/page2.xhtml");
    }

    @Test
    public void testMultipleSimpleNestedConfigs()
    {
        viewConfigExtension.addPageDefinition(MultipleSimpleNestedConfigs.Conversations.Grouped.Step1.class);
        viewConfigExtension.addPageDefinition(MultipleSimpleNestedConfigs.Conversations.Grouped.Step2.class);

        assertEquals(ViewConfigCache.getViewDefinition(
                MultipleSimpleNestedConfigs.Conversations.Grouped.Step1.class).getViewId(),
                "/multipleSimpleNestedConfigs/conversations/grouped/step1.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(
                MultipleSimpleNestedConfigs.Conversations.Grouped.Step2.class).getViewId(),
                "/multipleSimpleNestedConfigs/conversations/grouped/step2.xhtml");
    }

    @Test
    public void testNavigationOverriding1()
    {
        viewConfigExtension.addPageDefinition(NavigationOverriding1.RedirectedPage1.class);
        viewConfigExtension.addPageDefinition(NavigationOverriding1.ForwardedPage1.class);

        assertEquals(ViewConfigCache.getViewDefinition(NavigationOverriding1.RedirectedPage1.class).getViewId(),
                                                       "/redirectedPage1.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(NavigationOverriding1.ForwardedPage1.class).getViewId(),
                                                       "/forwardedPage1.xhtml");

        ViewConfigEntry viewConfigEntry =ViewConfigCache.getViewDefinition(NavigationOverriding1.RedirectedPage1.class);
        assertEquals(NavigationMode.REDIRECT, viewConfigEntry.getNavigationMode());

        viewConfigEntry = ViewConfigCache.getViewDefinition(NavigationOverriding1.ForwardedPage1.class);

        assertEquals(NavigationMode.FORWARD, viewConfigEntry.getNavigationMode());
    }

    @Test
    public void testNavigationOverriding2()
    {
        viewConfigExtension.addPageDefinition(NavigationOverriding2.RedirectedPage2.class);
        viewConfigExtension.addPageDefinition(NavigationOverriding2.ForwardedPage2.class);

        assertEquals(ViewConfigCache.getViewDefinition(NavigationOverriding2.RedirectedPage2.class).getViewId(),
                                                       "/redirectedPage2.xhtml");
        assertEquals(ViewConfigCache.getViewDefinition(NavigationOverriding2.ForwardedPage2.class).getViewId(),
                                                       "/forwardedPage2.xhtml");

        ViewConfigEntry viewConfigEntry =ViewConfigCache.getViewDefinition(NavigationOverriding2.RedirectedPage2.class);
        assertEquals(NavigationMode.REDIRECT, viewConfigEntry.getNavigationMode());

        viewConfigEntry = ViewConfigCache.getViewDefinition(NavigationOverriding2.ForwardedPage2.class);

        assertEquals(NavigationMode.FORWARD, viewConfigEntry.getNavigationMode());
    }
}
