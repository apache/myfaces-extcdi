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
package org.apache.myfaces.extensions.cdi.scripting.api;

import javax.script.Bindings;
import java.io.Serializable;

/**
 * Helper for building scripts
 */
public interface ScriptBuilder extends Serializable
{
    /**
     * Adds a script to the current builder instance
     * @param script target script
     * @return current builder
     */
    ScriptBuilder script(String script);

    /**
     * Adds an argument with a specific nam to the current builder instance
     * @param name name of the argument
     * @param value value of the argument
     * @return current builder
     */
    ScriptBuilder namedArgument(String name, Object value);

    /**
     * Adds a {@link Bindings} instance to the current builder instance
     * @param bindings bindings which should be used
     * @return current builder
     */
    ScriptBuilder bindings(Bindings bindings);

    /**
     * Evaluates the script built with the current {@link ScriptBuilder}
     * @return result of the evaluated script
     */
    Object eval();
    
    /**
     * Evaluates the script built with the current {@link ScriptBuilder}
     * @param returnType target type of the result
     * @return result of the evaluated script
     */
    <T> T eval(Class<T> returnType);
}
