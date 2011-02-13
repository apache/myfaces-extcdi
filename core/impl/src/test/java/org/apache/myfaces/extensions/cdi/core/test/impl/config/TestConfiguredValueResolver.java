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
package org.apache.myfaces.extensions.cdi.core.test.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver;

import java.util.List;
import java.util.Collections;

public class TestConfiguredValueResolver implements ConfiguredValueResolver
{
    private static boolean called = false;
    private static boolean activated = false;

    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        if(activated)
        {
            called = true;
        }
        return Collections.emptyList();
    }

    static boolean isCalled()
    {
        return called;
    }

    static void setActivated(boolean newValue)
    {
        activated = newValue;
    }

    public boolean isActivated()
    {
        return activated;
    }
}
