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

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author Gerhard Petracek
 */
class InterceptedRenderKit extends RenderKit
{
    private final RenderKit wrapped;

    //needed if the renderkit gets proxied - see EXTCDI-215
    protected InterceptedRenderKit()
    {
        this.wrapped = null;
    }

    InterceptedRenderKit(RenderKit wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    public void addRenderer(String s, String s1, Renderer renderer)
    {
        wrapped.addRenderer(s, s1, renderer);
    }

    /**
     * {@inheritDoc}
     */
    public Renderer getRenderer(String s, String s1)
    {
        return wrapped.getRenderer(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    public ResponseStateManager getResponseStateManager()
    {
        return wrapped.getResponseStateManager();
    }

    /**
     * Creates an intercepted {@link ResponseWriter} which adds a {@link WindowContextIdHolderComponent} to the
     * component tree.
     *
     * {@inheritDoc}
     */
    public ResponseWriter createResponseWriter(Writer writer, String s, String s1)
    {
        ResponseWriter responseWriter = wrapped.createResponseWriter(writer, s, s1);

        if (responseWriter == null)
        {
            return null;
        }

        return new InterceptedResponseWriter(responseWriter);
    }

    /**
     * {@inheritDoc}
     */
    public ResponseStream createResponseStream(OutputStream outputStream)
    {
        return wrapped.createResponseStream(outputStream);
    }
}
