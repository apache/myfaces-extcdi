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
package org.apache.myfaces.extensions.cdi.core.api;

/**
 * Core-impl has to provide beans for the names specified below.
 *
 * @author Gerhard Petracek
 */
public interface CoreModuleBeanNames extends BeanNames
{
    /**
     * Typesafe version of CURRENT_WINDOW_BEAN_NAME
     * (bean type: {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext})
     */
    String CURRENT_WINDOW_CONTEXT_BEAN_NAME = "currentWindowContext";

    /**
     * Useful for EL-Expressions e.g. to call #useNewId or a property of the current window
     */
    String CURRENT_WINDOW_BEAN_NAME = "currentWindow";

}
