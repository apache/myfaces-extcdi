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
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.api.ConfigParameter.*;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.DefaultWindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowContextFactory;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowContextManagerFactory;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationFactory;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.WindowContextQuotaHandler;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.JsfAwareConversationFactory;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.DefaultWindowContextQuotaHandler;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultWindowContextConfig extends JsfAwareWindowContextConfig
{
    private static final long serialVersionUID = -1065123725125153533L;

    private Boolean configInitialized;

    @Produces
    @Named
    @Dependent
    @Config(WindowContextConfig.class)
    public boolean getUrlParameterSupported()
    {
        return isUrlParameterSupported();
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

    //TODO <- add methods for all config parameters ->

    public boolean isUrlParameterSupported()
    {
        lazyInit();
        return getAttribute(URL_PARAMETER_ENABLED, Boolean.class);
    }

    public boolean isUnknownWindowIdsAllowed()
    {
        lazyInit();
        return getAttribute(ALLOW_UNKNOWN_WINDOW_IDS, Boolean.class);
    }

    public int getWindowContextTimeoutInMinutes()
    {
        lazyInit();
        return getAttribute(WINDOW_CONTEXT_TIMEOUT, Integer.class);
    }

    public int getConversationTimeoutInMinutes()
    {
        lazyInit();
        return getAttribute(CONVERSATION_TIMEOUT, Integer.class);
    }

    public boolean isScopeBeanEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_SCOPE_BEAN_EVENT, Boolean.class);
    }

    public boolean isBeanAccessEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_BEAN_ACCESS_EVENT, Boolean.class);
    }

    public boolean isUnscopeBeanEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_UNSCOPE_BEAN_EVENT, Boolean.class);
    }

    public int getMaxWindowContextCount()
    {
        lazyInit();
        return getAttribute(MAX_WINDOW_CONTEXT_COUNT, Integer.class);
    }

    public WindowHandler getWindowHandler()
    {
        return new DefaultWindowHandler(isUrlParameterSupported())
        {
            private static final long serialVersionUID = 7376499174252256735L;
        };
    }

    public ConversationFactory getConversationFactory()
    {
        //TODO add config parameter
        return new JsfAwareConversationFactory();
    }

    public WindowContextFactory getWindowContextFactory()
    {
        //TODO add config parameter
        //we don't need a default implementation
        return null;
    }

    public WindowContextManagerFactory getWindowContextManagerFactory()
    {
        //TODO add config parameter
        //we don't need a default implementation
        return null;
    }

    public WindowContextQuotaHandler getWindowContextQuotaHandler()
    {
        //TODO add config parameter
        return new DefaultWindowContextQuotaHandler(getMaxWindowContextCount());
    }

    public boolean isInitialRedirectDisable()
    {
        lazyInit();
        return getAttribute(DISABLE_INITIAL_REDIRECT, Boolean.class);
    }

    private void lazyInit()
    {
        if (configInitialized == null)
        {
            init(FacesContext.getCurrentInstance(), CodiUtils.getCurrentProjectStage());
        }
    }

    private synchronized void init(FacesContext facesContext, ProjectStage currentProjectStage)
    {
        if (configInitialized != null || facesContext == null)
        {
            return;
        }

        configInitialized = true;

        initUrlParameterEnabled(facesContext);
        initAllowUnknownWindowIds(facesContext);
        initMaxWindowContextCount(facesContext, ProjectStage.SystemTest.equals(currentProjectStage));
        initWindowContextTimeout(facesContext);
        initConversationTimeout(facesContext);
        initDisableInitialRedirect(facesContext);
        initConversatonEvents(facesContext);
    }

    private void initUrlParameterEnabled(FacesContext facesContext)
    {
        initConfig(facesContext, URL_PARAMETER_ENABLED, new BooleanConfigValueParser(), URL_PARAMETER_ENABLED_DEFAULT);
    }

    private void initAllowUnknownWindowIds(FacesContext facesContext)
    {
        initConfig(facesContext,
                ALLOW_UNKNOWN_WINDOW_IDS, new BooleanConfigValueParser(), ALLOW_UNKNOWN_WINDOW_IDS_DEFAULT);
    }

    private void initMaxWindowContextCount(FacesContext facesContext, boolean inProjectStageSystemTest)
    {
        int defaultMaxCount = MAX_WINDOW_CONTEXT_COUNT_DEFAULT;

        if(inProjectStageSystemTest)
        {
            defaultMaxCount = Integer.MAX_VALUE;
        }

        initConfig(facesContext, MAX_WINDOW_CONTEXT_COUNT, new IntegerConfigValueParser(), defaultMaxCount);
    }

    private void initWindowContextTimeout(FacesContext facesContext)
    {
        initConfig(facesContext,
                WINDOW_CONTEXT_TIMEOUT, new IntegerConfigValueParser(), WINDOW_CONTEXT_TIMEOUT_DEFAULT);
    }

    private void initConversationTimeout(FacesContext facesContext)
    {
        initConfig(facesContext, CONVERSATION_TIMEOUT, new IntegerConfigValueParser(), CONVERSATION_TIMEOUT_DEFAULT);
    }

    private void initDisableInitialRedirect(FacesContext facesContext)
    {
        initConfig(facesContext,
                DISABLE_INITIAL_REDIRECT, new BooleanConfigValueParser(), DISABLE_INITIAL_REDIRECT_DEFAULT);
    }

    private void initConversatonEvents(FacesContext facesContext)
    {
        initScopeBeanEvent(facesContext);
        initBeanAccessEvent(facesContext);
        initUnscopeBeanEvent(facesContext);
    }

    private void initScopeBeanEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_SCOPE_BEAN_EVENT, new BooleanConfigValueParser(), ENABLE_SCOPE_BEAN_EVENT_DEFAULT);
    }

    private void initBeanAccessEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_BEAN_ACCESS_EVENT, new BooleanConfigValueParser(), ENABLE_BEAN_ACCESS_EVENT_DEFAULT);
    }

    private void initUnscopeBeanEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_UNSCOPE_BEAN_EVENT, new BooleanConfigValueParser(), ENABLE_UNSCOPE_BEAN_EVENT_DEFAULT);
    }

    protected <T> void initConfig(FacesContext facesContext,
                                  String configKey,
                                  ConfigValueParser<T> configValueParser,
                                  T defaultValue)
    {
        String customValue = facesContext.getExternalContext().getInitParameter(configKey);

        if (customValue == null)
        {
            setAttribute(configKey, defaultValue);
            return;
        }

        customValue = customValue.trim();

        if ("".equals(customValue))
        {
            setAttribute(configKey, defaultValue);
            return;
        }

        if(configValueParser == null)
        {
            setAttribute(configKey, customValue);
        }
        else
        {
            setAttribute(configKey, configValueParser.parse(customValue));
        }
    }
}