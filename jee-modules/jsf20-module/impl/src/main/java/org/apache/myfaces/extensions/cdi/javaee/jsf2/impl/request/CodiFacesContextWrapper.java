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
package org.apache.myfaces.extensions.cdi.javaee.jsf2.impl.request;

import org.apache.myfaces.extensions.cdi.core.api.manager.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.request.BeforeAfterFacesRequestBroadcaster;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.RedirectedConversationAwareExternalContext;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExceptionHandler;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.render.RenderKit;
import javax.faces.component.UIViewRoot;
import javax.faces.event.PhaseId;
import javax.el.ELContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.context.spi.CreationalContext;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
class CodiFacesContextWrapper extends FacesContext
{
    private FacesContext wrappedFacesContext;

    private BeanManager beanManager;

    private BeforeAfterFacesRequestBroadcaster beforeAfterFacesRequestBroadcaster;

    CodiFacesContextWrapper(FacesContext wrappedFacesContext)
    {
        this.wrappedFacesContext = wrappedFacesContext;

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

    public ExceptionHandler getExceptionHandler()
    {
        return wrappedFacesContext.getExceptionHandler();
    }

    public Application getApplication()
    {
        return wrappedFacesContext.getApplication();
    }

    public Map<Object, Object> getAttributes()
    {
        return wrappedFacesContext.getAttributes();
    }

    public Iterator<String> getClientIdsWithMessages()
    {
        return wrappedFacesContext.getClientIdsWithMessages();
    }

    public PhaseId getCurrentPhaseId()
    {
        return wrappedFacesContext.getCurrentPhaseId();
    }

    public ExternalContext getExternalContext()
    {
        return new RedirectedConversationAwareExternalContext(wrappedFacesContext.getExternalContext());
    }

    public FacesMessage.Severity getMaximumSeverity()
    {
        return wrappedFacesContext.getMaximumSeverity();
    }

    public List<FacesMessage> getMessageList()
    {
        return wrappedFacesContext.getMessageList();
    }

    public List<FacesMessage> getMessageList(String s)
    {
        return wrappedFacesContext.getMessageList(s);
    }

    public Iterator<FacesMessage> getMessages()
    {
        return wrappedFacesContext.getMessages();
    }

    public Iterator<FacesMessage> getMessages(String s)
    {
        return wrappedFacesContext.getMessages(s);
    }

    public PartialViewContext getPartialViewContext()
    {
        return wrappedFacesContext.getPartialViewContext();
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

    public boolean isValidationFailed()
    {
        return wrappedFacesContext.isValidationFailed();
    }

    public void setResponseWriter(ResponseWriter responseWriter)
    {
        wrappedFacesContext.setResponseWriter(responseWriter);
    }

    public UIViewRoot getViewRoot()
    {
        return wrappedFacesContext.getViewRoot();
    }

    public boolean isPostback()
    {
        return wrappedFacesContext.isPostback();
    }

    public boolean isProcessingEvents()
    {
        return wrappedFacesContext.isProcessingEvents();
    }

    public void setViewRoot(UIViewRoot uiViewRoot)
    {
        wrappedFacesContext.setViewRoot(uiViewRoot);
    }

    public void validationFailed()
    {
        wrappedFacesContext.validationFailed();
    }

    public boolean isProjectStage(ProjectStage projectStage)
    {
        return wrappedFacesContext.isProjectStage(projectStage);
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

    public void setCurrentPhaseId(PhaseId phaseId)
    {
        wrappedFacesContext.setCurrentPhaseId(phaseId);
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler)
    {
        wrappedFacesContext.setExceptionHandler(exceptionHandler);
    }

    public void setProcessingEvents(boolean b)
    {
        wrappedFacesContext.setProcessingEvents(b);
    }

    private void initBroadcaster()
    {
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
