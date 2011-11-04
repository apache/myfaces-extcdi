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

import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.RedirectedConversationAwareExternalContext;
import org.apache.myfaces.extensions.cdi.message.api.Message;

import javax.el.ELContext;
import javax.enterprise.inject.Typed;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link FacesContext} which is needed for wrapping {@link ExternalContext} and which provides additional features
 */
@Typed()
class CodiFacesContextWrapper extends FacesContext
{
    private FacesContext wrappedFacesContext;

    private ExternalContext wrappedExternalContext;

    private Boolean advancedQualifierRequiredForDependencyInjection;

    private BeforeAfterFacesRequestBroadcaster beforeAfterFacesRequestBroadcaster;

    CodiFacesContextWrapper(FacesContext wrappedFacesContext)
    {
        this.wrappedFacesContext = wrappedFacesContext;

        this.wrappedExternalContext =
                new RedirectedConversationAwareExternalContext(wrappedFacesContext.getExternalContext());

        setCurrentInstance(this);
    }

    /**
     * Performs dependency injection manually (if permitted)
     * {@inheritDoc}
     */
    public Application getApplication()
    {
        lazyInit();
        return new InjectionAwareApplicationWrapper(wrappedFacesContext.getApplication(),
                this.advancedQualifierRequiredForDependencyInjection);
    }

    /**
     * Broadcasts the {@link org.apache.myfaces.extensions.cdi.jsf.api.listener.request.AfterFacesRequest} event
     * {@inheritDoc}
     */
    public void release()
    {
        broadcastAfterFacesRequestEvent();
        wrappedFacesContext.release();
    }

    private void broadcastAfterFacesRequestEvent()
    {
        lazyInit();
        if(this.beforeAfterFacesRequestBroadcaster != null)
        {
            this.beforeAfterFacesRequestBroadcaster.broadcastAfterFacesRequestEvent(this);
        }
    }

    private void lazyInit()
    {
        if(this.advancedQualifierRequiredForDependencyInjection == null)
        {
            this.advancedQualifierRequiredForDependencyInjection =
                    CodiUtils.getContextualReferenceByClass(CodiCoreConfig.class)
                            .isAdvancedQualifierRequiredForDependencyInjection();

            if(!ClassDeactivation.isClassActivated(BeforeAfterFacesRequestBroadcaster.class))
            {
                return;
            }

            this.beforeAfterFacesRequestBroadcaster =
                    CodiUtils.getContextualReferenceByClass(BeforeAfterFacesRequestBroadcaster.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ExternalContext getExternalContext()
    {
        return this.wrappedExternalContext;
    }

    /**
     * Adds the {@link FacesMessage} also to a request scoped list to allow to preserve them later on
     * (in case of redirects)
     * {@inheritDoc}
     */
    public void addMessage(String componentId, FacesMessage facesMessage)
    {
        this.wrappedFacesContext.addMessage(componentId, facesMessage);

        //don't store it directly in the window context - it would trigger a too early restore (in some cases)
        Map<String, Object> requestMap = getExternalContext().getRequestMap();

        @SuppressWarnings({"unchecked"})
        List<FacesMessageEntry> facesMessageEntryList =
                (List<FacesMessageEntry>)requestMap.get(Message.class.getName());

        if(facesMessageEntryList == null)
        {
            facesMessageEntryList = new CopyOnWriteArrayList<FacesMessageEntry>();
            requestMap.put(Message.class.getName(), facesMessageEntryList);
        }

        facesMessageEntryList.add(new FacesMessageEntry(componentId, facesMessage));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ELContext getELContext()
    {
        return wrappedFacesContext.getELContext();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getClientIdsWithMessages()
    {
        return wrappedFacesContext.getClientIdsWithMessages();
    }

    /**
     * {@inheritDoc}
     */
    public FacesMessage.Severity getMaximumSeverity()
    {
        return wrappedFacesContext.getMaximumSeverity();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<FacesMessage> getMessages()
    {
        return wrappedFacesContext.getMessages();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<FacesMessage> getMessages(String s)
    {
        return wrappedFacesContext.getMessages(s);
    }

    /**
     * {@inheritDoc}
     */
    public RenderKit getRenderKit()
    {
        return wrappedFacesContext.getRenderKit();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getRenderResponse()
    {
        return wrappedFacesContext.getRenderResponse();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getResponseComplete()
    {
        return wrappedFacesContext.getResponseComplete();
    }

    /**
     * {@inheritDoc}
     */
    public ResponseStream getResponseStream()
    {
        return wrappedFacesContext.getResponseStream();
    }

    /**
     * {@inheritDoc}
     */
    public void setResponseStream(ResponseStream responseStream)
    {
        wrappedFacesContext.setResponseStream(responseStream);
    }

    /**
     * {@inheritDoc}
     */
    public ResponseWriter getResponseWriter()
    {
        return wrappedFacesContext.getResponseWriter();
    }

    /**
     * {@inheritDoc}
     */
    public void setResponseWriter(ResponseWriter responseWriter)
    {
        wrappedFacesContext.setResponseWriter(responseWriter);
    }

    /**
     * {@inheritDoc}
     */
    public UIViewRoot getViewRoot()
    {
        return wrappedFacesContext.getViewRoot();
    }

    /**
     * {@inheritDoc}
     */
    public void setViewRoot(UIViewRoot uiViewRoot)
    {
        wrappedFacesContext.setViewRoot(uiViewRoot);
    }

    /**
     * {@inheritDoc}
     */
    public void renderResponse()
    {
        wrappedFacesContext.renderResponse();
    }

    /**
     * {@inheritDoc}
     */
    public void responseComplete()
    {
        wrappedFacesContext.responseComplete();
    }
}
