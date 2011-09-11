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
package org.apache.myfaces.extensions.cdi.core.api.config.view;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be used as interceptor for JSF action methods or
 * as simple annotation at View-Configs annotated with {@see Page} or
 * sub-classes of such classes. These parameters have to be added for a navigation
 *
 * @author Gerhard Petracek
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Documented

@InterceptorBinding
public @interface ViewParameter
{
    /**
     * Key of the parameter
     * @return name of the key
     */
    @Nonbinding
    String key();

    /**
     * Value or EL-Expression of the parameter
     * @return value or expression
     */
    @Nonbinding
    String value();

    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    @Documented

    /**
     * Allows to specify multiple parameters (@see ViewParameter)
     */
    @InterceptorBinding
    public static @interface List
    {
        /**
         * 1-n parameters
         * @return parameters
         */
        @Nonbinding
        ViewParameter[] value();
    }
}
