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
package org.apache.myfaces.extensions.cdi.core.api.resolver;

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;

import java.io.Serializable;

/**
 * Resolver which allows to resolve type-safe configs.
 * It allows an easier handling of config values.
 * 
 * @author Gerhard Petracek
 */
public interface ConfigResolver extends Serializable
{
    /**
     * Returns the active config for the given type.
     *
     * @param targetType the target config-type
     * @param <T> generic type
     * @return the config instance for the given type
     */
    public <T extends CodiConfig> T resolve(Class<T> targetType);
}
