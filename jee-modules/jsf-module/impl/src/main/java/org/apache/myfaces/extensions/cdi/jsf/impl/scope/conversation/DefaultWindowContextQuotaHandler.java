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

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowContextQuotaHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfModuleConfig;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.tooManyOpenWindowException;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultWindowContextQuotaHandler implements WindowContextQuotaHandler
{
    private static final long serialVersionUID = 4354405166761604711L;

    private int maxWindowContextCount;

    public DefaultWindowContextQuotaHandler()
    {
    }

    @Inject
    public DefaultWindowContextQuotaHandler(JsfModuleConfig config)
    {
        this.maxWindowContextCount = config.getMaxWindowContextCount();
    }

    public boolean checkQuota(int activeWindowContextCount)
    {
        return this.maxWindowContextCount < activeWindowContextCount;
    }

    public void handleQuotaViolation()
    {
        if(!cleanupWindowContext())
        {
            throw tooManyOpenWindowException(this.maxWindowContextCount);
        }
    }

    private boolean cleanupWindowContext()
    {
        WindowContextManager windowContextManager = ConversationUtils.getWindowContextManager();

        if(windowContextManager instanceof EditableWindowContextManager)
        {
            EditableWindowContextManager editableWindowContextManager =
                    (EditableWindowContextManager)windowContextManager;

            Collection<EditableWindowContext> activeWindowContexts = editableWindowContextManager.getWindowContexts();

            int activeWindowContextCountBeforeCleanup = activeWindowContexts.size();

            removeEldestWindowContext(editableWindowContextManager, activeWindowContexts);

            if(activeWindowContextCountBeforeCleanup > editableWindowContextManager.getWindowContexts().size())
            {
                return true;
            }
        }
        return false;
    }

    private void removeEldestWindowContext(EditableWindowContextManager editableWindowContextManager,
                                           Collection<EditableWindowContext> activeWindowContexts)
    {
        EditableWindowContext windowContextToRemove = findEldestWindowContext(activeWindowContexts);

        if(windowContextToRemove != null)
        {
            editableWindowContextManager.closeWindowContext(windowContextToRemove);
        }
    }

    private EditableWindowContext findEldestWindowContext(Collection<EditableWindowContext> activeWindowContexts)
    {
        Date lastAccess = new Date();
        EditableWindowContext result = null;
        for(EditableWindowContext windowContext : activeWindowContexts)
        {
            if(lastAccess.after(windowContext.getLastAccess()))
            {
                lastAccess = windowContext.getLastAccess();
                result = windowContext;
            }
        }
        return result;
    }
}
