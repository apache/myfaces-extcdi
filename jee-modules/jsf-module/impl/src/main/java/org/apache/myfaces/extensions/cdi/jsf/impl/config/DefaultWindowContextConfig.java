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
package org.apache.myfaces.extensions.cdi.jsf.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.config.AbstractAttributeAware;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ADD_WINDOW_ID_TO_ACTION_URL_ENABLED;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ADD_WINDOW_ID_TO_ACTION_URL_ENABLED_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ALLOW_UNKNOWN_WINDOW_IDS;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ALLOW_UNKNOWN_WINDOW_IDS_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.CONVERSATION_TIMEOUT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.CONVERSATION_TIMEOUT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.DISABLE_INITIAL_REDIRECT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.DISABLE_INITIAL_REDIRECT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_ACCESS_BEAN_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_ACCESS_BEAN_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_CLOSE_CONVERSATION_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_CLOSE_CONVERSATION_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_CLOSE_WINDOW_CONTEXT_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_CLOSE_WINDOW_CONTEXT_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_CREATE_WINDOW_CONTEXT_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_CREATE_WINDOW_CONTEXT_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_RESTART_CONVERSATION_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_RESTART_CONVERSATION_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_SCOPE_BEAN_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_SCOPE_BEAN_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_START_CONVERSATION_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_START_CONVERSATION_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_UNSCOPE_BEAN_EVENT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.ENABLE_UNSCOPE_BEAN_EVENT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.MAX_WINDOW_CONTEXT_COUNT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.MAX_WINDOW_CONTEXT_COUNT_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.URL_PARAMETER_ENABLED;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.URL_PARAMETER_ENABLED_DEFAULT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.WINDOW_CONTEXT_TIMEOUT;
import static org.apache.myfaces.extensions.cdi.jsf.api.ConfigParameter.WINDOW_CONTEXT_TIMEOUT_DEFAULT;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultWindowContextConfig extends AbstractAttributeAware implements JsfAwareWindowContextConfig
{
    private static final long serialVersionUID = -1065123725125153533L;

    private Boolean configInitialized;

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

    public boolean isAddWindowIdToActionUrlsEnabled()
    {
        lazyInit();
        return getAttribute(ADD_WINDOW_ID_TO_ACTION_URL_ENABLED, Boolean.class);
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

    public boolean isAccessBeanEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_ACCESS_BEAN_EVENT, Boolean.class);
    }

    public boolean isUnscopeBeanEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_UNSCOPE_BEAN_EVENT, Boolean.class);
    }

    public boolean isStartConversationEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_START_CONVERSATION_EVENT, Boolean.class);
    }

    public boolean isCloseConversationEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_CLOSE_CONVERSATION_EVENT, Boolean.class);
    }

    public boolean isRestartConversationEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_RESTART_CONVERSATION_EVENT, Boolean.class);
    }

    public int getMaxWindowContextCount()
    {
        lazyInit();
        return getAttribute(MAX_WINDOW_CONTEXT_COUNT, Integer.class);
    }

    public boolean isCreateWindowContextEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_CREATE_WINDOW_CONTEXT_EVENT, Boolean.class);
    }

    public boolean isCloseWindowContextEventEnable()
    {
        lazyInit();
        return getAttribute(ENABLE_CLOSE_WINDOW_CONTEXT_EVENT, Boolean.class);
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
        initActionUrlEncoding(facesContext);
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
        initAccessBeanEvent(facesContext);
        initUnscopeBeanEvent(facesContext);

        initStartConversationEvent(facesContext);
        initCloseConversationEvent(facesContext);
        initRestartConversationEvent(facesContext);

        initCreateWindowContextEvent(facesContext);
        initCloseWindowContextEvent(facesContext);
    }

    private void initStartConversationEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_START_CONVERSATION_EVENT, new BooleanConfigValueParser(),
                ENABLE_START_CONVERSATION_EVENT_DEFAULT);
    }

    private void initCloseConversationEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_CLOSE_CONVERSATION_EVENT, new BooleanConfigValueParser(),
                ENABLE_CLOSE_CONVERSATION_EVENT_DEFAULT);
    }

    private void initRestartConversationEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_RESTART_CONVERSATION_EVENT, new BooleanConfigValueParser(),
                ENABLE_RESTART_CONVERSATION_EVENT_DEFAULT);
    }

    private void initCreateWindowContextEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_CREATE_WINDOW_CONTEXT_EVENT, new BooleanConfigValueParser(),
                ENABLE_CREATE_WINDOW_CONTEXT_EVENT_DEFAULT);
    }

    private void initCloseWindowContextEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_CLOSE_WINDOW_CONTEXT_EVENT, new BooleanConfigValueParser(),
                ENABLE_CLOSE_WINDOW_CONTEXT_EVENT_DEFAULT);
    }

    private void initScopeBeanEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_SCOPE_BEAN_EVENT, new BooleanConfigValueParser(), ENABLE_SCOPE_BEAN_EVENT_DEFAULT);
    }

    private void initAccessBeanEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_ACCESS_BEAN_EVENT, new BooleanConfigValueParser(), ENABLE_ACCESS_BEAN_EVENT_DEFAULT);
    }

    private void initUnscopeBeanEvent(FacesContext facesContext)
    {
        initConfig(facesContext,
                ENABLE_UNSCOPE_BEAN_EVENT, new BooleanConfigValueParser(), ENABLE_UNSCOPE_BEAN_EVENT_DEFAULT);
    }

    private void initActionUrlEncoding(FacesContext facesContext)
    {
        initConfig(facesContext,
                ADD_WINDOW_ID_TO_ACTION_URL_ENABLED,
                new BooleanConfigValueParser(),
                ADD_WINDOW_ID_TO_ACTION_URL_ENABLED_DEFAULT);
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
