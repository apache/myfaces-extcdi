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
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public interface ScriptExecutor extends Serializable
{
    /**
     * Executes the given script
     * @param script script which has to be executed
     * @return result of the script
     */
    Object eval(String script);

    /**
     * Executes the given script with the given arguments
     * @param script script which has to be executed
     * @param arguments current arguments
     * @return result of the script
     */
    Object eval(String script, Map<String, Object> arguments);

    /**
     * Executes the given script with the given {@link Bindings}
     * @param script script which has to be executed
     * @param bindings current bindings
     * @return result of the script
     */
    Object eval(String script, Bindings bindings);

    /**
     * Executes the given script and the expected type of the result
     * @param script script which has to be executed
     * @param returnType type of the result
     * @return result of the script
     */
    <T> T eval(String script, Class<T> returnType);

    /**
     * Executes the given script with the given arguments and the expected type of the result
     * @param script script which has to be executed
     * @param arguments current arguments
     * @param returnType type of the result
     * @return result of the script
     */
    <T> T eval(String script, Map<String, Object> arguments, Class<T> returnType);

    /**
     * Executes the given script with the given {@link Bindings} and the expected type of the result
     * @param script script which has to be executed
     * @param bindings current bindings
     * @param returnType type of the result
     * @return result of the script
     */
    <T> T eval(String script, Bindings bindings, Class<T> returnType);
}
