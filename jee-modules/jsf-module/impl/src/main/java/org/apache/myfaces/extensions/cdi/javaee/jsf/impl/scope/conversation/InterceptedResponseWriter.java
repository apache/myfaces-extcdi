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

import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils
        .addWindowContextIdHolderComponent;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils
        .storeCurrentViewIdAsOldViewId;

import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Adds
 * {@link WindowContextIdHolderComponent}
 * to the tree before the call of {@link #startDocument}
 *
 * @author Gerhard Petracek
 */
class InterceptedResponseWriter extends ResponseWriterWrapper
{
    private ResponseWriter wrapped;

    InterceptedResponseWriter(ResponseWriter wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public void startDocument()
            throws IOException
    {
        addWindowContextIdHolderComponent();

        wrapped.startDocument();
    }

    @Override
    public void endDocument()
            throws IOException
    {
        storeCurrentViewIdAsOldViewId(FacesContext.getCurrentInstance());

        wrapped.endDocument();
    }

    protected ResponseWriter getWrapped()
    {
        return this.wrapped;
    }
}
