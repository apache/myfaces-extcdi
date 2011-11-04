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
package org.apache.myfaces.extensions.cdi.core.api.security.event;

import org.apache.myfaces.extensions.cdi.core.api.security.SecurityViolation;

/**
 * Event which gets fired if it isn't allowed to create a bean
 */
public class InvalidBeanCreationEvent
{
    private final SecurityViolation securityViolation;

    private boolean throwSecurityViolation = true;

    /**
     * Constructor which is required to create the event for the given {@link SecurityViolation}
     * @param securityViolation current security-violation
     */
    public InvalidBeanCreationEvent(SecurityViolation securityViolation)
    {
        this.securityViolation = securityViolation;
    }

    /**
     * Exposes the current {@link SecurityViolation}
     * @return the current security-violation
     */
    public SecurityViolation getSecurityViolation()
    {
        return securityViolation;
    }

    /**
     * Allows e.g. to filter the current violation
     * @param throwSecurityViolation new value for the flag
     */
    public void setThrowSecurityViolation(boolean throwSecurityViolation)
    {
        this.throwSecurityViolation = throwSecurityViolation;
    }

    /**
     * Exposes if the {@link SecurityViolation} wrapped by the current event should be thrown as exception
     * @return true if the violation should be thrown, false otherwise
     */
    public boolean isThrowSecurityViolation()
    {
        return throwSecurityViolation;
    }
}
