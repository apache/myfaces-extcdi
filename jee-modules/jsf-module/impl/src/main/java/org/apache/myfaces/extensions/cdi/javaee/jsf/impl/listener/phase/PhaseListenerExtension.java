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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.listener.phase;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.JsfPhaseListener;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.faces.event.PhaseListener;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Gerhard Petracek
 */
public class PhaseListenerExtension implements Extension
{
    private static List<PhaseListener> phaseListeners = new CopyOnWriteArrayList<PhaseListener>();

    public void filterJsfPhaseListeners(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(JsfPhaseListener.class))
        {
            addPhaseListener(processAnnotatedType);

            processAnnotatedType.veto();
        }
    }

    private void addPhaseListener(ProcessAnnotatedType processAnnotatedType)
    {
        PhaseListener newPhaseListener = createPhaseListenerInstance(processAnnotatedType);

        try
        {
            JsfUtils.registerPhaseListener(newPhaseListener);
        }
        catch (IllegalStateException e)
        {
            //current workaround some servers
            phaseListeners.add(newPhaseListener);
        }
    }

    private PhaseListener createPhaseListenerInstance(ProcessAnnotatedType processAnnotatedType)
    {
        return ClassUtils.tryToInstantiateClass(
                processAnnotatedType.getAnnotatedType().getJavaClass(), PhaseListener.class);
    }

    //current workaround some servers
    public static List<PhaseListener> consumePhaseListeners()
    {
        List<PhaseListener> result = new ArrayList<PhaseListener>(phaseListeners.size());
        result.addAll(phaseListeners);
        phaseListeners.clear();
        return result;
    }
}
