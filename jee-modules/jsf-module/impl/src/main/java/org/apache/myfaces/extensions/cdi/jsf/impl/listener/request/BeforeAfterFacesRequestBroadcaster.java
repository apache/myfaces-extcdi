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
package org.apache.myfaces.extensions.cdi.jsf.impl.listener.request;

import org.apache.myfaces.extensions.cdi.jsf.api.listener.request.BeforeFacesRequest;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.request.AfterFacesRequest;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
public class BeforeAfterFacesRequestBroadcaster
{
    @Inject
    @BeforeFacesRequest
    private Event<FacesContext> beforeFacesRequestEvent;

    @Inject
    @AfterFacesRequest
    private Event<FacesContext> afterFacesRequestEvent;

    /**
     * Broadcasts the {@link BeforeFacesRequest} event
     * @param facesContext current faces-context
     */
    public void broadcastBeforeFacesRequestEvent(FacesContext facesContext)
    {
        this.beforeFacesRequestEvent.fire(facesContext);
    }

    /**
     * Broadcasts the {@link AfterFacesRequest} event
     * @param facesContext current faces-context
     */
    public void broadcastAfterFacesRequestEvent(FacesContext facesContext)
    {
        this.afterFacesRequestEvent.fire(facesContext);
    }
}
