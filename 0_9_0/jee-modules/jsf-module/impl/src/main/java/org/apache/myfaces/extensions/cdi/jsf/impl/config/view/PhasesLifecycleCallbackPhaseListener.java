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

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.getOrCreateScopedInstanceOfBeanByName;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.Advanced;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.invalidPhasesCallbackMethod;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseId;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.enterprise.inject.spi.BeanManager;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Gerhard Petracek
 */
@Advanced
@JsfPhaseListener
@SuppressWarnings({"UnusedDeclaration"})
public final class PhasesLifecycleCallbackPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = 6893021853444122202L;

    private static final String INITIALIZED_VIEW_ID_MARKER_KEY = PhasesLifecycleCallbackPhaseListener.class.getName();

    @Inject
    private WindowContext windowContext;

    @Inject
    private BeanManager beanManager;

    public void afterPhase(PhaseEvent event)
    {
        try
        {
            processInitView(event);
            processPhaseCallbacks(event, false);
        }
        catch (Exception e)
        {
            if(!(e instanceof RuntimeException))
            {
                throw new IllegalStateException(e);
            }
            throw (RuntimeException)e;
        }
    }

    public void beforePhase(PhaseEvent event)
    {
        try
        {
            processInitView(event);
            processPreRenderView(event);
            processPhaseCallbacks(event, true);
        }
        catch (Exception e)
        {
            if(!(e instanceof RuntimeException))
            {
                throw new IllegalStateException(e);
            }
            throw (RuntimeException)e;
        }
    }

    private void processInitView(PhaseEvent event)
    {
        if(event.getPhaseId().equals(PhaseId.RESTORE_VIEW) && !isRedirectRequest(event.getFacesContext()))
        {
            return;
        }

        if (isValidView(event.getFacesContext()))
        {
            processInitView(event.getFacesContext().getViewRoot().getViewId());
        }
    }

    private void processInitView(String viewId)
    {
        //view already initialized
        if(viewId.equals(this.windowContext.getAttribute(INITIALIZED_VIEW_ID_MARKER_KEY, String.class)))
        {
            return;
        }

        this.windowContext.setAttribute(INITIALIZED_VIEW_ID_MARKER_KEY, viewId);

        ViewConfigEntry viewDefinitionEntry = ViewConfigCache.getViewDefinition(viewId);

        if (viewDefinitionEntry != null)
        {
            viewDefinitionEntry.invokeInitViewMethods();
        }
    }

    private void processPreRenderView(PhaseEvent event)
    {
        if (event.getPhaseId().equals(PhaseId.RENDER_RESPONSE))
        {
            processPreRenderView(event.getFacesContext().getViewRoot().getViewId());
        }
    }

    private void processPreRenderView(String viewId)
    {
        ViewConfigEntry viewDefinitionEntry = ViewConfigCache.getViewDefinition(viewId);

        if (viewDefinitionEntry != null)
        {
            viewDefinitionEntry.invokePreRenderViewMethods();
        }
    }

    public javax.faces.event.PhaseId getPhaseId()
    {
        return javax.faces.event.PhaseId.ANY_PHASE;
    }

    private void processPhaseCallbacks(PhaseEvent phaseEvent, boolean beforePhase) throws Exception
    {
        UIViewRoot viewRoot = phaseEvent.getFacesContext().getViewRoot();

        if(viewRoot == null)
        {
            return;
        }

        String viewId = viewRoot.getViewId();

        ViewConfigEntry viewDefinitionEntry = ViewConfigCache.getViewDefinition(viewId);

        if(viewDefinitionEntry == null)
        {
            return;
        }

        List<PageBeanConfigEntry> beanEntries = viewDefinitionEntry.getPageBeanDefinitions();

        Object bean;
        PhasesLifecycleCallbackEntry phasesLifecycleCallbackEntry;
        List<Method> lifecycleCallbacks;

        for(PageBeanConfigEntry beanEntry : beanEntries)
        {
            phasesLifecycleCallbackEntry = beanEntry.getPhasesLifecycleCallback(phaseEvent.getPhaseId());

            if(phasesLifecycleCallbackEntry == null)
            {
                continue;
            }


            if(beforePhase)
            {
                lifecycleCallbacks = phasesLifecycleCallbackEntry.getBeforePhaseCallbacks();
            }
            else
            {
                lifecycleCallbacks = phasesLifecycleCallbackEntry.getAfterPhaseCallbacks();
            }

            if(lifecycleCallbacks.isEmpty())
            {
                continue;
            }

            //TODO provide a detailed error message in case of a missing bean
            bean = getOrCreateScopedInstanceOfBeanByName(this.beanManager, beanEntry.getBeanName(), Object.class);
            invokePhasesLifecycleCallbacks(bean, lifecycleCallbacks, phaseEvent);
        }
    }

    private void invokePhasesLifecycleCallbacks(Object bean, List<Method> lifecycleCallbacks, PhaseEvent phaseEvent)
            throws InvocationTargetException, IllegalAccessException
    {
        Class<?>[] parameterTypes;
        for(Method currentMethod : lifecycleCallbacks)
        {
            currentMethod.setAccessible(true);

            parameterTypes = currentMethod.getParameterTypes();
            if(parameterTypes.length == 0)
            {
                currentMethod.invoke(bean);
            }
            else if(parameterTypes.length == 1 && PhaseEvent.class.isAssignableFrom(parameterTypes[0]))
            {
                currentMethod.invoke(bean, phaseEvent);
            }
            else
            {
                throw invalidPhasesCallbackMethod(bean.getClass(), currentMethod);
            }
        }
    }

    private boolean isValidView(FacesContext facesContext)
    {
        return facesContext.getViewRoot() != null && facesContext.getViewRoot().getViewId() != null;
    }

    private boolean isRedirectRequest(FacesContext facesContext)
    {
        return facesContext.getResponseComplete();
    }
}
