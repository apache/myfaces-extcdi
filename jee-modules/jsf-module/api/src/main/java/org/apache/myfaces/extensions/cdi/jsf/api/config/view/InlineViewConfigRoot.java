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
package org.apache.myfaces.extensions.cdi.jsf.api.config.view;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Only required for inline view-configs!
 *
 * Annotate a marker class or interface with this annotation for marking the package of the marker as base package.
 *
 * Simple example:
 *
 * Marker:
 * my.customPackage.view.InlineViewConfigRootMarker //annotated with @InlineViewConfigRoot(pageBeanPostfix = "Page")
 *
 * PageBeans:
 * my.customPackage.view.IndexPage //annotated with @Page and implements ViewConfig
 * -> view-id: /index.xhtml
 *
 * my.customPackage.view.registration.RegistrationStep01 //annotated with @Page and implements ViewConfig
 * -> view-id: /registration/registrationStep01.xhtml
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface InlineViewConfigRoot
{
    /**
     * Allows to customize the base-path
     * @return base-path which should be used for the view-ids
     */
    String basePath() default ".";

    /**
     * Allows to implement beans which use a common post-fix which won't be part of the view-id
     * @return optional common post-fix for page-beans
     */
    String[] pageBeanPostfix() default "";
}
