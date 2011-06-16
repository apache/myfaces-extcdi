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
package org.apache.myfaces.extensions.cdi.alternative.implementation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Only for the config and SPI of CODI!
 *
 * Annotate a bean which implements a SPI of CODI or extends a config class of CODI.
 * In case of implementing a SPI it's required to register the custom class.
 * So it will be checked by the {@link java.util.ServiceLoader}.
 *
 * In case of extending a config class of CODI, you have to do the same.
 * However, since the config classes aren't abstract or have a special interface,
 * it's required to register the custom class in a file called:
 * META-INF/services/org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig
 *
 * @author Gerhard Petracek
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented

public @interface AlternativeImplementation
{
}
