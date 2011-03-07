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
package org.apache.myfaces.extensions.cdi.core.api.security;

import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;

import java.util.Set;

/**
 * Exception occurs in case of a security-violation.
 * It's aware of the reason for the violation as well as the error-view which should be used to display the restriction.
 * 
 * @author Gerhard Petracek
 */
public class AccessDeniedException extends SecurityException
{
    private static final long serialVersionUID = -4066763895951237969L;

    private Set<SecurityViolation> violations;
    private Class<? extends ViewConfig> errorView;

    public AccessDeniedException(Set<SecurityViolation> violations, Class<? extends ViewConfig> errorView)
    {
        this.violations = violations;
        this.errorView = errorView;
    }

    /**
     * All {@link SecurityViolation} which were found by a {@link AccessDecisionVoter}
     *
     * @return all security-violations
     */
    public Set<SecurityViolation> getViolations()
    {
        return violations;
    }

    /**
     * Optional page which should be used as error-page
     * @return type-safe view-config which is mapped to an error-view.
     * Returning null would force the navigation to the default error-view.
     */
    public Class<? extends ViewConfig> getErrorView()
    {
        return this.errorView != null ? this.errorView : DefaultErrorView.class;
    }
}
