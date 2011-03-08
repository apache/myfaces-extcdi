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
package org.apache.myfaces.extensions.cdi.jsf.impl.security;

import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.SecurityUtils;

import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import static org.apache.myfaces.extensions.cdi.core.impl.util.SecurityUtils.invokeVoters;

/**
 * ATTENTION:
 * add all new methods to {@link org.apache.myfaces.extensions.cdi.jsf.impl.CodiViewHandler}
 *
 * @author Gerhard Petracek
 */
public class SecurityAwareViewHandler extends ViewHandlerWrapper implements Deactivatable
{
    private ViewHandler wrapped;

    private volatile BeanManager beanManager;

    private final boolean deactivated;

    public SecurityAwareViewHandler(ViewHandler wrapped)
    {
        this.wrapped = wrapped;
        this.deactivated = !isActivated();
    }

    public ViewHandler getWrapped()
    {
        return this.wrapped;
    }

    @Override
    public UIViewRoot createView(FacesContext context, String viewId)
    {
        UIViewRoot result = this.wrapped.createView(context, viewId);

        if(this.deactivated)
        {
            return result;
        }

        UIViewRoot originalViewRoot = context.getViewRoot();

        //we have to use it as current view if an AccessDecisionVoter uses the JSF API to check access to the view-id
        context.setViewRoot(result);

        try
        {
            ViewConfigDescriptor entry = ViewConfigCache.getViewConfig(result.getViewId());

            if(entry != null)
            {
                lazyInit();

                Class<? extends ViewConfig> errorView = null;

                if(entry instanceof EditableViewConfigDescriptor)
                {
                    errorView = ((EditableViewConfigDescriptor)entry).getErrorView();
                }

                invokeVoters(null /*TODO*/, this.beanManager, entry.getAccessDecisionVoters(), errorView);
            }
        }
        catch (AccessDeniedException accessDeniedException)
        {
            Class<? extends ViewConfig> errorView = SecurityUtils.getErrorView(accessDeniedException);
            return this.wrapped.createView(context, ViewConfigCache.getViewConfig(errorView).getViewId());
        }
        finally
        {
            if(originalViewRoot != null)
            {
                context.setViewRoot(originalViewRoot);
            }
        }

        return result;
    }

    private void lazyInit()
    {
        if(this.beanManager == null)
        {
            init();
        }
    }

    private synchronized void init()
    {
        // switch into paranoia mode
        if(this.beanManager == null)
        {
            this.beanManager = BeanManagerProvider.getInstance().getBeanManager();
        }
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
