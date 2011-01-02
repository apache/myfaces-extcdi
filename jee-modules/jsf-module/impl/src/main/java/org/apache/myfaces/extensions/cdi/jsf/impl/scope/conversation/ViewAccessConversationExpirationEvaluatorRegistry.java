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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Gerhard Petracek
 */
@WindowScoped
public class ViewAccessConversationExpirationEvaluatorRegistry implements Serializable
{
    private static final long serialVersionUID = -1783266839383634211L;

    protected ViewAccessConversationExpirationEvaluatorRegistry()
    {
    }

    private List<ViewAccessConversationExpirationEvaluator> viewAccessConversationExpirationEvaluatorList
            = new CopyOnWriteArrayList<ViewAccessConversationExpirationEvaluator>();

    void addViewAccessConversationExpirationEvaluator(ViewAccessConversationExpirationEvaluator evaluator)
    {
        this.viewAccessConversationExpirationEvaluatorList.add(evaluator);
    }

    void broadcastRenderedViewId(String viewId)
    {
        for(ViewAccessConversationExpirationEvaluator evaluator : this.viewAccessConversationExpirationEvaluatorList)
        {
            evaluator.observeRenderedView(viewId);

            if(evaluator.isExpired())
            {
                this.viewAccessConversationExpirationEvaluatorList.remove(evaluator);
            }
        }
    }
}
