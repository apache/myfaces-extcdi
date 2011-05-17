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
package org.apache.myfaces.extensions.cdi.core.api.activation;

import org.apache.myfaces.extensions.cdi.core.api.interpreter.ExpressionInterpreter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExpressionActivated
{
    /**
     * Expression which signals if the annotated bean should be active or not
     * @return expression-string which will be interpreted
     */
    String value();

    /**
     * Default config name: org.apache.myfaces.extensions.cdi.ExpressionActivated.properties
     * or myfaces-extcdi.properties as a default config file known by one of the default implementations of
     * {@link org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver}
     * @return config name
     */
    String configName() default "ExpressionActivated";

    /**
     * @return class of the interpeter which should be used (default leads to a simple config-property interpreter
     */
    Class<? extends ExpressionInterpreter> interpreter() default ExpressionInterpreter.class;
}
