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
package org.apache.myfaces.blank;

import org.apache.myfaces.extensions.cdi.core.api.listener.phase.annotation.View;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.PhaseId;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.annotation.AfterPhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.annotation.BeforePhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.project.stage.JsfProjectStage;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Model;
import javax.faces.event.PhaseEvent;
import javax.inject.Inject;

@Model
public class HelloCodiBean
{
    @Inject
    private JsfProjectStage jsfProjectStage;

    private String text;
    private StringBuffer invokedListenerMethods = new StringBuffer();

    public void preRenderView(@Observes @BeforePhase(PhaseId.RENDER_RESPONSE) PhaseEvent event)
    {
        this.invokedListenerMethods.append("preRenderView in phase:").append(event.getPhaseId());
        this.invokedListenerMethods.append(" | ");

        this.text = "Hello MyFaces CODI";
    }

    @View("/helloMyFacesCodi.jsp")
    public void preInvokeApplication(@Observes @BeforePhase(PhaseId.INVOKE_APPLICATION) PhaseEvent event)
    {
        this.invokedListenerMethods.append("preInvokeApplication in phase:").append(event.getPhaseId());
        this.invokedListenerMethods.append(" | ");
    }

    @View("/invalidPage.jsp")
    public void postRestoreViewInvalid(@Observes @AfterPhase(PhaseId.RESTORE_VIEW) PhaseEvent event)
    {
        this.invokedListenerMethods.append("postRestoreViewInvalid in phase:").append(event.getPhaseId());
        this.invokedListenerMethods.append(" | ");
    }

    @View({"/invalidPage.jsp", "/helloMyFacesCodi.jsp"})
    public void postRestoreView(@Observes @AfterPhase(PhaseId.RESTORE_VIEW) PhaseEvent event)
    {
        this.invokedListenerMethods.append("postRestoreView in phase:").append(event.getPhaseId());
        this.invokedListenerMethods.append(" | ");
    }

    public String getText()
    {
        return this.text;
    }

    public String getProjectStageName()
    {
        return this.jsfProjectStage.toString();
    }

    public String getInvokedListenerMethods()
    {
        return invokedListenerMethods.toString();
    }
}
