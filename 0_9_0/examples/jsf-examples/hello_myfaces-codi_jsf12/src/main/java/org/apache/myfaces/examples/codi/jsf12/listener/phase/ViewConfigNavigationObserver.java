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
package org.apache.myfaces.examples.codi.jsf12.listener.phase;

import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PreViewConfigNavigateEvent;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.examples.codi.jsf12.view.DemoPages;

import javax.enterprise.inject.Model;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * @author Gerhard Petracek
 */
@Model
public class ViewConfigNavigationObserver
{
    @Inject
    @Jsf
    private MessageContext messageContext;

    protected void onViewConfigNavigation(@Observes PreViewConfigNavigateEvent navigateEvent)
    {
        if(DemoPages.HelloMyFacesCodi1.class.equals(navigateEvent.getFromView()) &&
                !DemoPages.HelloMyFacesCodi2.class.equals(navigateEvent.getToView()) )
        {
            navigateEvent.navigateTo(DemoPages.HelloMyFacesCodi2.class);
        }

        this.messageContext.message()
                                .text("navigate from {oldViewId} to {newViewId} view.")
                                .namedArgument("oldViewId", navigateEvent.getFromView())
                                .namedArgument("newViewId", navigateEvent.getToView())
                           .add();
    }
}
