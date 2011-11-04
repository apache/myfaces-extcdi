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
package org.apache.myfaces.extensions.cdi.core.api.resource.bundle;

import java.io.Serializable;
import java.util.Locale;

/**
 * Allows to implement type-safe resource-bundles
 */
public interface ResourceBundle extends Serializable
{
    /**
     * Allows to specify a bundle name
     * @param bundleName name of the bundle which should be used
     * @return the instance itself
     */
    ResourceBundle useBundle(String bundleName);

    /**
     * Allows to specify a class which is mapped to a bundle
     * @param bundleClass class mapped to a bundle
     * @return the instance itself
     */
    ResourceBundle useBundle(Class<?> bundleClass);

    /**
     * Allows to specify a locale
     * @param locale locale which should be used
     * @return the instance itself
     */
    ResourceBundle useLocale(Locale locale);

    /**
     * Returns the value for the given key (and the configured bundle and local).
     * If the key extends a custom class and there is no specified bundle-name,
     * the name of the super-class will be used as bundle-name.
     *
     * @param key current key
     * @return the value for the given key
     */
    String getValue(Class<? extends BundleKey> key);

    /**
     * Returns the value for the given key (and the configured bundle and local).
     *
     * @param key current key
     * @return the value for the given key
     */
    String getValue(String key);
}
