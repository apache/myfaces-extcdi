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
package org.apache.myfaces.extensions.cdi.core.api.logging;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Gerhard Petracek
 */
@Target({PARAMETER, FIELD, METHOD})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface LoggerDetails
{
    //TODO add support for the i18n module

    /**
     * name of the logger which will be created
     *
     * @return name of the logger
     */
    @Nonbinding
    String name() default "";

    /**
     * name of the {@link java.util.ResourceBundle} which will be used
     * @return name of the resource-bundle
     */
    @Nonbinding
    String resourceBundleName() default "";

    /**
     * indicates if an anonymous logger should be used
     * @return true if an anonymous logger should be used, false otherwise
     */
    @Nonbinding
    boolean anonymous() default false;
}
