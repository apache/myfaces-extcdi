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

import javax.faces.event.SystemEventListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.application.Application;

/**
 * @author Gerhard Petracek
 */
public class CodiStartupListener implements SystemEventListener
{
    public boolean isListenerForSource(Object source)
    {
        return source instanceof Application;
    }

    public void processEvent(SystemEvent systemEvent)
    {
        if(systemEvent instanceof PostConstructApplicationEvent)
        {
            resolveBroadcaster().broadcastApplicationStartupEvent((PostConstructApplicationEvent)systemEvent);
        }
        else
        {
            resolveBroadcaster().logError(systemEvent);
        }
    }

    private SystemEventBroadcaster resolveBroadcaster()
    {
        //cdi has to inject the event
        return getOrCreateScopedInstanceOfBeanByName(SystemEventBroadcaster.BEAN_NAME, SystemEventBroadcaster.class);
    }
}
