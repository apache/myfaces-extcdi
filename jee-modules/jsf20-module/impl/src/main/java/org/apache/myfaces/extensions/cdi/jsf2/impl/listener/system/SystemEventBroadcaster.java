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

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;

import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.enterprise.event.Event;
import javax.enterprise.context.Dependent;

/**
 * @author Gerhard Petracek
 */
@Dependent
@Named
public class SystemEventBroadcaster
{
    static final String BEAN_NAME = "systemEventBroadcaster";

    @Inject
    private Event<PostConstructApplicationEvent> postConstructApplicationEvent;

    @Inject
    private Event<PreDestroyApplicationEvent> preDestroyApplicationEvent;

    @Inject
    private ProjectStage projectStage;

    protected SystemEventBroadcaster()
    {
    }

    void broadcastApplicationStartupEvent(PostConstructApplicationEvent postConstructApplicationEvent)
    {
        this.postConstructApplicationEvent.fire(postConstructApplicationEvent);
    }

    void broadcastShutdownApplicationEvent(PreDestroyApplicationEvent preDestroyApplicationEvent)
    {
        this.preDestroyApplicationEvent.fire(preDestroyApplicationEvent);
    }
}
