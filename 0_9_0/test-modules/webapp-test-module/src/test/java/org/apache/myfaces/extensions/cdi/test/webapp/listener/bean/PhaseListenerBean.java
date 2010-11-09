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
package org.apache.myfaces.extensions.cdi.test.webapp.listener.bean;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.junit.Assert;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakob Korherr
 */
@Named
@ApplicationScoped
public class PhaseListenerBean implements Serializable
{

    private List<Boolean> beforeAnyCalled;
    private List<Boolean> afterAnyCalled;
    private List<Boolean> beforeInvokeApplicationCalled;
    private List<Boolean> afterInvokeApplicationCalled;

    @PostConstruct
    public void initialize()
    {
        beforeAnyCalled = new ArrayList<Boolean>(6);
        afterAnyCalled = new ArrayList<Boolean>(6);
        beforeInvokeApplicationCalled = new ArrayList<Boolean>(6);
        afterInvokeApplicationCalled = new ArrayList<Boolean>(6);

        for (int i = 0; i < 6; i++)
        {
            beforeAnyCalled.add(false);
            afterAnyCalled.add(false);
            beforeInvokeApplicationCalled.add(false);
            afterInvokeApplicationCalled.add(false);
        }
    }

    public void beforeAnyPhase(@Observes @BeforePhase(JsfPhaseId.ANY_PHASE) PhaseEvent event)
    {
        doAssertions(event);

        beforeAnyCalled.set(event.getPhaseId().getOrdinal() - 1, true);
    }

    public void afterAnyPhase(@Observes @AfterPhase(JsfPhaseId.ANY_PHASE) PhaseEvent event)
    {
        doAssertions(event);

        afterAnyCalled.set(event.getPhaseId().getOrdinal() - 1, true);
    }

    public void beforeInvokeApplicationPhase(@Observes @BeforePhase(JsfPhaseId.INVOKE_APPLICATION) PhaseEvent event)
    {
        doAssertions(event);

        beforeInvokeApplicationCalled.set(event.getPhaseId().getOrdinal() - 1, true);
    }

    public void afterInvokeApplicationPhase(@Observes @AfterPhase(JsfPhaseId.INVOKE_APPLICATION) PhaseEvent event)
    {
        doAssertions(event);

        afterInvokeApplicationCalled.set(event.getPhaseId().getOrdinal() - 1, true);
    }

    private void doAssertions(PhaseEvent event)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        PhaseId fcPhaseId = facesContext.getCurrentPhaseId();
        PhaseId eventPhaseId = event.getPhaseId();

        // PhaseIds must match
        Assert.assertEquals(fcPhaseId, eventPhaseId);
    }

    public List<Boolean> getBeforeAnyCalled()
    {
        return beforeAnyCalled;
    }

    public List<Boolean> getAfterAnyCalled()
    {
        return afterAnyCalled;
    }

    public List<Boolean> getBeforeInvokeApplicationCalled()
    {
        return beforeInvokeApplicationCalled;
    }

    public List<Boolean> getAfterInvokeApplicationCalled()
    {
        return afterInvokeApplicationCalled;
    }
}
