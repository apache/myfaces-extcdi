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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.storeViewIdAsNewViewId;
import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;

import javax.faces.application.ViewHandlerWrapper;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;

/**
 * @author Gerhard Petracek
 */
public class WindowContextAwareViewHandler extends ViewHandlerWrapper implements Deactivatable
{
    private ViewHandler wrapped;

    private volatile WindowHandler windowHandler;

    private final boolean deactivated;

    public WindowContextAwareViewHandler(ViewHandler wrapped)
    {
        this.wrapped = wrapped;
        this.deactivated = !isActivated();
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

        if(this.deactivated)
        {
            return url;
        }

        url = this.windowHandler.encodeURL(url);
        return url;
    }

    private void lazyInit()
    {
        if(this.windowHandler == null)
        {
            init();
        }
    }

    private synchronized void init()
    {
        // switch into paranoia mode
        if(this.windowHandler == null)
        {
            this.windowHandler = ConversationUtils.getWindowHandler();
        }
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }

    @Override
    public UIViewRoot restoreView(FacesContext facesContext, String viewId)
    {
        if(this.deactivated)
        {
            return super.restoreView(facesContext, viewId);
        }

        if(isWindowIdAvailable(facesContext))
        {
            WindowContext windowContext = ConversationUtils.getWindowContextManager().getCurrentWindowContext();

            if(windowContext != null)
            {
                //see EXTCDI-131
                storeViewIdAsNewViewId(windowContext, calculateViewId(facesContext, viewId));
            }
        }

        return super.restoreView(facesContext, viewId);
    }

    //see EXTCDI-148 required if the mapped url is different from the final view-id
    private String calculateViewId(FacesContext facesContext, String viewId)
    {
        UIViewRoot uiViewRoot = this.wrapped.createView(facesContext, viewId);

        if(uiViewRoot != null)
        {
            String newViewId = uiViewRoot.getViewId();

            if(newViewId != null)
            {
                return newViewId;
            }
        }
        return viewId;
    }

    /**
     * check if the window-id has been restored before the restore-view phase
     * @param facesContext current faces-context
     * @return true if the window-id has been restored before, false otherwise
     */
    private boolean isWindowIdAvailable(FacesContext facesContext)
    {
        return facesContext.getExternalContext().getRequestMap()
                        .containsKey(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY);
    }
}
