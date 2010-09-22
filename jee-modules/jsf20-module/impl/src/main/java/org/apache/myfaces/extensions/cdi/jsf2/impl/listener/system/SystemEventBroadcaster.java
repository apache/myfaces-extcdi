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
import javax.faces.event.SystemEvent;
import javax.faces.event.PostConstructApplicationEvent;
import javax.enterprise.event.Event;
import javax.enterprise.context.Dependent;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
@Dependent
@Named
public class SystemEventBroadcaster
{
    static final String BEAN_NAME = "systemEventBroadcaster";

    private final Logger logger = Logger.getLogger(SystemEventBroadcaster.class.getName());

    @Inject
    private Event<PostConstructApplicationEvent> postConstructApplicationEventEvent;

    @Inject
    private ProjectStage projectStage;

    protected SystemEventBroadcaster()
    {
    }

    void broadcastApplicationStartupEvent(PostConstructApplicationEvent postConstructApplicationEvent)
    {
        this.postConstructApplicationEventEvent.fire(postConstructApplicationEvent);
    }

    void logError(SystemEvent systemEvent)
    {
        if(ProjectStage.Development.equals(this.projectStage))
        {
            String messageText = "failed to fire " + PostConstructApplicationEvent.class.getName();

            if(systemEvent == null)
            {
                this.logger.severe(messageText + " - the event is null.");
            }
            else
            {
                this.logger.severe(messageText + " - the event is of type: " + systemEvent.getClass().getName());
            }
        }
    }
}
