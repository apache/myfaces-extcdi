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
package org.apache.myfaces.extensions.cdi.jsf.api.config;

import java.io.IOException;
import java.io.Serializable;

/**
 * Contains information about whether the user has
 * JavaScript enabled on his client, etc.
 * It also contains the windowhandler html which gets sent to
 * the browser to detect the current windowId.
 *
 * This allows the 'customisation' of this html file to e.g.
 * adopt the background colour to avoid screen flickering.
 */
public interface ClientConfig extends Serializable
{
    /**
     * The location of the default windowhandler resource
     */
    String DEFAULT_WINDOW_HANDLER_HTML_FILE = "static/windowhandler.html";

    /**
     * Defaults to <code>true</code>.
     * @return if the user has JavaScript enabled
     */
    boolean isJavaScriptEnabled();

    /**
     * Set it to <code>false</code> if you don't like to use the
     * JavaScript based client side windowhandler. In this case
     * the request will be returned directly.
     * @param javaScriptEnabled
     */
    void setJavaScriptEnabled(boolean javaScriptEnabled);

    /**
     * For branding the windowhandler page - e.g. change the backgroundcolour
     * or the language of the message text - you can just copy the content of the
     * {@link #DEFAULT_WINDOW_HANDLER_HTML_FILE} and adopt it to your needs.
     * @return the location of the <i>windowhandler.html</i> resource
     *         which should be sent to the users browser.
     */
    String getWindowHandlerResourceLocation();

    /**
     * This might return different windowhandlers based on user settings like
     * his language, an affiliation, etc
     * @return a String containing the whole windowhandler.html file.
     * @throws IOException
     */
    String getWindowHandlerHtml() throws IOException;
}
