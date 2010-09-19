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

import org.apache.myfaces.extensions.cdi.core.api.security.Secured;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.getOrCreateScopedInstanceOfBeanByClass;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.interceptor.Interceptor;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
@Secured(SecurityInterceptor.PlaceHolderVoter.class)
@Interceptor
public class SecurityInterceptor
{
    interface PlaceHolderVoter extends AccessDecisionVoter
    {}

    @AroundInvoke
    public Object filterDeniedInvocations(InvocationContext invocationContext) throws Exception
    {
        Secured secured = getSecuredAnnotation(invocationContext);

        Class<? extends AccessDecisionVoter>[] voterClasses = secured.value();

        AccessDecisionVoter voter;
        Set<SecurityViolation> violations;
        for(Class<? extends AccessDecisionVoter> voterClass : voterClasses)
        {
            voter = getOrCreateScopedInstanceOfBeanByClass(voterClass);

            violations = voter.checkPermission(invocationContext);
            if(violations.size() > 0)
            {
                throw new AccessDeniedException(violations, secured.errorPage());
            }
        }

        return invocationContext.proceed();
    }

    //TODO refactor it to a generic impl. and move it to an util class
    private Secured getSecuredAnnotation(InvocationContext invocationContext)
    {
        Secured secured;
        Method method = invocationContext.getMethod();

        if(method.isAnnotationPresent(Secured.class))
        {
            secured = method.getAnnotation(Secured.class);
        }
        else
        {
            secured = method.getDeclaringClass().getAnnotation(Secured.class);
        }
        return secured;
    }
}
