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
package org.apache.myfaces.extensions.cdi.jsf.impl.listener.startup;

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class ApplicationStartupBroadcaster
{
    private static volatile Map<ClassLoader, Boolean> initialized =
            new ConcurrentHashMap<ClassLoader, Boolean>();

    @Inject
    private Event<JsfStartupEvent> applicationStartupEvent;

    /**
     * Broadcasts the {@link org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent}
     */
    public void broadcastStartupEvent()
    {
        if(initialized.containsKey(getClassLoader()))
        {
            return;
        }

        synchronized (ApplicationStartupBroadcaster.class)
        {
            // switch into paranoia mode
            if(initialized.containsKey(getClassLoader()))
            {
                return;
            }

            initialized.put(getClassLoader(), Boolean.TRUE);
        }

        this.applicationStartupEvent.fire(new JsfStartupEvent(FacesContext.getCurrentInstance()));
    }

    private ClassLoader getClassLoader()
    {
        return ClassUtils.getClassLoader(null);
    }
}
