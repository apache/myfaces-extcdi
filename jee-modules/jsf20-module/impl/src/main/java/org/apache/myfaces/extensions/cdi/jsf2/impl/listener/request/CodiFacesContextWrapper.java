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

import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.request.BeforeAfterFacesRequestBroadcaster;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.request.FacesMessageEntry;
import org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation.RedirectedConversationAwareExternalContext;
import org.apache.myfaces.extensions.cdi.jsf2.impl.security.SecurityAwareViewHandler;
import org.apache.myfaces.extensions.cdi.message.api.Message;

import javax.faces.application.FacesMessage;
import javax.faces.application.Application;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Gerhard Petracek
 */
class CodiFacesContextWrapper extends FacesContextWrapper
{
    private FacesContext wrappedFacesContext;

    private ExternalContext wrappedExternalContext;

    private Boolean advancedQualifierRequiredForDependencyInjection;

    private BeforeAfterFacesRequestBroadcaster beforeAfterFacesRequestBroadcaster;

    private boolean temporaryViewRootAwareApplicationWrapperActivated;

    private boolean defaultErrorViewExceptionHandlerActivated;

    CodiFacesContextWrapper(FacesContext wrappedFacesContext)
    {
        this.wrappedFacesContext = wrappedFacesContext;

        this.wrappedExternalContext =
                new RedirectedConversationAwareExternalContext(wrappedFacesContext.getExternalContext());

        setCurrentInstance(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExceptionHandler getExceptionHandler()
    {
        lazyInit();

        ExceptionHandler exceptionHandler = this.wrappedFacesContext.getExceptionHandler();

        if(this.defaultErrorViewExceptionHandlerActivated)
        {
            exceptionHandler = new DefaultErrorViewExceptionHandler(
                    exceptionHandler, this.advancedQualifierRequiredForDependencyInjection);
        }
        return exceptionHandler;
    }

    /**
     * Performs dependency injection manually (if permitted)
     * {@inheritDoc}
     */
    @Override
    public Application getApplication()
    {
        lazyInit();
        Application application = new InjectionAwareApplicationWrapper(wrappedFacesContext.getApplication(),
                this.advancedQualifierRequiredForDependencyInjection);

        if(this.temporaryViewRootAwareApplicationWrapperActivated)
        {
            return new TemporaryViewRootAwareApplicationWrapper(application);
        }
        return application;
    }

    /**
     * Broadcasts the {@link org.apache.myfaces.extensions.cdi.jsf.api.listener.request.AfterFacesRequest} event
     * {@inheritDoc}
     */
    @Override
    public void release()
    {
        if(!this.wrappedFacesContext.getApplication().getResourceHandler().isResourceRequest(this.wrappedFacesContext))
        {
            broadcastAfterFacesRequestEvent();
        }

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

            this.temporaryViewRootAwareApplicationWrapperActivated =
                    ClassDeactivation.isClassActivated(SecurityAwareViewHandler.class) &&
                    ClassDeactivation.isClassActivated(TemporaryViewRootAwareApplicationWrapper.class);

            //deactivate it, if there is no default-error-view available
            //ExceptionHandler used as marker because it's part of the jsf2 api and won't change
            this.defaultErrorViewExceptionHandlerActivated =
                    ViewConfigCache.getDefaultErrorViewConfigDescriptor() != null &&
                            ClassDeactivation.isClassActivated(ExceptionHandler.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalContext getExternalContext()
    {
        return this.wrappedExternalContext;
    }

    /**
     * Adds the {@link FacesMessage} also to a request scoped list to allow to preserve them later on
     * (in case of redirects)
     * {@inheritDoc}
     */
    @Override
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
    public FacesContext getWrapped()
    {
        return this.wrappedFacesContext;
    }
}
