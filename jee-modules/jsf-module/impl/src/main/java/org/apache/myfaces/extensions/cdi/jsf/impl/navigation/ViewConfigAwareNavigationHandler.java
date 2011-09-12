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
package org.apache.myfaces.extensions.cdi.jsf.impl.navigation;

import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToLoadClassForName;

import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageParameter;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.NavigationMode;
import org.apache.myfaces.extensions.cdi.core.api.navigation.PreViewConfigNavigateEvent;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;

import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.PageParameterContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.enterprise.inject.spi.BeanManager;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Gerhard Petracek
 */
public class ViewConfigAwareNavigationHandler extends NavigationHandler
{
    private Set<String> otherOutcomes = new CopyOnWriteArraySet<String>();
    private Map<String, ViewConfigDescriptor> viewConfigs = new ConcurrentHashMap<String, ViewConfigDescriptor>();

    private NavigationHandler navigationHandler;
    private boolean implicitNavigationSupported;

    private BeanManager beanManager;

    private PageParameterContext pageParameterContext;

    /**
     * Constructor which allows to use the given {@link NavigationHandler}
     * @param navigationHandler navigation-handler of jsf
     * @param implicitNavigationSupported true in case of jsf2+ and false in case of jsf1.2
     */
    public ViewConfigAwareNavigationHandler(NavigationHandler navigationHandler, boolean implicitNavigationSupported)
    {
        this.navigationHandler = navigationHandler;
        this.implicitNavigationSupported = implicitNavigationSupported;
    }

    //Security checks will be performed by the view-handler provided by codi
    @Override
    public void handleNavigation(FacesContext facesContext, String fromAction, String outcome)
    {
        initBeanManager();
        if(outcome != null && outcome.contains("."))
        {
            String originalOutcome = outcome;
            String oldViewId = facesContext.getViewRoot().getViewId();

            if(!this.otherOutcomes.contains(outcome))
            {
                //it isn't possible to support interfaces due to cdi restrictions
                if(outcome.startsWith("class "))
                {
                    outcome = outcome.substring(6);
                }
                ViewConfigDescriptor entry = this.viewConfigs.get(outcome);

                if(entry == null)
                {
                    if(DefaultErrorView.class.getName().equals(originalOutcome))
                    {
                        entry = ViewConfigCache.getDefaultErrorViewConfigDescriptor();
                    }
                }

                boolean allowCaching = true;
                if(entry == null)
                {
                    Class<?> loadedClass = tryToLoadClassForName(outcome);

                    if(loadedClass == null)
                    {
                        this.otherOutcomes.add(originalOutcome);
                    }
                    else if(ViewConfig.class.isAssignableFrom(loadedClass))
                    {
                        //a sub-classed page-config for annotating it with different view params
                        if(loadedClass.getAnnotation(Page.class) == null &&
                                loadedClass.getSuperclass().getAnnotation(Page.class) != null)
                        {
                            allowCaching = false;
                            addConfiguredViewParameters(loadedClass);

                            loadedClass = loadedClass.getSuperclass();
                        }
                        //noinspection unchecked
                        entry = ViewConfigCache.getViewConfigDescriptor((Class<? extends ViewConfig>) loadedClass);
                    }
                }

                if(entry != null)
                {
                    if(allowCaching)
                    {
                        this.viewConfigs.put(outcome, entry);
                        addConfiguredViewParameters(entry.getViewConfig()); //in case of false it has been added already
                    }

                    PreViewConfigNavigateEvent navigateEvent = firePreViewConfigNavigateEvent(oldViewId, entry);

                    entry = tryToUpdateEntry(entry, navigateEvent);

                    if(entry != null && !this.implicitNavigationSupported) //entry might be null after the update
                    {
                        //jsf1.2
                        processViewDefinitionEntry(facesContext, entry);

                        //just to invoke all other nav handlers if they have to perform special tasks...
                        this.navigationHandler.handleNavigation(facesContext, null, null);
                        return;
                    }
                    else if(entry != null)
                    {
                        //jsf2+
                        outcome = convertEntryToOutcome(facesContext.getExternalContext(), entry);
                    }
                }
            }
        }

        this.navigationHandler.handleNavigation(facesContext, fromAction, outcome);
    }

