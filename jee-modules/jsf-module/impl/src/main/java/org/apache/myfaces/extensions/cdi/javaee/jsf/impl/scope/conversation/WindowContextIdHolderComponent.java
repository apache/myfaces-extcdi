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

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
public class WindowContextIdHolderComponent extends UIOutput
{
    private long windowContextId;

    public WindowContextIdHolderComponent()
    {
    }

    public WindowContextIdHolderComponent(long windowContextId)
    {
        this.windowContextId = windowContextId;
    }


    public Object saveState(FacesContext facesContext)
    {
        Object[] values = new Object[2];
        values[0] = super.saveState(facesContext);
        values[1] = this.windowContextId;
        return values;
    }

    public void restoreState(FacesContext facesContext, Object state)
    {
        if (state == null)
        {
            return;
        }

        Object[] values = (Object[]) state;
        super.restoreState(facesContext, values[0]);

        this.windowContextId = (Long) values[1];

        facesContext.getExternalContext().getRequestMap()
                .put(WindowContextManager.WINDOW_CONTEXT_ID_PARAMETER_KEY, this.windowContextId);
    }

    public long getWindowContextId()
    {
        return windowContextId;
    }

    void changeWindowContextId(long conversationContextId)
    {
        this.windowContextId = conversationContextId;
    }
}
