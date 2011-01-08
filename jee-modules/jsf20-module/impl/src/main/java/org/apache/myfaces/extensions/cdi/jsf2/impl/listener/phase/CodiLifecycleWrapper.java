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
package org.apache.myfaces.extensions.cdi.jsf2.impl.listener.phase;

import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.request.BeforeAfterFacesRequestBroadcaster;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.startup.ApplicationStartupBroadcaster;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.LifecycleAwareWindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import java.util.List;

/**
 * intermediate workaround for
 * {@link org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils#registerPhaseListener}
 *
 * @author Gerhard Petracek
 */
class CodiLifecycleWrapper extends Lifecycle
{
    private Lifecycle wrapped;

    private BeforeAfterFacesRequestBroadcaster beforeAfterFacesRequestBroadcaster;

    private Boolean initialized;

    private Boolean applicationInitialized;

    CodiLifecycleWrapper(Lifecycle wrapped, List<PhaseListener> phaseListeners)
    {
        this.wrapped = wrapped;

        for(PhaseListener phaseListener : phaseListeners)
        {
            this.wrapped.addPhaseListener(phaseListener);
        }
    }

    public void addPhaseListener(PhaseListener phaseListener)
    {
        wrapped.addPhaseListener(phaseListener);
    }

    public void execute(FacesContext facesContext)
            throws FacesException
    {
        broadcastApplicationStartupBroadcaster();
        broadcastBeforeFacesRequestEvent(facesContext);

        WindowHandler windowHandler = CodiUtils.getContextualReferenceByClass(WindowHandler.class);

        if (windowHandler instanceof LifecycleAwareWindowHandler)
        {
            ((LifecycleAwareWindowHandler) windowHandler).beforeLifecycleExecute(facesContext);
            if (facesContext.getResponseComplete())
            {
                // no further processing
                return;
            }
        }

        wrapped.execute(facesContext);
    }

    public PhaseListener[] getPhaseListeners()
    {
        return wrapped.getPhaseListeners();
    }

    public void removePhaseListener(PhaseListener phaseListener)
    {
        wrapped.removePhaseListener(phaseListener);
    }

    public void render(FacesContext facesContext)
            throws FacesException
    {
        wrapped.render(facesContext);
        //if the cache would get resetted by an observer or a phase-listener
        //it might be the case that a 2nd observer accesses the cache again and afterwards there won't be a cleanup
        //-> don't remove:
        RequestCache.resetCache();
    }

    private void broadcastApplicationStartupBroadcaster()
    {
        //just an !additional! check to improve the performance
        if(applicationInitialized == null)
        {
            applicationInitialized = true;
            ApplicationStartupBroadcaster applicationStartupBroadcaster =
                    CodiUtils.getContextualReferenceByClass(ApplicationStartupBroadcaster.class);
            applicationStartupBroadcaster.broadcastStartupEvent();
        }
    }

    private void broadcastBeforeFacesRequestEvent(FacesContext facesContext)
    {
        lazyInit();
        if(this.beforeAfterFacesRequestBroadcaster != null)
        {
            this.beforeAfterFacesRequestBroadcaster.broadcastBeforeFacesRequestEvent(facesContext);
        }
    }

    private void lazyInit()
    {
        if(this.initialized == null)
        {
            synchronized (this)
            {
                if(ClassDeactivation.isClassActivated(BeforeAfterFacesRequestBroadcaster.class))
                {
                    this.beforeAfterFacesRequestBroadcaster =
                            CodiUtils.getContextualReferenceByClass(BeforeAfterFacesRequestBroadcaster.class);
                }

                this.initialized = true;
            }
        }
    }
}
