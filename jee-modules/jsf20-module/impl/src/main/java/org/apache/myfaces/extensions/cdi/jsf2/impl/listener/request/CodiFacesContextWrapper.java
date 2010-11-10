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

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.request.BeforeAfterFacesRequestBroadcaster;
import org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation.RedirectedConversationAwareExternalContext;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.application.Application;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
class CodiFacesContextWrapper extends FacesContextWrapper
{
    private FacesContext wrappedFacesContext;

    private ExternalContext wrappedExternalContext;

    private BeanManager beanManager;

    private BeforeAfterFacesRequestBroadcaster beforeAfterFacesRequestBroadcaster;

    CodiFacesContextWrapper(FacesContext wrappedFacesContext)
    {
        this.wrappedFacesContext = wrappedFacesContext;

        this.wrappedExternalContext =
                new RedirectedConversationAwareExternalContext(wrappedFacesContext.getExternalContext());

        setCurrentInstance(this);
        //(currently) causes issue in combination with geronimo 3.0-m1
        init();
    }

    private void init()
    {
        this.beanManager = BeanManagerProvider.getInstance().getBeanManager();

        initBroadcaster();

        broadcastBeforeFacesRequestEvent();
    }

    @Override
    public Application getApplication()
    {
        return new InjectionAwareApplicationWrapper(wrappedFacesContext.getApplication());
    }

    private void broadcastBeforeFacesRequestEvent()
    {
        if(this.beforeAfterFacesRequestBroadcaster != null)
        {
            this.beforeAfterFacesRequestBroadcaster.broadcastBeforeFacesRequestEvent(this);
        }
    }

    private void broadcastAfterFacesRequestEvent()
    {
        if(this.beforeAfterFacesRequestBroadcaster != null)
        {
            this.beforeAfterFacesRequestBroadcaster.broadcastAfterFacesRequestEvent(this);
        }
    }

    public FacesContext getWrapped()
    {
        return this.wrappedFacesContext;
    }

    public ExternalContext getExternalContext()
    {
        return wrappedExternalContext;
    }

    public void release()
    {
        broadcastAfterFacesRequestEvent();
        wrappedFacesContext.release();
    }

    private void initBroadcaster()
    {
        if(!ClassDeactivation.isClassActivated(BeforeAfterFacesRequestBroadcaster.class))
        {
            return;
        }

        Set<? extends Bean> broadcasterBeans = this.beanManager.getBeans(BeforeAfterFacesRequestBroadcaster.class);

        if(broadcasterBeans.size() != 1)
        {
            //TODO add an exception to the exception context
            return;
        }

        CreationalContext<BeforeAfterFacesRequestBroadcaster> creationalContext;

        for(Bean<BeforeAfterFacesRequestBroadcaster> requestHandlerBean : broadcasterBeans)
        {
            creationalContext = beanManager.createCreationalContext(requestHandlerBean);

            this.beforeAfterFacesRequestBroadcaster = requestHandlerBean.create(creationalContext);
        }
    }
}
