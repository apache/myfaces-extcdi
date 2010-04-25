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
package org.apache.myfaces.extensions.cdi.message.api;

import java.io.Serializable;

/**
 * a key/value based config
 *
 * @author Gerhard Petracek
 */
public interface GenericConfig extends Serializable
{
    /**
     * to add a key/value pair to the config
     *
     * @param key key for the current value
     * @param value value for the current key
     * @return the instance of the config to allow a fluent api
     */
    GenericConfig addProperty(String key, Serializable value);

    /**
     * @param key the key of the value in question
     * @return the value for the given key - null otherwise
     */
    Serializable getProperty(String key);

    /**
     * @param key the key of the value in question
     * @param targetType type of the expected value
     * @return the value for the given key - null otherwise
     */
    <T extends Serializable> T getProperty(String key, Class<T> targetType);

    /**
     * @param key the key of the value in question
     * @return true if a value for the given key is available - false otherwise
     */
    boolean containsProperty(String key);
}