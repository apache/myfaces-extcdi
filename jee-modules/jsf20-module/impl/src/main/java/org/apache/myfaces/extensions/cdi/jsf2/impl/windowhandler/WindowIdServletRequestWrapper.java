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
package org.apache.myfaces.extensions.cdi.jsf2.impl.windowhandler;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakob Korherr
 * @Deprecated use {@link WindowHandlerPhaseListener}
 */
public class WindowIdServletRequestWrapper extends HttpServletRequestWrapper
{

    private String windowId;
    private Map parameterMap;

    public WindowIdServletRequestWrapper(HttpServletRequest httpServletRequest, String windowId)
    {
        super(httpServletRequest);

        this.windowId = windowId;

        // create new parameter map including windowId
        parameterMap = new HashMap();
        parameterMap.putAll(httpServletRequest.getParameterMap());
        parameterMap.put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, windowId);
    }

    @Override
    public String getParameter(String name)
    {
        if (WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY.equals(name))
        {
            return windowId;
        }

        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name)
    {
        if (WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY.equals(name))
        {
            return new String[] { windowId };
        }

        return super.getParameterValues(name);
    }

    @Override
    public Map getParameterMap()
    {
        return parameterMap;
    }

    @Override
    public Enumeration getParameterNames()
    {
        return Collections.enumeration(parameterMap.entrySet());
    }

}
