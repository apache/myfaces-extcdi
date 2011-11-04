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

import java.io.Serializable;

/**
 * Key for implementation caches
 */
class ArtifactCacheKey<K extends Serializable>
{
    private final K key;
    private final Class targetType;

    ArtifactCacheKey(K key, Class targetType)
    {
        if(key == null)
        {
            //TODO
            throw new IllegalStateException("Please provide a key for " + targetType);
        }
        if(targetType == null)
        {
            //TODO
            throw new IllegalStateException("Please provide a targetType");
        }

        this.key = key;
        this.targetType = targetType;
    }

    /*
     * generated
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ArtifactCacheKey))
        {
            return false;
        }

        ArtifactCacheKey that = (ArtifactCacheKey) o;

        if (!key.equals(that.key))
        {
            return false;
        }
        if (!targetType.equals(that.targetType))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key.hashCode();
        result = 31 * result + targetType.hashCode();
        return result;
    }
}
