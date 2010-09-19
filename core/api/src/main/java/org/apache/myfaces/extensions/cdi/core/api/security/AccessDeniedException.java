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

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;

import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class AccessDeniedException extends SecurityException
{
    private static final long serialVersionUID = -4066763895951237969L;

    private Set<SecurityViolation> violations;
    private Class<? extends ViewConfig> errorPage;

    public AccessDeniedException(Set<SecurityViolation> violations, Class<? extends ViewConfig> errorPage)
    {
        this.violations = violations;
        this.errorPage = errorPage;
    }

    public Set<SecurityViolation> getViolations()
    {
        return violations;
    }

    public Class<? extends ViewConfig> getErrorPage()
    {
        return errorPage;
    }
}
