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
package org.apache.myfaces.extensions.cdi.jsf.impl.security;

import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.api.security.DefaultErrorPage;
import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.getOrCreateScopedInstanceOfBeanByClass;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import javax.faces.event.ActionListener;
import javax.faces.event.ActionEvent;
import javax.faces.event.AbortProcessingException;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class SecurityViolationAwareActionListener implements ActionListener
{
    private static final Jsf JSF_QUALIFIER = DefaultAnnotation.of(Jsf.class);

    private ActionListener wrapped;

    public SecurityViolationAwareActionListener(ActionListener wrapped)
    {
        this.wrapped = wrapped;
    }

    public void processAction(ActionEvent actionEvent) throws AbortProcessingException
    {
        try
        {
            this.wrapped.processAction(actionEvent);
        }
        catch (FacesException facesException)
        {
            AccessDeniedException exception = extractException(facesException);

            if(exception == null)
            {
                throw facesException;
            }

            Class<? extends ViewConfig> errorPage = null;

            Class<? extends ViewConfig> inlineErrorPage = exception.getErrorPage();

            if(!DefaultErrorPage.class.getName().equals(inlineErrorPage.getName()))
            {
                errorPage = inlineErrorPage;
            }

            if(errorPage == null)
            {
                ViewConfigEntry errorPageEntry = ViewConfigCache.getDefaultErrorPage();

                if(errorPageEntry != null)
                {
                    errorPage = errorPageEntry.getViewDefinitionClass();
                }
            }

            if(errorPage == null)
            {
                throw exception;
            }

            processApplicationSecurityException(exception, errorPage);
        }
    }

    private AccessDeniedException extractException(Throwable exception)
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

    private void processApplicationSecurityException(AccessDeniedException exception,
                                                     Class<? extends ViewConfig> errorPage)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        addViolationsAsMessage(exception.getViolations());

        facesContext.getApplication().getNavigationHandler()
                .handleNavigation(facesContext, null, errorPage.getName());
    }

    private void addViolationsAsMessage(Set<SecurityViolation> violations)
    {
        MessageContext messageContext = getOrCreateScopedInstanceOfBeanByClass(
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
