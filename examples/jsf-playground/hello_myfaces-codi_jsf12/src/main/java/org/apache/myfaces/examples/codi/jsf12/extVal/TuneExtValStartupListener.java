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
package org.apache.myfaces.examples.codi.jsf12.extVal;

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.renderkit.ExtValRendererProxy;
import org.apache.myfaces.extensions.validator.core.interceptor.ValidationInterceptor;
import org.apache.myfaces.extensions.validator.beanval.validation.ModelValidationPhaseListener;
import org.apache.myfaces.extensions.validator.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseListener;

/**
 * Performance tuning based on http://wiki.apache.org/myfaces/Extensions/Validator/JSR303/Advanced
 */
@JsfPhaseListener
public class TuneExtValStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = -6264765928200362764L;

    protected void init()
    {
        ExtValContext.getContext().denyRendererInterceptor(ValidationInterceptor.class);
        JsfUtils.deregisterPhaseListener(new ModelValidationPhaseListener());
        ExtValContext.getContext().addGlobalProperty(ExtValRendererProxy.KEY, null);
    }
}
