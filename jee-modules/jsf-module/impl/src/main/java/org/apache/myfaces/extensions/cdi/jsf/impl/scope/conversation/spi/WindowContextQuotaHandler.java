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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi;

import java.io.Serializable;

/**
 * Allows to create a custom handler for a custom window-quota
 */
public interface WindowContextQuotaHandler extends Serializable
{
    /**
     * Checks if the count of the currently active window-contexts is too high
     * @param activeWindowContextCount current window-context count
     * @return true if the count is too high and a cleanup has to be triggered, false otherwise
     */
    boolean isWindowContextQuotaViolated(int activeWindowContextCount);

    /**
     * Handles a quota violation if #checkQuota returned true and the cleanup couldn't remove an old context
     */
    void handleQuotaViolation();
}
