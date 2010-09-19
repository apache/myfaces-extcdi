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
package org.apache.myfaces.extensions.cdi.core.impl.utils;

import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.getOrCreateScopedInstanceOfBeanByClass;

import javax.interceptor.InvocationContext;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Gerhard Petracek
 */
public class SecurityUtils
{
    public static void invokeVoters(InvocationContext invocationContext,
                                    Class<? extends AccessDecisionVoter>[] accessDecisionVoters,
                                    Class<? extends ViewConfig> errorView)
    {
        if(accessDecisionVoters == null)
        {
            return;
        }

        Set<SecurityViolation> violations = new HashSet<SecurityViolation>();

        AccessDecisionVoter voter;
        for(Class<? extends AccessDecisionVoter> voterClass : accessDecisionVoters)
        {
            voter = getOrCreateScopedInstanceOfBeanByClass(voterClass);

            voter.checkPermission(invocationContext, violations);
            if(violations.size() > 0)
            {
                throw new AccessDeniedException(violations, errorView);
            }
        }
    }
}
