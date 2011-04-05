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

import javax.enterprise.inject.spi.Bean;
import java.util.HashSet;
import java.util.Set;

/**
 * This feature is optional and has to be activated via
 * {@link org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig#isInvalidBeanCreationEventEnabled()}
 *
 * @author Gerhard Petracek
 */
public abstract class AbstractBeanCreationDecisionVoter
        extends AbstractDecisionVoter implements BeanCreationDecisionVoter
{
    /**
     * {@inheritDoc}
     */
    public <T> Set<SecurityViolation> checkPermission(Bean<T> beanToCheck)
    {
        Set<SecurityViolation> result = new HashSet<SecurityViolation>();

        checkPermission(beanToCheck, result);

        return result;
    }

    /**
     * Allows an easier implementation in combination with {@link #newSecurityViolation(String)}.
     *
     * @param beanToCheck bean which has to be checked
     * @param violations set for adding violations
     */
    protected abstract <T> void checkPermission(Bean<T> beanToCheck, Set<SecurityViolation> violations);
}
