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
package org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import static org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId.*;
import org.apache.myfaces.extensions.cdi.jsf.api.request.RequestTypeResolver;
import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.injectFields;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;

import javax.enterprise.event.Observes;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.PhaseEvent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.ValueHolder;
import javax.faces.validator.Validator;
import javax.faces.context.FacesContext;
import java.util.Collection;

/**
 * see EXTCDI-127
 */
@ApplicationScoped
public class RestoreInjectionPointsObserver
{
    private String injectionMarker = RestoreInjectionPointsObserver.class.getName() + ":injected";

    protected void restoreInjectionPoints(@Observes @BeforePhase(PROCESS_VALIDATIONS) PhaseEvent event,
                                          CodiCoreConfig codiCoreConfig)
    {
        FacesContext facesContext = event.getFacesContext();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        restoreAllInjectionPoints(uiViewRoot, codiCoreConfig);

        facesContext.getExternalContext().getRequestMap().put(this.injectionMarker, uiViewRoot.getViewId());
    }

    protected void restoreInjectionPointsForSkippedRequests(@Observes @BeforePhase(RENDER_RESPONSE) PhaseEvent event,
                                                            CodiCoreConfig codiCoreConfig,
                                                            RequestTypeResolver requestTypeResolver)
    {
        //injection is performed by the application wrapper provided by codi in case of initial and get requests
        if(!requestTypeResolver.isPostRequest())
        {
            return;
        }

        FacesContext facesContext = event.getFacesContext();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(isSkippedPostback(facesContext))
        {
            //restored view but the life-cycle wasn't executed completely
            restoreAllInjectionPoints(uiViewRoot, codiCoreConfig);
        }
    }

    /**
     * Checks if the {@link #restoreInjectionPoints} has been invoked
     *
     * @param facesContext current faces-context
     * @return true if the injection points have to be restored, false otherwise
     */
    private boolean isSkippedPostback(FacesContext facesContext)
    {
        Object storedViewId = facesContext.getExternalContext().getRequestMap().get(this.injectionMarker);
        return storedViewId == null;
    }

    private void restoreAllInjectionPoints(UIViewRoot uiViewRoot, CodiCoreConfig codiCoreConfig)
    {
        if(uiViewRoot == null)
        {
            return;
        }

        boolean advancedQualifierRequiredForDependencyInjection =
                codiCoreConfig.isAdvancedQualifierRequiredForDependencyInjection();
        processComponents(uiViewRoot.getChildren(), advancedQualifierRequiredForDependencyInjection);
    }

    private void processComponents(Collection<UIComponent> uiComponents,
                                   boolean advancedQualifierRequiredForDependencyInjection)
    {
        if(uiComponents == null)
        {
            return;
        }

        for(UIComponent uiComponent : uiComponents)
        {
            inject(uiComponent, advancedQualifierRequiredForDependencyInjection);
            processComponents(uiComponent.getFacets().values(), advancedQualifierRequiredForDependencyInjection);
            processComponents(uiComponent.getChildren(), advancedQualifierRequiredForDependencyInjection);
        }
    }

    private void inject(UIComponent uiComponent, boolean advancedQualifierRequiredForDependencyInjection)
    {
        if(uiComponent instanceof ValueHolder)
        {
            injectFields(((ValueHolder)uiComponent).getConverter());

            if(uiComponent instanceof EditableValueHolder)
            {
                for(Validator validator : ((EditableValueHolder)uiComponent).getValidators())
                {
                    injectFields(validator, advancedQualifierRequiredForDependencyInjection);
                }
            }
        }
    }
}
