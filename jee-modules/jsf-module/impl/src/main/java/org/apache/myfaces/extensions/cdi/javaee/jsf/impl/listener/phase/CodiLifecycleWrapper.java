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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.listener.phase;

import javax.faces.lifecycle.Lifecycle;
import javax.faces.event.PhaseListener;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import java.util.List;

/**
 * intermediate workaround for
 * {@link org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils#registerPhaseListener}
 *
 * @author Gerhard Petracek
 */
class CodiLifecycleWrapper extends Lifecycle
{
    private Lifecycle wrapped;

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
        wrapped.execute(facesContext);
    }

    public PhaseListener[] getPhaseListeners()
    {
        return this.wrapped.getPhaseListeners();
    }

    public void removePhaseListener(PhaseListener phaseListener)
    {
        wrapped.removePhaseListener(phaseListener);
    }

    public void render(FacesContext facesContext)
            throws FacesException
    {
        wrapped.render(facesContext);
    }
}
