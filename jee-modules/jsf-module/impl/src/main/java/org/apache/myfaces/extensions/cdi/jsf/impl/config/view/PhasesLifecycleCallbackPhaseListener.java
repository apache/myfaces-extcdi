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

import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.invalidPhasesCallbackMethod;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseListener;
import javax.faces.component.UIViewRoot;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Gerhard Petracek
 */
@JsfPhaseListener
public final class PhasesLifecycleCallbackPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = 6893021853444122202L;

    public void afterPhase(PhaseEvent event)
    {
        try
        {
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

        List<PageBeanConfigEntry> beanEntries = viewDefinitionEntry.getBeanDefinitions();

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
            bean = CodiUtils.getOrCreateScopedInstanceOfBeanByName(beanEntry.getBeanName(), Object.class);
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
}
