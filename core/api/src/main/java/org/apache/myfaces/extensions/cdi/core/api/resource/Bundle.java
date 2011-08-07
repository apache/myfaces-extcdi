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
package org.apache.myfaces.extensions.cdi.core.api.resource;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Optional to specify a different bundle name
 *
 * @author Gerhard Petracek
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, CONSTRUCTOR, METHOD, FIELD, PARAMETER})

public @interface Bundle
{
    /**
     * Allows to specify the class which is mapped to the resource-bundle
     * @return class which represents the resource-bundle
     */
    Class<?> value() default Class.class;

    /**
     * Esp. useful if the class which is mapped to the resource-bundle has a different name
     * and can't be mapped to the bundle via convention. #name allows to explicitly specify the name of the bundle.
     * @return the overridden name which should be used to identify the resource-bundle
     */
    String name() default "";
}
