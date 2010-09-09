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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.BeforePhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.AfterPhase;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ExceptionUtils.unsupportedPhasesLifecycleCallback;

import javax.faces.event.PhaseId;
import static javax.faces.event.PhaseId.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
class PageBeanConfigEntry
{
    private final String beanName;

    private final Class beanClass;

    private final Map<PhaseId, PhasesLifecycleCallbackEntry> phasesLifecycleCallbacks;

    PageBeanConfigEntry(String beanName, Class beanClass)
    {
        this.beanName = beanName;
        this.beanClass = beanClass;
        this.phasesLifecycleCallbacks = Collections.unmodifiableMap(findCallbackDefinitions(beanClass));
    }

    String getBeanName()
    {
        return beanName;
    }

    Class getBeanClass()
    {
        return beanClass;
    }

    PhasesLifecycleCallbackEntry getPhasesLifecycleCallback(PhaseId phaseId)
    {
        return phasesLifecycleCallbacks.get(phaseId);
    }

    private Map<PhaseId, PhasesLifecycleCallbackEntry> findCallbackDefinitions(Class beanClass)
    {
        Class currentClass = beanClass;

        PhasesLifecycleCallbackEntryHelper beforeCallbackEntryHelper = new PhasesLifecycleCallbackEntryHelper();
        PhasesLifecycleCallbackEntryHelper afterCallbackEntryHelper = new PhasesLifecycleCallbackEntryHelper();

        while(!(currentClass.getName().equals(Object.class.getName())))
        {
            for(Method currentMethod : currentClass.getDeclaredMethods())
            {
                if(currentMethod.isAnnotationPresent(BeforePhase.class))
                {
                    beforeCallbackEntryHelper.add(
                            currentMethod.getAnnotation(BeforePhase.class).value(), currentMethod);
                }
                else if(currentMethod.isAnnotationPresent(AfterPhase.class))
                {
                    afterCallbackEntryHelper.add(
                            currentMethod.getAnnotation(AfterPhase.class).value(), currentMethod);
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return createPhasesLifecycleCallbackMap(beforeCallbackEntryHelper, afterCallbackEntryHelper);
    }

    private Map<PhaseId, PhasesLifecycleCallbackEntry> createPhasesLifecycleCallbackMap(
            PhasesLifecycleCallbackEntryHelper beforeCallbackEntryHelper,
            PhasesLifecycleCallbackEntryHelper afterCallbackEntryHelper)
    {
        Map<PhaseId, PhasesLifecycleCallbackEntry> result = new HashMap<PhaseId, PhasesLifecycleCallbackEntry>(6);

        result.put(RESTORE_VIEW,
                createCallbackEntry(RESTORE_VIEW, beforeCallbackEntryHelper, afterCallbackEntryHelper));

        result.put(APPLY_REQUEST_VALUES,
                createCallbackEntry(APPLY_REQUEST_VALUES, beforeCallbackEntryHelper, afterCallbackEntryHelper));

        result.put(PROCESS_VALIDATIONS,
                createCallbackEntry(PROCESS_VALIDATIONS, beforeCallbackEntryHelper, afterCallbackEntryHelper));

        result.put(UPDATE_MODEL_VALUES,
                createCallbackEntry(UPDATE_MODEL_VALUES, beforeCallbackEntryHelper, afterCallbackEntryHelper));

        result.put(INVOKE_APPLICATION,
                createCallbackEntry(INVOKE_APPLICATION, beforeCallbackEntryHelper, afterCallbackEntryHelper));

        result.put(RENDER_RESPONSE,
                createCallbackEntry(RENDER_RESPONSE, beforeCallbackEntryHelper, afterCallbackEntryHelper));

        return result;
    }

    private PhasesLifecycleCallbackEntry createCallbackEntry(
            PhaseId phaseId,
            PhasesLifecycleCallbackEntryHelper beforeCallbackEntryHelper,
            PhasesLifecycleCallbackEntryHelper afterCallbackEntryHelper)
    {
        List<Method> beforePhaseCallbacks = beforeCallbackEntryHelper.getMethodsFor(phaseId);
        List<Method> afterPhaseCallbacks = afterCallbackEntryHelper.getMethodsFor(phaseId);

        if(ANY_PHASE.equals(phaseId) ||
                (RESTORE_VIEW.equals(phaseId) && beforePhaseCallbacks != null && !beforePhaseCallbacks.isEmpty()))
        {
            throw unsupportedPhasesLifecycleCallback();
        }

        return new PhasesLifecycleCallbackEntry(beforePhaseCallbacks, afterPhaseCallbacks);
    }
}
