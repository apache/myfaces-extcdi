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
package org.apache.myfaces.extensions.cdi.jsf2.impl.security;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class SecurityAwareViewHandler
        extends org.apache.myfaces.extensions.cdi.jsf.impl.security.SecurityAwareViewHandler
{
    /**
     * {@inheritDoc}
     */
    public SecurityAwareViewHandler(ViewHandler wrapped)
    {
        super(wrapped);
    }

    /**
     * {@inheritDoc}
     *
     * see EXTCDI-167
     */
    @Override
    public UIViewRoot createView(FacesContext context, String viewId)
    {
        UIViewRoot originalViewRoot = context.getViewRoot();
        UIViewRoot newViewRoot;

        Map<String, Object> viewMap = null;
        if(originalViewRoot != null)
        {
            viewMap = originalViewRoot.getViewMap(false);

            if(viewMap instanceof Serializable)
            {
                //noinspection unchecked
                viewMap = StateCloner.clone((Serializable)viewMap, Map.class);
            }
        }
        newViewRoot = super.createView(context, viewId);

        if(viewMap instanceof Serializable)
        {
            originalViewRoot.getViewMap(true).putAll(viewMap);
        }
        return newViewRoot;
    }
}
