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
package org.apache.myfaces.extensions.cdi.jsf.impl.listener.request;

import javax.faces.context.FacesContextFactory;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.FacesException;

/**
 * @author Gerhard Petracek
 */
public class CodiFacesContextFactory extends FacesContextFactory
{
    protected final FacesContextFactory wrappedFacesContextFactory;

    public CodiFacesContextFactory(FacesContextFactory wrappedFacesContextFactory)
    {
        this.wrappedFacesContextFactory = wrappedFacesContextFactory;
    }

    public FacesContext getFacesContext(Object context,
                                        Object request,
                                        Object response,
                                        Lifecycle lifecycle) throws FacesException
    {
        //TODO wrap response if it's an instance of HttpServletResponse (to use #encodeURL)
        
        FacesContext facesContext =
                this.wrappedFacesContextFactory.getFacesContext(context, request, response, lifecycle);

        if (facesContext == null)
        {
            return null;
        }

        return new CodiFacesContextWrapper(facesContext);
    }

    public static FacesContext wrapFacesContext(FacesContext facesContext)
    {
        if(facesContext instanceof CodiFacesContextWrapper)
        {
            return facesContext;
        }
        return new CodiFacesContextWrapper(facesContext);
    }
}
