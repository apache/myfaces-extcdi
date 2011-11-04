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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoterContext;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionState;

import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.getOldViewId;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.getNewViewId;

import javax.faces.context.FacesContext;

/**
 * {@link ConversationExpirationEvaluator} for
 * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped}
 */
class ViewAccessConversationExpirationEvaluator implements ConversationExpirationEvaluator, ConversationAware
{
    private static final long serialVersionUID = 5586717766107967144L;

    private String lastViewId; //for access scope

    private Conversation conversation;

    private AccessDecisionVoterContext accessDecisionVoterContext;

    ViewAccessConversationExpirationEvaluator(AccessDecisionVoterContext accessDecisionVoterContext)
    {
        this.accessDecisionVoterContext = accessDecisionVoterContext;
    }

    //see EXTCDI-49
    void observeRenderedView(String viewId)
    {
        if(viewId != null /*in case of an invalid view*/ && !viewId.equals(this.lastViewId))
        {
            if(this.conversation != null)
            {
                this.conversation.close();
            }
            expire();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpired()
    {
        //see EXTCDI-154
        if(this.accessDecisionVoterContext != null &&
                AccessDecisionState.VOTE_IN_PROGRESS.equals(this.accessDecisionVoterContext.getState()))
        {
            return false;
        }

        if(this.lastViewId == null)
        {
            return true;
        }

        String oldViewId = getOldViewId();

        if (oldViewId != null && oldViewId.equals(this.lastViewId))
        {
            this.lastViewId = getNewViewId();
        }

        String currentViewId = getCurrentViewId();

        if(currentViewId == null) //in case of an invalid view
        {
            return false;
        }
        boolean result = !currentViewId.equals(this.lastViewId);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void touch()
    {
        this.lastViewId = getCurrentViewId();
    }

    /**
     * {@inheritDoc}
     */
    public void expire()
    {
        this.lastViewId = null;
        this.conversation = null;
    }

    private String getCurrentViewId()
    {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    /**
     * {@inheritDoc}
     */
    public void setConversation(Conversation conversation)
    {
        this.conversation = conversation;
    }
}