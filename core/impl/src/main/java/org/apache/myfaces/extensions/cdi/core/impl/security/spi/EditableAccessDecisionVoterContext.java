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
package org.apache.myfaces.extensions.cdi.core.impl.security.spi;

import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionState;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoterContext;
import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;

/**
 * Interface which allows to provide a custom {@link AccessDecisionVoterContext} implementation
 */
public interface EditableAccessDecisionVoterContext extends AccessDecisionVoterContext
{
    /**
     * Updates the state of the context
     * @param accessDecisionVoterState current state
     */
    void setState(AccessDecisionState accessDecisionVoterState);

    /**
     * Adds a new {@link SecurityViolation} to the context
     * @param securityViolation security-violation which should be added
     */
    void addViolation(SecurityViolation securityViolation);
}