    private void addConfiguredViewParameters(Class<?> viewConfigClass)
    {
        if(this.pageParameterContext != null)
        {
            PageParameter pageParameter = viewConfigClass.getAnnotation(PageParameter.class);

            if(pageParameter != null)
            {
                addConfiguredPageParameter(pageParameter);
            }
            else
            {
                PageParameter.List pageParameterList = viewConfigClass.getAnnotation(PageParameter.List.class);

                if(pageParameterList != null)
                {
                    for(PageParameter currentPageParameter : pageParameterList.value())
                    {
                        addConfiguredPageParameter(currentPageParameter);
                    }
                }
            }
        }
    }

    private void addConfiguredPageParameter(PageParameter viewParameter)
    {
        this.pageParameterContext.addPageParameter(viewParameter.key(), viewParameter.value());
    }

    private String convertEntryToOutcome(ExternalContext externalContext, ViewConfigDescriptor entry)
    {
        boolean performRedirect = Page.NavigationMode.REDIRECT.equals(entry.getNavigationMode());
        boolean includeViewParameters = false;

        if(entry instanceof EditableViewConfigDescriptor)
        {
            includeViewParameters = Page.ViewParameterMode.INCLUDE
                    .equals(((EditableViewConfigDescriptor) entry).getViewParameterMode());
        }

        StringBuilder result = new StringBuilder(entry.getViewId());

        if(performRedirect)
        {
            result.append("?faces-redirect=true");
        }
        if(includeViewParameters)
        {
            if(performRedirect)
            {
                result.append("&");
            }
            else
            {
                result.append("?");
            }
            result.append("includeViewParams=true");

            return JsfUtils.addParameters(externalContext, result.toString(), true, true, false);
        }

        return JsfUtils.addParameters(externalContext, result.toString(), false, performRedirect, false);
    }

    private ViewConfigDescriptor tryToUpdateEntry(ViewConfigDescriptor viewConfigDescriptor,
                                                  PreViewConfigNavigateEvent navigateEvent)
    {
        if(navigateEvent == null)
        {
            return viewConfigDescriptor;
        }

        if(navigateEvent.getToView() == null)
        {
            return null;
        }

        if(navigateEvent.getToView().equals(viewConfigDescriptor.getViewConfig()))
        {
            return viewConfigDescriptor;
        }

        return ViewConfigCache.getViewConfigDescriptor(navigateEvent.getToView());
    }

    private PreViewConfigNavigateEvent firePreViewConfigNavigateEvent(
            String oldViewId, ViewConfigDescriptor newViewConfigDescriptor)
    {
        ViewConfigDescriptor oldViewConfigDescriptor = ViewConfigCache.getViewConfigDescriptor(oldViewId);

        if(oldViewConfigDescriptor != null)
        {
            PreViewConfigNavigateEvent navigateEvent = new PreViewConfigNavigateEvent(
                    oldViewConfigDescriptor.getViewConfig(), newViewConfigDescriptor.getViewConfig());

            this.beanManager.fireEvent(navigateEvent);
            return navigateEvent;
        }
        return null;
    }

    private void initBeanManager()
    {
        if(this.beanManager == null)
        {
            this.beanManager = BeanManagerProvider.getInstance().getBeanManager();
            this.pageParameterContext =
                    CodiUtils.getContextualReferenceByClass(this.beanManager, PageParameterContext.class, true);
        }
    }

    private void processViewDefinitionEntry(FacesContext facesContext, ViewConfigDescriptor entry)
    {
        String targetViewId = entry.getViewId();

        NavigationMode currentNavigationMode = entry.getNavigationMode();

        if(NavigationMode.DEFAULT.equals(currentNavigationMode))
        {
            //TODO use value of the config
            currentNavigationMode = NavigationMode.FORWARD;
        }

        if(NavigationMode.REDIRECT.equals(currentNavigationMode))
        {
            ExternalContext externalContext = facesContext.getExternalContext();
            ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
            String redirectPath = viewHandler.getActionURL(facesContext, targetViewId);

            try
            {
                //there are no jsf2 view-params, but codi view-config params should be added (if present)
                redirectPath = JsfUtils.addParameters(externalContext, redirectPath, false, true, true);
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
