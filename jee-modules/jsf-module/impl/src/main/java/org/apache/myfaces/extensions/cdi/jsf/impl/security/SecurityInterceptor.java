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
package org.apache.myfaces.extensions.cdi.jsf.impl.security;

import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.Secured;
import org.apache.myfaces.extensions.cdi.jsf.impl.security.spi.SecurityStrategy;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@Secured(SecurityInterceptor.PlaceHolderVoter.class)
@Interceptor
public class SecurityInterceptor implements Serializable
{
    private static final long serialVersionUID = -7094673146532371976L;

    interface PlaceHolderVoter extends AccessDecisionVoter {}

    @Inject
    private SecurityStrategy securityStrategy;

    /**
     * Triggers the registered {@link AccessDecisionVoter}s
     * @param invocationContext current invocation-context
     * @return result of the intercepted method
     * @throws Exception exception which might be thrown by the intercepted method
     */
    @AroundInvoke
    public Object filterDeniedInvocations(InvocationContext invocationContext) throws Exception
    {
        return this.securityStrategy.execute(invocationContext);
    }
}
