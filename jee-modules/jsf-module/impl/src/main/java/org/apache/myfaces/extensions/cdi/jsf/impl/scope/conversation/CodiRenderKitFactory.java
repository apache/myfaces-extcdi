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

import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.RenderKitWrapperFactory;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.util.Iterator;

/**
 * !!!keep in sync with org.apache.myfaces.extensions.cdi.jsf2.impl.scope.conversation.CodiRenderKitFactory!!!
 *
 * @author Gerhard Petracek
 */
public class CodiRenderKitFactory extends RenderKitFactory implements Deactivatable
{
    public static final String RENDER_KIT_WRAPPER_FACTORY_PROPERTY_NAME =
            "org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.RenderKitWrapperFactory";
    public static final String RENDER_KIT_WRAPPER_FACTORY_JNDI_NAME =
            "java:comp/env/myfaces-codi/RenderKitWrapperFactory";

    private RenderKitFactory wrapped;
    private RenderKitWrapperFactory renderKitWrapperFactory;

    private Boolean initialized;
    private final boolean deactivated;

    public CodiRenderKitFactory(RenderKitFactory wrapped)
    {
        this.wrapped = wrapped;
        this.deactivated = !isActivated();

        if(!this.deactivated)
        {
            this.renderKitWrapperFactory = CodiUtils.lookupFromEnvironment(RENDER_KIT_WRAPPER_FACTORY_PROPERTY_NAME,
                                                                           RENDER_KIT_WRAPPER_FACTORY_JNDI_NAME,
                                                                           RenderKitWrapperFactory.class);
        }
    }

    public void addRenderKit(String s, RenderKit renderKit)
    {
        wrapped.addRenderKit(s, renderKit);
    }

    public RenderKit getRenderKit(FacesContext facesContext, String s)
    {
        RenderKit renderKit = wrapped.getRenderKit(facesContext, s);

        if (renderKit == null)
        {
            return null;
        }

        if(this.deactivated)
        {
            return renderKit;
        }

        //workaround for mojarra

        if(this.initialized == null)
        {
            lazyInit();
        }

        if(this.renderKitWrapperFactory != null)
        {
            return this.renderKitWrapperFactory.create(renderKit);
        }

        return new InterceptedRenderKit(renderKit);
    }

    private void lazyInit()
    {
        if(this.renderKitWrapperFactory == null && CodiUtils.isCdiInitialized())
        {
            this.renderKitWrapperFactory = CodiUtils
                    .getOrCreateScopedInstanceOfBeanByClass(RenderKitWrapperFactory.class, true);
        }

        if(CodiUtils.isCdiInitialized())
        {
            this.initialized = true;
        }
    }

    public Iterator<String> getRenderKitIds()
    {
        return wrapped.getRenderKitIds();
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
