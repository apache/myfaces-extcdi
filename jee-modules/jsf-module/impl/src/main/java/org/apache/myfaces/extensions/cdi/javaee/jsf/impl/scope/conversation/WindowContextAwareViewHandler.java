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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;

import javax.faces.application.ViewHandlerWrapper;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
public class WindowContextAwareViewHandler extends ViewHandlerWrapper
{
    private ViewHandler wrapped;

    private WindowHandler windowHandler;

    public WindowContextAwareViewHandler(ViewHandler wrapped)
    {
        this.wrapped = wrapped;
    }

    public ViewHandler getWrapped()
    {
        return this.wrapped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId)
    {
        lazyInit();

        String url = this.wrapped.getActionURL(context, viewId);
        url = this.windowHandler.encodeURL(url);
        return url;
    }

    private synchronized void lazyInit()
    {
        if(this.windowHandler == null)
        {
            this.windowHandler = ConversationUtils.getWindowHandler();
        }
    }
}
