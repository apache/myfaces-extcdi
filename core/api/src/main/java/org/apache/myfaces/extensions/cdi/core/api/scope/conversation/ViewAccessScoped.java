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
package org.apache.myfaces.extensions.cdi.core.api.scope.conversation;

import javax.enterprise.context.NormalScope;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;

/**
 * The scope is active as long as it's bean is accessed by a view.
 * Basically &#064;ViewAccessScoped is a CODI Conversation which
 * automatically gets ended when the next view tree gets restored
 * without hitting the bean.
 *
 * &#064;ViewAccessScoped currently doesn't support Conversation-groups,
 * thus the {@link Conversation} always only contains the bean itself.
 */
//internal hint:
//this scope doesn't support conversation-groups.
//if such a support is requested, we lose one of the caches.
//cdi annotations
@Target({METHOD,TYPE,FIELD})
@Retention(RUNTIME)
@Inherited
@Documented
@NormalScope(passivating=true)
public @interface ViewAccessScoped
{
}
