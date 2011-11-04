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

import javax.interceptor.InterceptorBinding;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Allows to restrict e.g. phase-listener methods to specific views.
 * Use an existing view-config OR ManualView.class + the view-id string/s.
 *
 * Furthermore it's possible to use it at the class-level for configuring page-controllers
 * (as an alternative to specifying the (page-)bean in the view-config).
 * That means e.g.
 *
 * \@View(DemoPages.Page1.class)
 * //...
 * public class PageBean1 implements Serializable
 * {
 *     \@PreRenderView
 *     protected void preRenderView()
 *     {
 *         //...
 *     }
 *
 *     //...
 * }
 *
 * leads to the invocation of the pre-render-view logic before page1 gets rendered and
 * it won't be called for other pages.
 */

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented

//cdi annotations
@InterceptorBinding
public @interface View
{
    /**
     * Specifies the pages via type-safe {@link ViewConfig}.
     * Use {@link ManualView} if it is required to use strings as view-id.
     * @return views which should be aware of the bean or observer
     */
    @Nonbinding
    Class<? extends ViewConfig>[] value();

    /**
     * Alternative to #value in order to use hardcoded strings instead of type-safe view-configs.
     * @return views which should be aware of the bean or observer
     */
    @Nonbinding
    String[] inline() default "";
}