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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.ClientConfig;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

/**
 * Contains information about whether the user has
 * JavaScript enabled on his client, etc.
 * It also contains the windowhandler html which gets sent to
 * the browser to detect the current windowId.
 *
 * This allows the 'customisation' of this html file to e.g.
 * adopt the background colour to avoid screen flickering.
 */
@SessionScoped
public class DefaultClientConfig implements ClientConfig
{
    private static final long serialVersionUID = -3264016646002116064L;

    private boolean javaScriptEnabled = true;

    protected String windowHandlerHtml;

    @Inject
    private ProjectStage projectStage;


    public boolean isJavaScriptEnabled()
    {
        return this.javaScriptEnabled;
    }

    public void setJavaScriptEnabled(boolean javaScriptEnabled)
    {
        this.javaScriptEnabled = javaScriptEnabled;
    }


    public String getWindowHandlerResourceLocation()
    {
        return DEFAULT_WINDOW_HANDLER_HTML_FILE;
    }

    public String getWindowHandlerHtml()
            throws IOException
    {
        if (projectStage != ProjectStage.Development && windowHandlerHtml != null)
        {
            // use cached windowHandlerHtml except in Development
            return windowHandlerHtml;
        }

        InputStream is = ClassUtils.getClassLoader(null).getResourceAsStream(getWindowHandlerResourceLocation());
        StringBuffer sb = new StringBuffer();
        try
        {
            byte[] buf = new byte[32 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1)
            {
                String sbuf = new String(buf);
                sb.append(sbuf);
            }
        }
        finally
        {
            is.close();
        }

        windowHandlerHtml = sb.toString();
        return windowHandlerHtml;
    }

}
