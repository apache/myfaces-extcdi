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
import javax.faces.context.FacesContext;

/**
 * Evaluates when a &#064;RestScoped Conversation context needs to get restarted.
 * Each Conversation gets it's own ExpirationEvaluator. We just use the RestParameters
 * to get the actual viewParams depending on the underlying technology.
 * This can be done evaluating JSF-2 &lt;f:viewParam&gt; or an
 * own implementation (e.g. for PrettyFaces).
 */
@Typed() // this is not a CDI bean
class RestConversationExpirationEvaluator implements ConversationExpirationEvaluator
{
    private static final long serialVersionUID = 4586717766107967148L;

    private AccessDecisionVoterContext accessDecisionVoterContext;

    private RestParameters restParameters;

    private String oldRestId;

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
        return checkRestId();
    }

    public boolean checkRestId()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null)
        {
            // this might happen if we are outside the JSF-Servlet, e.g. in a ServletFilter.
            return false;
        }


        if (restParameters.isPostback())
        {
            // we ignore POST requests
            return false;
        }

        String currentRestId = restParameters.getRestId();

        return currentRestId != null && !currentRestId.equals(oldRestId);
    }

    /**
     * {@inheritDoc}
     */
    public void touch()
    {
        if (oldRestId == null)
        {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext == null)
            {
                return;
            }
            oldRestId = restParameters.getRestId();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void expire()
    {
        oldRestId = null;
    }

}
