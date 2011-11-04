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
package org.apache.myfaces.extensions.cdi.core.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoterContext;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionState;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDeniedException;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;
import org.apache.myfaces.extensions.cdi.core.impl.security.spi.EditableAccessDecisionVoterContext;

import javax.interceptor.InvocationContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.Typed;
import java.util.List;
import java.util.Set;

/**
 * Helper for using {@link AccessDecisionVoter}
 */
@Typed()
public abstract class SecurityUtils
{
    /**
     * Constructor which prevents the instantiation of this class
     */
    private SecurityUtils()
    {
        // prevent instantiation
    }

    /**
     * Helper for invoking the given {@link AccessDecisionVoter}s
     * @param invocationContext current invocation-context (might be null in case of secured views)
     * @param beanManager current bean-manager
     * @param accessDecisionVoters current access-decision-voters
     * @param errorView optional inline error view
     */
    public static void invokeVoters(InvocationContext invocationContext,
                                    BeanManager beanManager,
                                    List<Class<? extends AccessDecisionVoter>> accessDecisionVoters,
                                    Class<? extends ViewConfig> errorView)
    {
        if(accessDecisionVoters == null)
        {
            return;
        }

        AccessDecisionVoterContext voterContext =
                CodiUtils.getContextualReferenceByClass(beanManager, AccessDecisionVoterContext.class, true);

        AccessDecisionState voterState = AccessDecisionState.VOTE_IN_PROGRESS;
        try
        {
            if(voterContext instanceof EditableAccessDecisionVoterContext)
            {
                ((EditableAccessDecisionVoterContext)voterContext).setState(voterState);
            }

            Set<SecurityViolation> violations;

            AccessDecisionVoter voter;
            for(Class<? extends AccessDecisionVoter> voterClass : accessDecisionVoters)
            {
                voter = CodiUtils.getContextualReferenceByClass(beanManager, voterClass);

                violations = voter.checkPermission(invocationContext);

                if(violations != null && violations.size() > 0)
                {
                    if(voterContext instanceof EditableAccessDecisionVoterContext)
                    {
                        voterState = AccessDecisionState.VIOLATION_FOUND;
                        for(SecurityViolation securityViolation : violations)
                        {
                            ((EditableAccessDecisionVoterContext) voterContext).addViolation(securityViolation);
                        }
                    }
                    throw new AccessDeniedException(violations, errorView);
                }
            }
        }
        finally
        {
            if(voterContext instanceof EditableAccessDecisionVoterContext)
            {
                if(AccessDecisionState.VOTE_IN_PROGRESS.equals(voterState))
                {
                    voterState = AccessDecisionState.NO_VIOLATION_FOUND;
                }

                ((EditableAccessDecisionVoterContext)voterContext).setState(voterState);
            }
        }
    }
}
