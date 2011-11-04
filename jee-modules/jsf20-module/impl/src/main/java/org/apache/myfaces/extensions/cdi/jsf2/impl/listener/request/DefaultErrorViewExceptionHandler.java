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

import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.navigation.ViewNavigationHandler;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.context.ContextNotActiveException;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.application.ViewExpiredException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;

/**
 * Needed for autom. usage of {@link DefaultErrorView}
 */
class DefaultErrorViewExceptionHandler extends ExceptionHandlerWrapper
{
    private ExceptionHandler wrapped;
    private boolean advancedQualifierRequiredForDependencyInjection;

    private ViewNavigationHandler viewNavigationHandler;

    /**
     * Constructor used by proxy libs
     */
    protected DefaultErrorViewExceptionHandler()
    {
    }

    DefaultErrorViewExceptionHandler(ExceptionHandler wrapped, boolean advancedQualifierRequiredForDependencyInjection)
    {
        this.wrapped = wrapped;
        this.advancedQualifierRequiredForDependencyInjection = advancedQualifierRequiredForDependencyInjection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle() throws FacesException
    {
        lazyInit();
        Iterator<ExceptionQueuedEvent> exceptionQueuedEventIterator = getUnhandledExceptionQueuedEvents().iterator();

        while (exceptionQueuedEventIterator.hasNext())
        {
            ExceptionQueuedEventContext exceptionQueuedEventContext =
                    (ExceptionQueuedEventContext) exceptionQueuedEventIterator.next().getSource();

            @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
            Throwable throwable = exceptionQueuedEventContext.getException();

            String viewId = null;

            if (throwable instanceof ViewExpiredException)
            {
                viewId = ((ViewExpiredException) throwable).getViewId();
            }
            else if(throwable instanceof ContextNotActiveException)
            {
                FacesContext facesContext = exceptionQueuedEventContext.getContext();
                Flash flash =  facesContext.getExternalContext().getFlash();

                //the error page uses a cdi scope which isn't active as well
                if(flash.containsKey(ContextNotActiveException.class.getName()))
                {
                    break;
                }

                if(facesContext.getViewRoot() != null)
                {
                    viewId = facesContext.getViewRoot().getViewId();
                }
            }

            if(viewId != null)
            {
                FacesContext facesContext = exceptionQueuedEventContext.getContext();
                UIViewRoot uiViewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, viewId);

                if (uiViewRoot == null)
                {
                    continue;
                }

                if(facesContext.isProjectStage(ProjectStage.Development) ||
                        ProjectStageProducer.getInstance().getProjectStage() ==
                                org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage.Development)
                {
                    throwable.printStackTrace();
                }

                facesContext.setViewRoot(uiViewRoot);
                exceptionQueuedEventIterator.remove();

                Flash flash =  facesContext.getExternalContext().getFlash();
                flash.put(throwable.getClass().getName(), throwable);
                flash.keep(throwable.getClass().getName());

                this.viewNavigationHandler.navigateTo(DefaultErrorView.class);

                break;
            }
        }

        this.wrapped.handle();
    }

    private void lazyInit()
    {
        if(this.viewNavigationHandler == null)
        {
            tryToInjectFields(this.wrapped);
            this.viewNavigationHandler = CodiUtils.getContextualReferenceByClass(ViewNavigationHandler.class);
        }
    }

    private void tryToInjectFields(ExceptionHandler exceptionHandler)
    {
        CodiUtils.injectFields(exceptionHandler, this.advancedQualifierRequiredForDependencyInjection);

        if(exceptionHandler instanceof ExceptionHandlerWrapper)
        {
            tryToInjectFields(((ExceptionHandlerWrapper) exceptionHandler).getWrapped());
        }
    }

    /**
     * {@inheritDoc}
     */
    public ExceptionHandler getWrapped()
    {
        lazyInit();
        return wrapped;
    }
}
