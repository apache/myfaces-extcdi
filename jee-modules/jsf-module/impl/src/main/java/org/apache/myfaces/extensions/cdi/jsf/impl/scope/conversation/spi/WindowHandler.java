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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi;

import javax.faces.context.ExternalContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * Allows to customize the basic window integration.
 * E.g. needed for adapters for component libs which already provide a window-id concept
 */
public interface WindowHandler extends Serializable
{
    /**
     * Allows to intercept the URL encoding
     * @param url URL which has to be encoded
     * @return the changed URL
     */
    String encodeURL(String url);

    /**
     * Allows to intercept redirects
     * @param externalContext current external-context
     * @param url current URL
     * @param addRequestParameter flag which indicates if the request-parameters should be added to the URL
     * @throws IOException exception which might be thrown by the external-context during the redirect
     */
    void sendRedirect(ExternalContext externalContext, String url, boolean addRequestParameter) throws IOException;

    /**
     * Creates a new and unique window-id for the current user-session
     * @return valid window-id
     */
    String createWindowId();

    /**
     * Allows to restore the window-id depending on the window-strategy supported by the implementation
     * @param externalContext current external-context
     * @return extracted window-id, null otherwise
     */
    String restoreWindowId(ExternalContext externalContext);
}
