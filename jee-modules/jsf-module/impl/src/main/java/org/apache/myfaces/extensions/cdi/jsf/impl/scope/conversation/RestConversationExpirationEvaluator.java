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

import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionState;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoterContext;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Evaluates when a &#064;RestScoped Conversation context needs to get restarted.
 */
@Typed() // this is not a CDI bean
class RestConversationExpirationEvaluator implements ConversationExpirationEvaluator
{
    private static final long serialVersionUID = 4586717766107967148L;

    private AccessDecisionVoterContext accessDecisionVoterContext;

    private RestParameters restParameters;

    RestConversationExpirationEvaluator(BeanManager beanManager, AccessDecisionVoterContext accessDecisionVoterContext)
    {
        this.accessDecisionVoterContext = accessDecisionVoterContext;
        this.restParameters = CodiUtils.getContextualReferenceByClass(beanManager, RestParameters.class);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpired()
    {
        // check for bean access via ViewConfig
        if(this.accessDecisionVoterContext != null &&
                AccessDecisionState.VOTE_IN_PROGRESS.equals(this.accessDecisionVoterContext.getState()))
        {
            return false;
        }

        // if the view params changed, then our Conversation must expire.
        return restParameters.checkForNewViewParameters();
    }

    /**
     * {@inheritDoc}
     */
    public void touch()
    {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void expire()
    {
        restParameters.reset();
    }

}
