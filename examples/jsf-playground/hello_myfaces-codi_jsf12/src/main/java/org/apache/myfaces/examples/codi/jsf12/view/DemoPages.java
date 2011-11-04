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
package org.apache.myfaces.examples.codi.jsf12.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBean;
import static org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.NavigationMode.FORWARD;
import org.apache.myfaces.examples.codi.jsf12.conversation.grouped.ConversationDemoBean1;

/**
 * View-config
 */
@Page(basePath = "" /*override default to ignore path level*/,
      navigation = Page.NavigationMode.REDIRECT, extension = Page.Extension.JSP)
public abstract class DemoPages implements ViewConfig
{
    @PageBean(ConversationDemoBean1.class) //triggers e.g. @PostConstruct before the rendering process (if needed)
    @Page(basePath = ".")
    //means that DemoPages$HelloMyFacesCodi gets /demoPages/helloMyFacesCodi.jsp if parent-base-path isn't overridden
    //in this case: helloMyFacesCodi.jsp
    public final class HelloMyFacesCodi extends DemoPages
    {
    }

    @Page(basePath = ".")
    public final class HelloMyFacesCodi1 extends DemoPages
    {
    }

    @ViewMode(readOnly = true) //custom view meta-data
    @Page(basePath = ".", navigation = FORWARD)
    public final class HelloMyFacesCodi2 extends DemoPages
    {
    }

    @Page(basePath = ".")
    public final class HelloMyFacesCodi3 extends DemoPages
    {
    }
}
