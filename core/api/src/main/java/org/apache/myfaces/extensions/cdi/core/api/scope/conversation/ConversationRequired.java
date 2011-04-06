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

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewMetaData;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author Gerhard Petracek
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented

@ViewMetaData(override = true)
public @interface ConversationRequired
{
    Class<?> conversationGroup() default ConversationRequired.class;

    /**
     * Default entry-point which will be used if a violation had been detected
     * @return entry-point page
     */
    Class<? extends ViewConfig> defaultEntryPoint();

    /**
     * Allowed entry-points for starting a conversation
     * @return possible entry-points
     */
    Class<? extends ViewConfig>[] entryPoints() default {};
}
