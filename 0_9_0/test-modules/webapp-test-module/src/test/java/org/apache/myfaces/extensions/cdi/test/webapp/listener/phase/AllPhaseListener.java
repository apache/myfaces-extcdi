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
package org.apache.myfaces.extensions.cdi.test.webapp.listener.phase;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;
import org.junit.Assert;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jakob Korherr
 */
@JsfPhaseListener
public class AllPhaseListener implements PhaseListener
{

    public static final String BEFORE_CALLED = "AllPhaseListener.BEFORE_CALLED";
    public static final String AFTER_CALLED = "AllPhaseListener.AFTER_CALLED";

    private List<Boolean> beforeCalled;
    private List<Boolean> afterCalled;
    private boolean initialized = false;

    public AllPhaseListener()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNotNull("FacesContext must be accessible at instantiation time", facesContext);
    }

    public void beforePhase(PhaseEvent event)
    {
        lazyinit();

        doAssertions(event);

        beforeCalled.set(event.getPhaseId().getOrdinal() - 1, true);
    }

    public void afterPhase(PhaseEvent event)
    {
        doAssertions(event);

        afterCalled.set(event.getPhaseId().getOrdinal() - 1, true);
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }

    private void lazyinit()
    {
        if (!initialized)
        {
            beforeCalled = new ArrayList<Boolean>(6);
            afterCalled = new ArrayList<Boolean>(6);

            for (int i = 0; i < 6; i++)
            {
                beforeCalled.add(false);
                afterCalled.add(false);
            }

            // put on ApplicationMap
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Map<String, Object> applicationMap = facesContext.getExternalContext().getApplicationMap();
            applicationMap.put(BEFORE_CALLED, beforeCalled);
            applicationMap.put(AFTER_CALLED, afterCalled);

            initialized = true;
        }
    }

    private void doAssertions(PhaseEvent event)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        PhaseId fcPhaseId = facesContext.getCurrentPhaseId();
        PhaseId eventPhaseId = event.getPhaseId();

        // PhaseIds must match
        Assert.assertEquals(fcPhaseId, eventPhaseId);
    }

}
