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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.event.PhaseEvent;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class PreRenderViewBeanLoader
{
    protected void initBeans(
            @Observes @BeforePhase(JsfPhaseId.RENDER_RESPONSE) PhaseEvent event, BeanManager beanManager)
    {
        String viewId = event.getFacesContext().getViewRoot().getViewId();

        ViewConfigDescriptor viewDefinitionEntry = ViewConfigCache.getViewConfig(viewId);

        if(viewDefinitionEntry == null)
        {
            return;
        }

        List<PageBeanDescriptor> beanEntries = viewDefinitionEntry.getPageBeanConfigs();

        for(PageBeanDescriptor beanEntry : beanEntries)
        {
            //resolve bean to trigger @PostConstruct if it isn't scoped
            CodiUtils.getContextualReferenceByName(beanManager, beanEntry.getBeanName(), Object.class);
        }
    }
}
