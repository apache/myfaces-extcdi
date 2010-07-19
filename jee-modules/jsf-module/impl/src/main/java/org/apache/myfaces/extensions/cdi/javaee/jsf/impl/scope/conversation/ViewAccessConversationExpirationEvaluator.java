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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.getOldViewIdFromRequest;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.getNewViewIdFromRequest;

import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
class ViewAccessConversationExpirationEvaluator implements ConversationExpirationEvaluator
{
    private String lastViewId; //for access scope

    public boolean isExpired()
    {
        if(this.lastViewId == null)
        {
            return true;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        String fromViewId = getOldViewIdFromRequest(facesContext);

        if (fromViewId != null && fromViewId.endsWith(this.lastViewId))
        {
            this.lastViewId = getNewViewIdFromRequest(facesContext);
        }
        return !this.lastViewId.equals(getCurrentViewId());
    }

    public void touch()
    {
        this.lastViewId = getCurrentViewId();
    }

    public void expire()
    {
        this.lastViewId = null;
    }

    private String getCurrentViewId()
    {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }
}