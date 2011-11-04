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
package org.apache.myfaces.examples.codi.jsf12;

import org.apache.myfaces.examples.codi.jsf12.view.DemoPages;
import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.navigation.ViewNavigationHandler;

import javax.enterprise.inject.Model;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

/**
 * Navigation handler based on view-configs
 */
@Model
public class ManualNavigationBean
{
    @Inject
    private ViewNavigationHandler viewNavigationHandler;

    public void navigateToHelloMyFacesCodi(ActionEvent actionEvent)
    {
        this.viewNavigationHandler.navigateTo(DemoPages.HelloMyFacesCodi.class);
    }

    public void navigateToErrorView(ActionEvent actionEvent)
    {
        this.viewNavigationHandler.navigateTo(DefaultErrorView.class);
    }
}
