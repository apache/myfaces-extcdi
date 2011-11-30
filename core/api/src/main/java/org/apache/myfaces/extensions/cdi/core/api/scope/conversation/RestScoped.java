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
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The scope is similar to &#064;ConversationScoped but it will
 * automatically end once the view get's invoked with a GET and the
 * viewParams are different than on the previous invoke.
 * The conversation will also get resetted when you hit a different
 * View via a GET request.
 *
 * Be aware that PostRedirectGet (faces-redirect=true) might cause
 * the conversation to end. If you use PRG only to display a result-page
 * then it should not have immediate effect because all the information
 * will also get transported via GET parameters. But be aware that
 * the target page might again execute any bean initialization.
 */
@Target({METHOD,TYPE,FIELD})
@Retention(RUNTIME)
@Inherited
@Documented
@NormalScope(passivating=true)
public @interface RestScoped
{
}
