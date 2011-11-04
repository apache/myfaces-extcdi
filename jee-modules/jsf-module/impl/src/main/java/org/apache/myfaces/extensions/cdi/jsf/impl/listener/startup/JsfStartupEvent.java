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

import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEvent;

import javax.enterprise.inject.Typed;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

/**
 * Event which gets triggered lazily as soon as the JSF container is up and running.
 * Attention: the concrete point depends on the JSF implementation
 */
@Typed()
class JsfStartupEvent implements StartupEvent
{
    private FacesContext facesContext;

    JsfStartupEvent(FacesContext facesContext)
    {
        this.facesContext = facesContext;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Serializable> getApplicationParameters()
    {
        return this.facesContext.getExternalContext().getInitParameterMap();
    }
}
