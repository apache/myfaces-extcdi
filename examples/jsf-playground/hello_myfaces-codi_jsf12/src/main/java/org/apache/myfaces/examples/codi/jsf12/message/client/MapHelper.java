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
package org.apache.myfaces.examples.codi.jsf12.message.client;

import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * @author Gerhard Petracek
 */
abstract class MapHelper<K, V> implements Map<K, V>
{
    private final String unsupportedMessage = "unsupported operation";

    protected abstract V getValue(K key);

    public final V get(Object key)
    {
        return getValue((K)key);
    }

    public final int size()
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final boolean isEmpty()
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final boolean containsKey(Object key)
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final boolean containsValue(Object value)
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final V put(K key, V value)
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final V remove(Object key)
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final void putAll(Map<? extends K, ? extends V> m)
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final void clear()
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final Set<K> keySet()
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final Collection<V> values()
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }

    public final Set<Entry<K, V>> entrySet()
    {
        throw new UnsupportedOperationException(unsupportedMessage);
    }
}
