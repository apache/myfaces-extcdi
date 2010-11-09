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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
class PhasesLifecycleCallbackEntry
{
    private final List<Method> beforePhaseCallbacks;

    private final List<Method> afterPhaseCallbacks;

    PhasesLifecycleCallbackEntry(List<Method> beforePhaseCallbacks, List<Method> afterPhaseCallbacks)
    {
        if(beforePhaseCallbacks != null)
        {
            this.beforePhaseCallbacks = Collections.unmodifiableList(beforePhaseCallbacks);
        }
        else
        {
            this.beforePhaseCallbacks = Collections.emptyList();
        }

        if(afterPhaseCallbacks != null)
        {
            this.afterPhaseCallbacks = Collections.unmodifiableList(afterPhaseCallbacks);
        }
        else
        {
            this.afterPhaseCallbacks = Collections.emptyList();
        }
    }

    List<Method> getBeforePhaseCallbacks()
    {
        return beforePhaseCallbacks;
    }

    List<Method> getAfterPhaseCallbacks()
    {
        return afterPhaseCallbacks;
    }
}
