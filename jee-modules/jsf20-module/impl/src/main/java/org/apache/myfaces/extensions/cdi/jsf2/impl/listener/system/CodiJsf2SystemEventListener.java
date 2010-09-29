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
package org.apache.myfaces.extensions.cdi.jsf2.impl.listener.system;

import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.getOrCreateScopedInstanceOfBeanByName;
import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.faces.event.SystemEventListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.faces.application.Application;

/**
 * @author Gerhard Petracek
 */
public class CodiJsf2SystemEventListener implements SystemEventListener, Deactivatable
{
    private final boolean deactivated;

    public CodiJsf2SystemEventListener()
    {
        this.deactivated = !isActivated();
    }

    public boolean isListenerForSource(Object source)
    {
        return !this.deactivated && source instanceof Application;
    }

    public void processEvent(SystemEvent systemEvent)
    {
        if(systemEvent instanceof PostConstructApplicationEvent)
        {
            resolveBroadcaster().broadcastApplicationStartupEvent((PostConstructApplicationEvent)systemEvent);
        }
        else if(systemEvent instanceof PreDestroyApplicationEvent)
        {
            resolveBroadcaster().broadcastShutdownApplicationEvent((PreDestroyApplicationEvent)systemEvent);
        }
    }

    private SystemEventBroadcaster resolveBroadcaster()
    {
        //cdi has to inject the event
        return getOrCreateScopedInstanceOfBeanByName(SystemEventBroadcaster.BEAN_NAME, SystemEventBroadcaster.class);
    }

    public boolean isActivated()
    {
        return ClassUtils.isClassActivated(getClass());
    }
}
