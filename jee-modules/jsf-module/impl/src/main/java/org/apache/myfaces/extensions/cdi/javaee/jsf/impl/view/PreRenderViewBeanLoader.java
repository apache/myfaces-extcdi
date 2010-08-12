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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.view;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.PhaseId;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;

import javax.enterprise.event.Observes;
import javax.faces.event.PhaseEvent;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
final class PreRenderViewBeanLoader
{
    protected void initBeans(@Observes @BeforePhase(PhaseId.RENDER_RESPONSE) PhaseEvent event)
    {
        String viewId = event.getFacesContext().getViewRoot().getViewId();

        ViewDefinitionEntry viewDefinitionEntry = ViewDefinitionCache.getViewDefinition(viewId);

        if(viewDefinitionEntry == null)
        {
            return;
        }

        List<String> beanNames = viewDefinitionEntry.getBeanNames();

        for(String beanName : beanNames)
        {
            //resolve bean to trigger @PostConstruct if it isn't scoped
            CodiUtils.getOrCreateScopedInstanceOfBeanByName(beanName, Object.class);
        }
    }
}
