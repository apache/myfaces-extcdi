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
package org.apache.myfaces.extensions.cdi.javaee.jsf.api;

import org.apache.myfaces.extensions.cdi.core.api.config.CoreCodiConfigParameter;

/**
 * @author Gerhard Petracek
 */
public interface ConfigParameter
{
    static final String TRANSACTION_TOKEN_ENABLED =
            CoreCodiConfigParameter.BASE_NAME + "TRANSACTION_TOKEN_ENABLED";

    static final Boolean TRANSACTION_TOKEN_ENABLED_DEFAULT = Boolean.FALSE;

    static final String URL_PARAMETER_ENABLED =
            CoreCodiConfigParameter.BASE_NAME + "URL_PARAMETER_ENABLED";

    static final boolean URL_PARAMETER_ENABLED_DEFAULT = true;

    static final String GROUPED_CONVERSATION_TIMEOUT =
            CoreCodiConfigParameter.BASE_NAME + "GROUPED_CONVERSATION_TIMEOUT";

    static final int GROUPED_CONVERSATION_TIMEOUT_DEFAULT = 30;

    static final String WINDOW_CONTEXT_TIMEOUT =
            CoreCodiConfigParameter.BASE_NAME + "WINDOW_CONTEXT_TIMEOUT";

    static final int WINDOW_CONTEXT_TIMEOUT_DEFAULT = 60;

    static final String MAX_WINDOW_CONTEXT_COUNT =
            CoreCodiConfigParameter.BASE_NAME + "MAX_WINDOW_CONTEXT_COUNT";

    static final int MAX_WINDOW_CONTEXT_COUNT_DEFAULT = 64;
}
