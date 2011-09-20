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
package org.apache.myfaces.extensions.cdi.trinidad2.impl;

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.InterceptedResponseWriter;
import org.apache.myfaces.trinidad.render.DialogRenderKitService;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

import javax.enterprise.inject.Typed;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

@Typed()
class TrinidadRenderKitWrapper extends RenderKit
        implements Service.Provider, DialogRenderKitService, ExtendedRenderKitService
{
    private RenderKit wrapped;

    /**
     * Constructor for wrapping the given {@link RenderKit}
     *
     * @param wrapped render-kit which should be wrapped
     */
    public TrinidadRenderKitWrapper(RenderKit wrapped)
    {
        this.wrapped = wrapped;
    }

    /**
     * Wraps the {@link ResponseWriter} with a special wrapper which adds
     * {@link org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.WindowContextIdHolderComponent}
     * at the beginning.
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
    public <T> T getService(Class<T> tClass)
    {
        if (this.wrapped instanceof Service.Provider)
        {
            return ((Service.Provider) this.wrapped).getService(tClass);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean launchDialog(FacesContext facesContext,
                                UIViewRoot uiViewRoot,
                                UIComponent uiComponent,
                                Map<String, Object> processParameters,
                                boolean useWindow,
                                Map<String, Object> windowProperties)
    {
        if (this.wrapped instanceof DialogRenderKitService)
        {
            return ((DialogRenderKitService) this.wrapped).launchDialog(facesContext,
                    uiViewRoot,
                    uiComponent,
                    processParameters,
                    useWindow,
                    windowProperties);
        }

        //TODO logging
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean returnFromDialog(FacesContext facesContext, Object returnValue)
    {
        if (this.wrapped instanceof DialogRenderKitService)
        {
            return ((DialogRenderKitService) this.wrapped).returnFromDialog(facesContext, returnValue);
        }

        //TODO logging
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReturning(FacesContext facesContext, UIComponent source)
    {
        if (this.wrapped instanceof DialogRenderKitService)
        {
            return ((DialogRenderKitService) this.wrapped).returnFromDialog(facesContext, source);
        }

        //TODO logging
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void addScript(FacesContext facesContext, String s)
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).addScript(facesContext, s);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void encodeScripts(FacesContext facesContext) throws IOException
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).encodeScripts(facesContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean shortCircuitRenderView(FacesContext facesContext) throws IOException
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).shortCircuitRenderView(facesContext);
        }

        //TODO logging
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isStateless(FacesContext facesContext)
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).isStateless(facesContext);
        }

        //TODO logging
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void encodeBegin(FacesContext facesContext) throws IOException
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).encodeBegin(facesContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void encodeEnd(FacesContext facesContext) throws IOException
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).encodeEnd(facesContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void encodeFinally(FacesContext facesContext)
    {
        if (this.wrapped instanceof ExtendedRenderKitService)
        {
            ((ExtendedRenderKitService) this.wrapped).encodeFinally(facesContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addRenderer(String s, String s1, Renderer renderer)
    {
        this.wrapped.addRenderer(s, s1, renderer);
    }

    /**
     * {@inheritDoc}
     */
    public Renderer getRenderer(String s, String s1)
    {
        return this.wrapped.getRenderer(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    public ResponseStateManager getResponseStateManager()
    {
        return this.wrapped.getResponseStateManager();
    }

    /**
     * {@inheritDoc}
     */
    public ResponseStream createResponseStream(OutputStream outputStream)
    {
        return this.wrapped.createResponseStream(outputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addClientBehaviorRenderer(String type, ClientBehaviorRenderer renderer)
    {
        wrapped.addClientBehaviorRenderer(type, renderer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientBehaviorRenderer getClientBehaviorRenderer(String type)
    {
        return wrapped.getClientBehaviorRenderer(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> getClientBehaviorRendererTypes()
    {
        return wrapped.getClientBehaviorRendererTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> getComponentFamilies()
    {
        return wrapped.getComponentFamilies();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> getRendererTypes(String componentFamily)
    {
        return wrapped.getRendererTypes(componentFamily);
    }
}