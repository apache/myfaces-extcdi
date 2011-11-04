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
package org.apache.myfaces.extensions.cdi.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolationHandler;
import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;
import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.getContextualReferenceByClass;

import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;

import javax.faces.context.FacesContext;
import javax.enterprise.inject.Typed;
import java.util.Set;

/**
 * Helper for {@link org.apache.myfaces.extensions.cdi.core.api.security.Secured}
 */
@Typed()
public abstract class SecurityUtils
{
    private static final Jsf JSF_QUALIFIER = DefaultAnnotation.of(Jsf.class);

    /**
     * Constructor which prevents the instantiation of this class
     */
    private SecurityUtils()
    {
        // prevent instantiation
    }

    /**
     * Processes a security violation without triggering the navigation to the error page
     * @param runtimeException current exception
     * @return error view which can be used e.g. for manual navigation
     */
    public static Class<? extends ViewConfig> handleSecurityViolationWithoutNavigation(
            RuntimeException runtimeException)
    {
        return tryToHandleSecurityViolation(runtimeException, false);
    }

    /**
     * Processes a security violation including the navigation to the error page
     * @param runtimeException current exception
     */
    public static void tryToHandleSecurityViolation(RuntimeException runtimeException)
    {
        tryToHandleSecurityViolation(runtimeException, true);
    }

    private static Class<? extends ViewConfig> tryToHandleSecurityViolation(RuntimeException runtimeException,
                                                                            boolean allowNavigation)
    {
        AccessDeniedException exception = extractException(runtimeException);

        if(exception == null)
        {
            throw runtimeException;
        }

        Class<? extends ViewConfig> errorView = null;

        Class<? extends ViewConfig> inlineErrorView = exception.getErrorView();

        if(inlineErrorView != null && !DefaultErrorView.class.getName().equals(inlineErrorView.getName()))
        {
            errorView = inlineErrorView;
        }

        if(errorView == null)
        {
            ViewConfigDescriptor errorPageDescriptor = ViewConfigCache.getDefaultErrorViewConfigDescriptor();

            if(errorPageDescriptor != null)
            {
                errorView = errorPageDescriptor.getViewConfig();
            }
        }

        if(errorView == null)
        {
            throw exception;
        }

        processApplicationSecurityException(exception, errorView, allowNavigation);
        return errorView;
    }

    private static AccessDeniedException extractException(Throwable exception)
    {
        if(exception == null)
        {
            return null;
        }

        if(exception instanceof AccessDeniedException)
        {
            return (AccessDeniedException)exception;
        }

        return extractException(exception.getCause());
    }

    private static void processApplicationSecurityException(AccessDeniedException exception,
                                                            Class<? extends ViewConfig> errorPage,
                                                            boolean allowNavigation)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        SecurityViolationHandler securityViolationHandler =
                CodiUtils.getContextualReferenceByClass(SecurityViolationHandler.class, true);

        if(securityViolationHandler != null)
        {
            //optional (custom handler) - allows to handle custom implementations of SecurityViolation
            securityViolationHandler.processSecurityViolations(exception.getViolations());
        }
        else
        {
            addViolationsAsMessage(exception.getViolations());
        }

        if(allowNavigation)
        {
            facesContext.getApplication().getNavigationHandler()
                    .handleNavigation(facesContext, null, errorPage.getName());
        }
    }

    private static void addViolationsAsMessage(Set<SecurityViolation> violations)
    {
        MessageContext messageContext = getContextualReferenceByClass(
                MessageContext.class, true, JSF_QUALIFIER);

        if(messageContext == null)
        {
            return;
        }

        for(SecurityViolation violation : violations)
        {
            messageContext.message().text(violation.getReason()).payload(MessageSeverity.ERROR).add();
        }
    }
}
