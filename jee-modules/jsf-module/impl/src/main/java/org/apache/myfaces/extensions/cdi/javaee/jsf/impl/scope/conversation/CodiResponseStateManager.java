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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 */
class CodiResponseStateManager extends ResponseStateManager
{
    private ResponseStateManager wrapped;

    CodiResponseStateManager(ResponseStateManager wrapped)
    {
        this.wrapped = wrapped;
    }

    public void writeState(FacesContext facesContext, Object o)
            throws IOException
    {
        wrapped.writeState(facesContext, o);
    }

    public void writeState(FacesContext facesContext, StateManager.SerializedView serializedView)
            throws IOException
    {
        wrapped.writeState(facesContext, serializedView);
    }

    public Object getState(FacesContext facesContext, String s)
    {
        return wrapped.getState(facesContext, s);
    }

    public Object getTreeStructureToRestore(FacesContext facesContext, String s)
    {
        return wrapped.getTreeStructureToRestore(facesContext, s);
    }

    public Object getComponentStateToRestore(FacesContext facesContext)
    {
        return wrapped.getComponentStateToRestore(facesContext);
    }

    public boolean isPostback(FacesContext facesContext)
    {
        return wrapped.isPostback(facesContext);
    }
}
