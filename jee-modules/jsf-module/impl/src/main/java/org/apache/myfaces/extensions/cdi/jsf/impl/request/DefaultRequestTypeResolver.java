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
package org.apache.myfaces.extensions.cdi.jsf.impl.request;

import org.apache.myfaces.extensions.cdi.jsf.api.request.AbstractRequestTypeResolver;
import org.apache.myfaces.extensions.cdi.jsf.api.request.RequestTypeResolver;

import javax.enterprise.context.RequestScoped;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

/**
 * {@inheritDoc}
 */
@RequestScoped
public class DefaultRequestTypeResolver extends AbstractRequestTypeResolver
{
    private static final long serialVersionUID = -3702446693324781260L;

    /**
     * {@inheritDoc}
     */
    protected RequestTypeResolver createDefaultRequestTypeResolver()
    {
        return new RequestTypeResolver()
        {
            private static final long serialVersionUID = 6038323522900519990L;

            private boolean postRequest;

            {
                //TODO
                FacesContext facesContext = FacesContext.getCurrentInstance();

                RenderKit renderKit = facesContext.getRenderKit();
                if (renderKit == null)
                {
                    String renderKitId = facesContext.getApplication().getViewHandler()
                            .calculateRenderKitId(facesContext);

                    RenderKitFactory factory = (RenderKitFactory)
                            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
                    renderKit = factory.getRenderKit(facesContext, renderKitId);
                }
                this.postRequest = renderKit.getResponseStateManager().isPostback(facesContext);
            }

            /**
             * {@inheritDoc}
             */
            public boolean isPartialRequest()
            {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            public boolean isPostRequest()
            {
                return this.postRequest;
            }
        };
    }
}
