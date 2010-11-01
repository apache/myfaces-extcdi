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
package org.apache.myfaces.extensions.cdi.jsf.api;

import org.apache.myfaces.extensions.cdi.core.api.config.CoreCodiConfigParameter;

/**
 * @author Gerhard Petracek
 */
public interface ConfigParameter
{
    /*
    static final String TRANSACTION_TOKEN_ENABLED =
            CoreCodiConfigParameter.BASE_NAME + "TRANSACTION_TOKEN_ENABLED";

    static final Boolean TRANSACTION_TOKEN_ENABLED_DEFAULT = Boolean.FALSE;
    */

    static final String URL_PARAMETER_ENABLED =
            CoreCodiConfigParameter.BASE_NAME + "URL_PARAMETER_ENABLED";

    static final boolean URL_PARAMETER_ENABLED_DEFAULT = true;

    static final String ALLOW_UNKNOWN_WINDOW_IDS =
            CoreCodiConfigParameter.BASE_NAME + "ALLOW_UNKNOWN_WINDOW_IDS";

    static final boolean ALLOW_UNKNOWN_WINDOW_IDS_DEFAULT = false;

    static final String ADD_WINDOW_ID_TO_ACTION_URL_ENABLED =
            CoreCodiConfigParameter.BASE_NAME + "ADD_WINDOW_ID_TO_ACTION_URL_ENABLED";

    static final boolean ADD_WINDOW_ID_TO_ACTION_URL_ENABLED_DEFAULT = false;

    static final String DISABLE_INITIAL_REDIRECT =
            CoreCodiConfigParameter.BASE_NAME + "DISABLE_INITIAL_REDIRECT";

    static final boolean DISABLE_INITIAL_REDIRECT_DEFAULT = false;

    static final String CONVERSATION_TIMEOUT =
            CoreCodiConfigParameter.BASE_NAME + "CONVERSATION_TIMEOUT";

    static final int CONVERSATION_TIMEOUT_DEFAULT = 30;

    static final String WINDOW_CONTEXT_TIMEOUT =
            CoreCodiConfigParameter.BASE_NAME + "WINDOW_CONTEXT_TIMEOUT";

    static final int WINDOW_CONTEXT_TIMEOUT_DEFAULT = 60;

    static final String MAX_WINDOW_CONTEXT_COUNT =
            CoreCodiConfigParameter.BASE_NAME + "MAX_WINDOW_CONTEXT_COUNT";

    static final int MAX_WINDOW_CONTEXT_COUNT_DEFAULT = 64;

    static final String ENABLE_SCOPE_BEAN_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_SCOPE_BEAN_EVENT";

    static final boolean ENABLE_SCOPE_BEAN_EVENT_DEFAULT = false;

    static final String ENABLE_ACCESS_BEAN_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_ACCESS_BEAN_EVENT";

    static final boolean ENABLE_ACCESS_BEAN_EVENT_DEFAULT = false;

    static final String ENABLE_UNSCOPE_BEAN_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_UNSCOPE_BEAN_EVENT";

    static final boolean ENABLE_UNSCOPE_BEAN_EVENT_DEFAULT = false;

    static final String ENABLE_START_CONVERSATION_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_START_CONVERSATION_EVENT";

    static final boolean ENABLE_START_CONVERSATION_EVENT_DEFAULT = false;

    static final String ENABLE_CLOSE_CONVERSATION_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_CLOSE_CONVERSATION_EVENT";

    static final boolean ENABLE_CLOSE_CONVERSATION_EVENT_DEFAULT = false;

    static final String ENABLE_RESTART_CONVERSATION_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_RESTART_CONVERSATION_EVENT";

    static final boolean ENABLE_RESTART_CONVERSATION_EVENT_DEFAULT = false;

    static final String ENABLE_CREATE_WINDOW_CONTEXT_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_CREATE_WINDOW_CONTEXT_EVENT";

    static final boolean ENABLE_CREATE_WINDOW_CONTEXT_EVENT_DEFAULT = false;

    static final String ENABLE_CLOSE_WINDOW_CONTEXT_EVENT =
            CoreCodiConfigParameter.BASE_NAME + "ENABLE_CLOSE_WINDOW_CONTEXT_EVENT";

    static final boolean ENABLE_CLOSE_WINDOW_CONTEXT_EVENT_DEFAULT = false;
}
