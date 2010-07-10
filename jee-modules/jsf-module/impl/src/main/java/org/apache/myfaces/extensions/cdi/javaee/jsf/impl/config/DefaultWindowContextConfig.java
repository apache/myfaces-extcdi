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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.config.Config;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContextConfig;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.api.ConfigParameter.*;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Gerhard Petracek
 */
@Singleton
public class DefaultWindowContextConfig extends WindowContextConfig
{
    private static final long serialVersionUID = -1065123725125153533L;

    private Boolean configInitialized;

    @Produces
    @Named
    @Dependent
    @Config(WindowContextConfig.class)
    public boolean getRequestParameterSupported()
    {
        return isGetRequestParameterSupported();
    }

    @Produces
    @Named
    @Dependent
    @Config(WindowContextConfig.class)
    public Integer windowContextTimeoutInMinutes()
    {
        return getWindowContextTimeoutInMinutes();
    }

    @Produces
    @Named
    @Dependent
    @Config(WindowContextConfig.class)
    public Integer conversationTimeoutInMinutes()
    {
        return getConversationTimeoutInMinutes();
    }

    public boolean isGetRequestParameterSupported()
    {
        lazyInit();
        return getAttribute(GET_REQUEST_PARAMETER_ENABLED, Boolean.class);
    }

    public int getWindowContextTimeoutInMinutes()
    {
        lazyInit();
        return getAttribute(WINDOW_CONTEXT_TIMEOUT, Integer.class);
    }

    public int getConversationTimeoutInMinutes()
    {
        lazyInit();
        return getAttribute(GROUPED_CONVERSATION_TIMEOUT, Integer.class);
    }

    private void lazyInit()
    {
        if (configInitialized == null)
        {
            init(FacesContext.getCurrentInstance());
        }
    }

    private synchronized void init(FacesContext facesContext)
    {
        if (configInitialized != null || facesContext == null)
        {
            return;
        }

        configInitialized = true;

        initGetRequestParameterEnabled(facesContext);
        initWindowContextConversationTimeout(facesContext);
        initGroupedConversationTimeout(facesContext);
    }

    private void initGetRequestParameterEnabled(FacesContext facesContext)
    {
        boolean requestParameterEnabled = GET_REQUEST_PARAMETER_ENABLED_DEFAULT;

        String requestParameterEnabledString =
                facesContext.getExternalContext().getInitParameter(GET_REQUEST_PARAMETER_ENABLED);

        if (requestParameterEnabledString == null)
        {
            setAttribute(GET_REQUEST_PARAMETER_ENABLED, requestParameterEnabled);
            return;
        }

        requestParameterEnabledString = requestParameterEnabledString.trim();

        if ("".equals(requestParameterEnabledString))
        {
            setAttribute(GET_REQUEST_PARAMETER_ENABLED, requestParameterEnabled);
            return;
        }

        setAttribute(GET_REQUEST_PARAMETER_ENABLED, Boolean.parseBoolean(requestParameterEnabledString));
    }

    private void initWindowContextConversationTimeout(FacesContext facesContext)
    {
        int timeoutInMinutes = WINDOW_CONTEXT_TIMEOUT_DEFAULT;

        String timeoutString = facesContext.getExternalContext().getInitParameter(WINDOW_CONTEXT_TIMEOUT);

        if (timeoutString == null)
        {
            setAttribute(WINDOW_CONTEXT_TIMEOUT, timeoutInMinutes);
            return;
        }

        timeoutString = timeoutString.trim();

        if ("".equals(timeoutString))
        {
            setAttribute(WINDOW_CONTEXT_TIMEOUT, timeoutInMinutes);
            return;
        }

        setAttribute(WINDOW_CONTEXT_TIMEOUT, Integer.parseInt(timeoutString));
    }

    private void initGroupedConversationTimeout(FacesContext facesContext)
    {
        int timeoutInMinutes = GROUPED_CONVERSATION_TIMEOUT_DEFAULT;

        String timeoutString = facesContext.getExternalContext().getInitParameter(GROUPED_CONVERSATION_TIMEOUT);

        if (timeoutString == null)
        {
            setAttribute(GROUPED_CONVERSATION_TIMEOUT, timeoutInMinutes);
            return;
        }

        timeoutString = timeoutString.trim();

        if ("".equals(timeoutString))
        {
            setAttribute(GROUPED_CONVERSATION_TIMEOUT, timeoutInMinutes);
            return;
        }

        setAttribute(GROUPED_CONVERSATION_TIMEOUT, Integer.parseInt(timeoutString));
    }
}