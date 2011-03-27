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
package org.apache.myfaces.extensions.cdi.core.api.security;

import javax.interceptor.InvocationContext;
import java.util.Set;
import java.util.HashSet;

/**
 * Base implementation which provides helper methods.
 *
 * @author Gerhard Petracek
 */
public abstract class AbstractAccessDecisionVoter implements AccessDecisionVoter
{
    private static final long serialVersionUID = -9145021044568668681L;

    /**
     * It should be final - but proxy-libs won't support it.
     *
     * {@inheritDoc}
     */
    public Set<SecurityViolation> checkPermission(InvocationContext invocationContext)
    {
        Set<SecurityViolation> result = new HashSet<SecurityViolation>();

        checkPermission(invocationContext, result);

        return result;
    }

    /**
     * Allows an easier implementation in combination with {@link #newSecurityViolation(String)}.
     *
     * @param invocationContext current invocationContext
     * @param violations set for adding violations
     */
    protected abstract void checkPermission(InvocationContext invocationContext, Set<SecurityViolation> violations);

    /**
     * Creates an instance of {@link org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation} for a given
     * string which will be used as reason to describe the violation.
     * 
     * @param reason description of the violation
     * @return A new instance of {@link org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation}
     * which provides details about the found restriction.
     */
    protected SecurityViolation newSecurityViolation(final String reason)
    {
        return new SecurityViolation()
        {
            /**
             * {@inheritDoc}
             */
            public String getReason()
            {
                return reason;
            }
        };
    }
}