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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.listener.request;

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.RedirectedConversationAwareExternalContext;

import javax.el.ELContext;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO move to a shared package
 *
 * @author Gerhard Petracek
 */
class CodiFacesContextWrapper extends FacesContext
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

    private void broadcastBeforeFacesRequestEvent()
    {
        this.beforeAfterFacesRequestBroadcaster.broadcastBeforeFacesRequestEvent(this);
    }

    private void broadcastAfterFacesRequestEvent()
    {
        this.beforeAfterFacesRequestBroadcaster.broadcastAfterFacesRequestEvent(this);
    }

    public ELContext getELContext()
    {
        return wrappedFacesContext.getELContext();
    }

    public Application getApplication()
    {
        return wrappedFacesContext.getApplication();
    }

    public Iterator<String> getClientIdsWithMessages()
    {
        return wrappedFacesContext.getClientIdsWithMessages();
    }

    public ExternalContext getExternalContext()
    {
        return wrappedExternalContext;
    }

    public FacesMessage.Severity getMaximumSeverity()
    {
        return wrappedFacesContext.getMaximumSeverity();
    }

    public Iterator<FacesMessage> getMessages()
    {
        return wrappedFacesContext.getMessages();
    }

    public Iterator<FacesMessage> getMessages(String s)
    {
        return wrappedFacesContext.getMessages(s);
    }

    public RenderKit getRenderKit()
    {
        return wrappedFacesContext.getRenderKit();
    }

    public boolean getRenderResponse()
    {
        return wrappedFacesContext.getRenderResponse();
    }

    public boolean getResponseComplete()
    {
        return wrappedFacesContext.getResponseComplete();
    }

    public ResponseStream getResponseStream()
    {
        return wrappedFacesContext.getResponseStream();
    }

    public void setResponseStream(ResponseStream responseStream)
    {
        wrappedFacesContext.setResponseStream(responseStream);
    }

    public ResponseWriter getResponseWriter()
    {
        return wrappedFacesContext.getResponseWriter();
    }

    public void setResponseWriter(ResponseWriter responseWriter)
    {
        wrappedFacesContext.setResponseWriter(responseWriter);
    }

    public UIViewRoot getViewRoot()
    {
        return wrappedFacesContext.getViewRoot();
    }

    public void setViewRoot(UIViewRoot uiViewRoot)
    {
        wrappedFacesContext.setViewRoot(uiViewRoot);
    }

    public void addMessage(String s, FacesMessage facesMessage)
    {
        //TODO
        wrappedFacesContext.addMessage(s, facesMessage);
    }

    public void release()
    {
        broadcastAfterFacesRequestEvent();
        wrappedFacesContext.release();
    }

    public void renderResponse()
    {
        wrappedFacesContext.renderResponse();
    }

    public void responseComplete()
    {
        wrappedFacesContext.responseComplete();
    }

    private void initBroadcaster()
    {
        Set<? extends Bean> broadcasterBeans = this.beanManager.getBeans(BeforeAfterFacesRequestBroadcaster.class);

        if (broadcasterBeans.size() != 1)
        {
            //TODO add an exception to the exception context
            return;
        }

        CreationalContext<BeforeAfterFacesRequestBroadcaster> creationalContext;

        for (Bean<BeforeAfterFacesRequestBroadcaster> requestHandlerBean : broadcasterBeans)
        {
            creationalContext = beanManager.createCreationalContext(requestHandlerBean);

            this.beforeAfterFacesRequestBroadcaster = requestHandlerBean.create(creationalContext);
        }
    }
}
