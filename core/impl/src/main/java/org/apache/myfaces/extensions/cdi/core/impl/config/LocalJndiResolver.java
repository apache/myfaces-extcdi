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
package org.apache.myfaces.extensions.cdi.core.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.impl.util.JndiUtils;

import javax.enterprise.inject.Typed;
import java.util.List;
import java.util.Collections;

/**
 * {@link org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver} for JNDI
 */
@Typed()
@InvocationOrder(300)
public class LocalJndiResolver extends AbstractConfiguredValueResolver
{
    private static final String BASE_NAME = "java:comp/env/myfaces-codi/";

    /**
     * {@inheritDoc}
     */
    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        String key;
        if(descriptor.getKey() instanceof String && ((String)descriptor.getKey()).startsWith("java:comp/env"))
        {
            key = (String)descriptor.getKey();
        }
        else
        {
            key = BASE_NAME + descriptor.getKey();
        }

        T resolvedValue = null;

        try
        {
            resolvedValue = JndiUtils.lookup(key, descriptor.getTargetType());
        }
        catch (Exception e)
        {
            //do nothing it was just a try
        }

        if(resolvedValue == null)
        {
            return Collections.emptyList();
        }

        add(resolvedValue);

        return getConfiguredValues(descriptor.getTargetType());
    }
}