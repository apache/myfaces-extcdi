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

import javax.interceptor.InterceptorBinding;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * Interceptor for securing beans.
 * It's also possible to use it as meta-annotation for type-safe view-configs.
 */
@Target({TYPE, METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented

//cdi annotations
@InterceptorBinding
public @interface Secured
{
    /**
     * {@link AccessDecisionVoter}s which will be invoked before accessing the intercepted instance or in case of
     * view-configs before a view gets used.
     *
     * @return the configured access-decision-voters which should be used for the voting process
     */
    @Nonbinding
    Class<? extends AccessDecisionVoter>[] value();

    /**
     * Optional inline error-view if it is required to show an error-page
     * which is different from the default error page.
     * @return type-safe view-config of the page which should be used as error-view
     */
    @Nonbinding
    Class<? extends ViewConfig> errorView() default DefaultErrorView.class;
}
