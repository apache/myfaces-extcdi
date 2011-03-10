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
package org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation;

import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import java.io.Writer;

import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.addWindowContextIdHolderComponent;

/**
 * @author Gerhard Petracek
 */
class InterceptedRenderKit extends RenderKitWrapper
{
    private final RenderKit wrapped;

    InterceptedRenderKit(RenderKit wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * Adds a {@link org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.WindowContextIdHolderComponent}
     * to the component tree.
     *
     * {@inheritDoc}
     */
    public ResponseWriter createResponseWriter(Writer writer, String s, String s1)
    {
        addWindowContextIdHolderComponent();

        return this.wrapped.createResponseWriter(writer, s, s1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RenderKit getWrapped()
    {
        return wrapped;
    }
}
