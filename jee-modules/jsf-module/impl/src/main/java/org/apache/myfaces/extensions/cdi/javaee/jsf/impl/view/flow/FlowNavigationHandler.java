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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.view.flow;

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.view.definition.ViewDefinition;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.view.definition.NavigationMode;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.view.ViewDefinitionCache;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.view.ViewDefinitionEntry;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Gerhard Petracek
 */
public class FlowNavigationHandler extends NavigationHandler
{
    private Set<String> otherOutcomes = new CopyOnWriteArraySet<String>();
    private Map<String, ViewDefinitionEntry> viewDefinitions = new ConcurrentHashMap<String, ViewDefinitionEntry>();

    private NavigationHandler navigationHandler;

    public FlowNavigationHandler(NavigationHandler navigationHandler)
    {
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void handleNavigation(FacesContext facesContext, String fromAction, String outcome)
    {
        if(outcome != null && outcome.contains("."))
        {
            String originalOutcome = outcome;

            if(!otherOutcomes.contains(outcome))
            {
                if(outcome.startsWith("class "))
                {
                    outcome = outcome.substring(6);
                }
                ViewDefinitionEntry entry = viewDefinitions.get(outcome);

                if(entry == null)
                {
                    Object loadedClass = ClassUtils.tryToLoadClassForName(outcome);

                    if(loadedClass == null)
                    {
                        otherOutcomes.add(originalOutcome);
                    }
                    else if(loadedClass instanceof Class && ViewDefinition.class.isAssignableFrom((Class)loadedClass))
                    {
                        //noinspection unchecked
                        entry = ViewDefinitionCache.getViewDefinition((Class<? extends ViewDefinition>)loadedClass);
                    }
                }

                if(entry != null)
                {
                    processViewDefinitionEntry(facesContext, entry);
                    viewDefinitions.put(outcome, entry);
                    //just to invoke all other nav handlers if they have to perform special tasks...
                    navigationHandler.handleNavigation(facesContext, fromAction, null);
                    return;
                }
            }
        }

        navigationHandler.handleNavigation(facesContext, fromAction, outcome);
    }

    private void processViewDefinitionEntry(FacesContext facesContext, ViewDefinitionEntry entry)
    {
        String targetViewId = entry.getViewId();
        if(NavigationMode.REDIRECT.equals(entry.getNavigationMode()))
        {
            ExternalContext externalContext = facesContext.getExternalContext();
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            String redirectPath = viewHandler.getActionURL(facesContext, targetViewId);

            try
            {
                externalContext.redirect(externalContext.encodeActionURL(redirectPath));
            }
            catch (IOException e)
            {
                throw new FacesException(e.getMessage(), e);
            }
        }
        else
        {
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            UIViewRoot viewRoot = viewHandler.createView(facesContext, targetViewId);
            facesContext.setViewRoot(viewRoot);
            facesContext.renderResponse();
        }
    }
}
