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
package org.apache.myfaces.extensions.cdi.core.api.config;

import org.apache.myfaces.extensions.cdi.core.api.activation.Deactivatable;

import java.util.List;

/**
 * Allows to customize CODI in case of extension points which aren't supported by CDI.
 * E.g. for artifacts which are needed before the bootstrapping process is finished.
 *
 * @author Gerhard Petracek
 */
public interface ConfiguredValueResolver extends Deactivatable
{
    /**
     * Resolves 0-n instances configured for the given key of the type which is provided via the
     * {@link ConfiguredValueDescriptor}.
     *
     * @param descriptor given descriptor for the configured-value
     * @param <K> type of the key
     * @param <T> type of the configured value
     * @return all configured values for the given descriptor
     */
    <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor);
}
