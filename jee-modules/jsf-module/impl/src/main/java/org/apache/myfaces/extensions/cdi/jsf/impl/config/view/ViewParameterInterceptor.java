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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewParameter;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigParameterStrategy;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */

@ViewParameter(key = "", value = "")
@Interceptor
public class ViewParameterInterceptor implements Serializable
{
    private static final long serialVersionUID = 8762625956958428994L;

    @Inject
    protected ViewConfigParameterStrategy viewConfigParameterStrategy;

    @AroundInvoke
    public Object addParameter(InvocationContext invocationContext) throws Exception
    {
        addViewParameter(invocationContext.getMethod().getAnnotation(ViewParameter.class));
        return this.viewConfigParameterStrategy.execute(invocationContext);
    }

    protected void addViewParameter(ViewParameter viewParameter)
    {
        this.viewConfigParameterStrategy.addParameter(viewParameter.key(), viewParameter.value());
    }
}
