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

import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.LifecycleAwarePageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.BasefolderViewConfigRootMarker;
import org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.RenamedBasefolderViewConfigRootMarker;
import org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.SubfolderViewConfigRootMarker;
import org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.order.OrderOverviewPage;
import org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.registration.RegistrationStep01PageBean;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.NavigationMode;

import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class ViewConfigTest
{
    private TestableViewConfigExtension viewConfigExtension = new TestableViewConfigExtension();

    @BeforeMethod
    public void resetCache()
    {
        ViewConfigCache.reset();
        viewConfigExtension.beginViewConfigExtraction(); //allow lazy init
    }

    @Test
    public void testSimpleCase()
    {
        viewConfigExtension.addPageDefinition(SimpleView.class);

        assertEquals(ViewConfigCache.getViewConfig(SimpleView.class).getViewId(), "/simpleView.xhtml");

        assertEquals(ViewConfigCache.getViewConfig(SimpleView.class).getViewConfig(),
                                                       SimpleView.class);
    }

    @Test
    public void testSimpleManualCase()
    {
        viewConfigExtension.addPageDefinition(SimpleViewWithManualName.class);

        assertEquals(ViewConfigCache.getViewConfig(SimpleViewWithManualName.class).getViewId(),
                                                       "/simpleManualPage.xhtml");
    }

    @Test
    public void testSimpleNestedCase()
    {
        viewConfigExtension.addPageDefinition(SimpleNestedViewConfig.Page1.class);
        viewConfigExtension.addPageDefinition(SimpleNestedViewConfig.Page2.class);

        assertEquals(ViewConfigCache.getViewConfig(SimpleNestedViewConfig.Page1.class).getViewId(),
                                                       "/simpleNestedViewConfig/page1.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(SimpleNestedViewConfig.Page2.class).getViewId(),
                                                       "/simpleNestedViewConfig/page2.xhtml");
    }

    @Test
    public void testNestedViewConfigWithManualNamesAndSharedBasePath1()
    {
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath1.Page1.class);
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath1.Page2.class);

        assertEquals(ViewConfigCache.getViewConfig(
                NestedViewConfigWithManualNamesAndSharedBasePath1.Page1.class).getViewId(),
                "/manual/page1.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(
                NestedViewConfigWithManualNamesAndSharedBasePath1.Page2.class).getViewId(),
                "/manual/page2.xhtml");
    }

    @Test
    public void testNestedViewConfigWithManualNamesAndSharedBasePath2()
    {
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath2.Page1.class);
        viewConfigExtension.addPageDefinition(NestedViewConfigWithManualNamesAndSharedBasePath2.Page2.class);

        assertEquals(ViewConfigCache.getViewConfig(
                NestedViewConfigWithManualNamesAndSharedBasePath2.Page1.class).getViewId(),
                "/manual/page3.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(
                NestedViewConfigWithManualNamesAndSharedBasePath2.Page2.class).getViewId(),
                "/manual/page4.xhtml");
    }

    @Test
    public void testVirtualNesting()
    {
        viewConfigExtension.addPageDefinition(VirtualNesting.Page1.class);
        viewConfigExtension.addPageDefinition(VirtualNesting.Page2.class);

        assertEquals(ViewConfigCache.getViewConfig(VirtualNesting.Page1.class).getViewId(),
                                                       "/page1.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(VirtualNesting.Page2.class).getViewId(),
                                                       "/page2.xhtml");
    }

    @Test
    public void testMultipleSimpleNestedConfigs()
    {
        viewConfigExtension.addPageDefinition(MultipleSimpleNestedConfigs.Conversations.Grouped.Step1.class);
        viewConfigExtension.addPageDefinition(MultipleSimpleNestedConfigs.Conversations.Grouped.Step2.class);

        assertEquals(ViewConfigCache.getViewConfig(
                MultipleSimpleNestedConfigs.Conversations.Grouped.Step1.class).getViewId(),
                "/multipleSimpleNestedConfigs/conversations/grouped/step1.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(
                MultipleSimpleNestedConfigs.Conversations.Grouped.Step2.class).getViewId(),
                "/multipleSimpleNestedConfigs/conversations/grouped/step2.xhtml");
    }

    @Test
    public void testSimpleInterfaceBasedConfigs()
    {
        viewConfigExtension.addPageDefinition(SimpleInterfaceBasedConfigs.Wizards.Registration.Step1.class);
        viewConfigExtension.addPageDefinition(SimpleInterfaceBasedConfigs.Wizards.Order.Step1.class);

        ViewConfigDescriptor registrationConfigEntry = ViewConfigCache.getViewConfig(SimpleInterfaceBasedConfigs.Wizards.Registration.Step1.class);
        ViewConfigDescriptor orderConfigEntry = ViewConfigCache.getViewConfig(SimpleInterfaceBasedConfigs.Wizards.Order.Step1.class);

        assertEquals(registrationConfigEntry.getViewId(),
                "/pages/wizards/registration/step1.xhtml");
        assertEquals(orderConfigEntry.getViewId(),
                "/pages/wizards/order/step1.xhtml");

        assertEquals(registrationConfigEntry.getNavigationMode(), Page.NavigationMode.DEFAULT);
        assertEquals(orderConfigEntry.getNavigationMode(), Page.NavigationMode.REDIRECT);

        assertTrue(registrationConfigEntry.getAccessDecisionVoters().size() == 0);
        assertEquals(orderConfigEntry.getAccessDecisionVoters().size(), 1);
        assertEquals(orderConfigEntry.getAccessDecisionVoters().iterator().next(), TestAccessDecisionVoter1.class);
    }

    @Test
    public void testNavigationOverriding1()
    {
        viewConfigExtension.addPageDefinition(NavigationOverriding1.RedirectedPage1.class);
        viewConfigExtension.addPageDefinition(NavigationOverriding1.ForwardedPage1.class);

        assertEquals(ViewConfigCache.getViewConfig(NavigationOverriding1.RedirectedPage1.class).getViewId(),
                                                       "/redirectedPage1.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(NavigationOverriding1.ForwardedPage1.class).getViewId(),
                                                       "/forwardedPage1.xhtml");

        ViewConfigDescriptor viewConfig =ViewConfigCache.getViewConfig(NavigationOverriding1.RedirectedPage1.class);
        assertEquals(viewConfig.getNavigationMode(), NavigationMode.REDIRECT);

        viewConfig = ViewConfigCache.getViewConfig(NavigationOverriding1.ForwardedPage1.class);

        assertEquals(viewConfig.getNavigationMode(), NavigationMode.FORWARD);
    }

    @Test
    public void testNavigationOverriding2()
    {
        viewConfigExtension.addPageDefinition(NavigationOverriding2.RedirectedPage2.class);
        viewConfigExtension.addPageDefinition(NavigationOverriding2.ForwardedPage2.class);

        assertEquals(ViewConfigCache.getViewConfig(NavigationOverriding2.RedirectedPage2.class).getViewId(),
                                                       "/redirectedPage2.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(NavigationOverriding2.ForwardedPage2.class).getViewId(),
                                                       "/forwardedPage2.xhtml");

        ViewConfigDescriptor viewConfig =ViewConfigCache.getViewConfig(NavigationOverriding2.RedirectedPage2.class);
        assertEquals(viewConfig.getNavigationMode(), NavigationMode.REDIRECT);

        viewConfig = ViewConfigCache.getViewConfig(NavigationOverriding2.ForwardedPage2.class);

        assertEquals(viewConfig.getNavigationMode(), NavigationMode.FORWARD);
    }

    @Test
    public void testViewConfigWithAccessDecisionVoters()
    {
        viewConfigExtension.addPageDefinition(ViewConfigWithAccessDecisionVoters.Page1.class);
        viewConfigExtension.addPageDefinition(ViewConfigWithAccessDecisionVoters.Page2.class);

        ViewConfigDescriptor viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithAccessDecisionVoters.Page1.class);

        assertEquals(viewConfig.getAccessDecisionVoters().size(), 1);

        viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithAccessDecisionVoters.Page2.class);

        assertEquals(viewConfig.getAccessDecisionVoters().size(), 2);

        boolean voter2 = false;
        for(Class<? extends AccessDecisionVoter> accessDecisionVoter : viewConfig.getAccessDecisionVoters())
        {
            if(!voter2)
            {
                assertTrue(accessDecisionVoter.equals(TestAccessDecisionVoter2.class));
                voter2 = true;
            }
            else
            {
                assertTrue(accessDecisionVoter.equals(TestAccessDecisionVoter1.class));
            }
        }
    }

    @Test
    public void testViewConfigWithSecurityErrorPages()
    {
        viewConfigExtension.addPageDefinition(ViewConfigWithSecurityErrorPages.Page1.class);
        viewConfigExtension.addPageDefinition(ViewConfigWithSecurityErrorPages.Page2.class);

        ViewConfigDescriptor viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithSecurityErrorPages.Page1.class);

        assertEquals(((EditableViewConfigDescriptor)viewConfig).getErrorView(), SimpleView.class);

        viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithSecurityErrorPages.Page2.class);
        assertEquals(((EditableViewConfigDescriptor)viewConfig).getErrorView(), SimpleViewWithManualName.class);
    }

    @Test
    public void testViewConfigWithViewMetaData()
    {
        viewConfigExtension.addPageDefinition(ViewConfigWithViewMetaData.Page1.class);
        viewConfigExtension.addPageDefinition(ViewConfigWithViewMetaData.Page2.class);

        ViewConfigDescriptor viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithViewMetaData.Page1.class);

        assertEquals(viewConfig.getMetaData().size(), 2);

        viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithViewMetaData.Page2.class);
        assertEquals(viewConfig.getMetaData().size(), 3);
    }

    @Test
    public void testViewConfigWithViewController()
    {
        viewConfigExtension.addPageDefinition(ViewConfigWithViewController.Page1.class);
        viewConfigExtension.addPageDefinition(ViewConfigWithViewController.Page2.class);

        ViewConfigDescriptor viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithViewController.Page1.class);

        List<PageBeanDescriptor> pageBeanDescriptors = viewConfig.getPageBeanDescriptors();
        assertEquals(pageBeanDescriptors.size(), 1);
        assertTrue(pageBeanDescriptors.iterator().next().getBeanClass().equals(TestPageBean2.class));

        viewConfig = ViewConfigCache.getViewConfig(ViewConfigWithViewController.Page2.class);

        pageBeanDescriptors = viewConfig.getPageBeanDescriptors();
        assertEquals(pageBeanDescriptors.size(), 2);

        for(PageBeanDescriptor pageBeanDescriptor : pageBeanDescriptors)
        {
            assertTrue(pageBeanDescriptor.getBeanClass().equals(TestPageBean2.class) ||
                       pageBeanDescriptor.getBeanClass().equals(TestPageBean3.class));
        }
    }

    @Test
    public void testBasefolderInlineViewConfig()
    {
        viewConfigExtension.setInlineViewConfigRootMarker(BasefolderViewConfigRootMarker.class);
        viewConfigExtension.addInlinePageDefinition(OrderOverviewPage.class);
        viewConfigExtension.addInlinePageDefinition(RegistrationStep01PageBean.class);

        viewConfigExtension.finalizeConfig();

        assertEquals(ViewConfigCache.getViewConfig(
                OrderOverviewPage.class).getViewId(),
                "/pages/order/orderOverview.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(
                RegistrationStep01PageBean.class).getViewId(),
                "/pages/registration/registrationStep01.xhtml");

        assertEquals(ViewConfigCache.getViewConfig(
                OrderOverviewPage.class).getAccessDecisionVoters().iterator().next().getName(),
                "org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.order.TestAccessDecisionVoter3");

        PageBeanDescriptor pageBeanDescriptor = ViewConfigCache.getViewConfig(RegistrationStep01PageBean.class)
                .getPageBeanDescriptors().iterator().next();

        assertEquals(pageBeanDescriptor.getBeanClass(), RegistrationStep01PageBean.class);
        assertEquals(((LifecycleAwarePageBeanDescriptor) pageBeanDescriptor).getPreRenderViewMethods().size(), 1);
    }

    @Test
    public void testRenamvedBasefolderInlineViewConfig()
    {
        viewConfigExtension.setInlineViewConfigRootMarker(RenamedBasefolderViewConfigRootMarker.class);
        viewConfigExtension.addInlinePageDefinition(OrderOverviewPage.class);
        viewConfigExtension.addInlinePageDefinition(RegistrationStep01PageBean.class);

        viewConfigExtension.finalizeConfig();

        assertEquals(ViewConfigCache.getViewConfig(
                OrderOverviewPage.class).getViewId(),
                "/views/order/orderOverview.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(
                RegistrationStep01PageBean.class).getViewId(),
                "/views/registration/registrationStep01.xhtml");

        assertEquals(ViewConfigCache.getViewConfig(
                OrderOverviewPage.class).getAccessDecisionVoters().iterator().next().getName(),
                "org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.order.TestAccessDecisionVoter3");

        PageBeanDescriptor pageBeanDescriptor = ViewConfigCache.getViewConfig(RegistrationStep01PageBean.class)
                .getPageBeanDescriptors().iterator().next();

        assertEquals(pageBeanDescriptor.getBeanClass(), RegistrationStep01PageBean.class);
        assertEquals(((LifecycleAwarePageBeanDescriptor)pageBeanDescriptor).getPreRenderViewMethods().size(), 1);
    }

    @Test
    public void testSubfolderInlineViewConfig()
    {
        viewConfigExtension.setInlineViewConfigRootMarker(SubfolderViewConfigRootMarker.class);
        viewConfigExtension.addInlinePageDefinition(OrderOverviewPage.class);
        viewConfigExtension.addInlinePageDefinition(RegistrationStep01PageBean.class);

        viewConfigExtension.finalizeConfig();

        assertEquals(ViewConfigCache.getViewConfig(
                OrderOverviewPage.class).getViewId(),
                "/order/orderOverview.xhtml");
        assertEquals(ViewConfigCache.getViewConfig(
                RegistrationStep01PageBean.class).getViewId(),
                "/registration/registrationStep01.xhtml");

        assertEquals(ViewConfigCache.getViewConfig(
                OrderOverviewPage.class).getAccessDecisionVoters().iterator().next().getName(),
                "org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view.inline.pages.order.TestAccessDecisionVoter3");

        PageBeanDescriptor pageBeanDescriptor = ViewConfigCache.getViewConfig(RegistrationStep01PageBean.class)
                .getPageBeanDescriptors().iterator().next();

        assertEquals(pageBeanDescriptor.getBeanClass(), RegistrationStep01PageBean.class);
        assertEquals(((LifecycleAwarePageBeanDescriptor)pageBeanDescriptor).getPreRenderViewMethods().size(), 1);
    }

    @Test
    public void testInlineViewConfigDetection()
    {
        assertTrue(viewConfigExtension.isInlineViewConfig(OrderOverviewPage.class));
        assertTrue(viewConfigExtension.isInlineViewConfig(RegistrationStep01PageBean.class));
    }

    @Test
    public void testMissingInlineViewConfigRootMarkerWithInlineViewConfig()
    {
        viewConfigExtension.addInlinePageDefinition(OrderOverviewPage.class);

        viewConfigExtension.finalizeConfig();

        try
        {
            assertEquals(ViewConfigCache.getViewConfig(
                    OrderOverviewPage.class).getViewId(),
                    "/order/orderOverview.xhtml");
        }
        catch (IllegalStateException e)
        {
            return;
        }
        fail();
    }

    @Test
    public void testAmbiguousInlineViewConfigRootMarkerWithInlineViewConfig()
    {
        try
        {
            viewConfigExtension.setInlineViewConfigRootMarker(SubfolderViewConfigRootMarker.class);
            viewConfigExtension.setInlineViewConfigRootMarker(getClass());
        }
        catch (IllegalStateException e)
        {
            return;
        }
        fail();
    }
}
