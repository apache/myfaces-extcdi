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
package org.apache.myfaces.extensions.cdi.jsf2.impl.listener.request;


import org.apache.myfaces.extensions.cdi.jsf2.impl.security.SecurityAwareViewHandler;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.PreDestroyViewMapEvent;
import javax.faces.event.SystemEvent;

/**
 * needed due to EXTCDI-167
 */
class TemporaryViewRootAwareApplicationWrapper extends ApplicationWrapper
{
    private Application wrapped;

    TemporaryViewRootAwareApplicationWrapper(Application wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    public Application getWrapped()
    {
        return this.wrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishEvent(FacesContext facesContext, Class<? extends SystemEvent> systemEventClass, Object source)
    {
        if(!PreDestroyViewMapEvent.class.isAssignableFrom(systemEventClass) ||
                isPreDestroyViewMapEventAllowed(facesContext))
        {
            super.publishEvent(facesContext, systemEventClass, source);
        }
    }

    private boolean isPreDestroyViewMapEventAllowed(FacesContext facesContext)
    {
        return !Boolean.TRUE.equals(
                facesContext.getExternalContext().getRequestMap().get(SecurityAwareViewHandler.class.getName()));
    }
}
