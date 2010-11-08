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
package org.apache.myfaces.extensions.cdi.core.impl.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper for the JSF-Map trick in case of expressions which are read-only.
 *
 * @author Gerhard Petracek
 */
public abstract class UnmodifiableMap<K, V> extends HashMap<K, V>
{
    private static final long serialVersionUID = -7117422976009229722L;

    @Override
    public final V put(K key, V value)
    {
        throw new UnsupportedOperationException("It isn't allowed to modify this map!");
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> m)
    {
        throw new UnsupportedOperationException("It isn't allowed to modify this map!");
    }

    @Override
    public final V remove(Object key)
    {
        throw new UnsupportedOperationException("It isn't allowed to modify this map!");
    }

    @Override
    public final void clear()
    {
        throw new UnsupportedOperationException("It isn't allowed to modify this map!");
    }
}
