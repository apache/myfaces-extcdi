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
package org.apache.myfaces.extensions.cdi.core.custom.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Gerhard Petracek
 */
@Deprecated
public class ServiceProvider<T> extends org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProvider<T>
{
    protected ServiceProvider(Class<T> serviceType,
              org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProviderContext serviceProviderContext)
    {
        super(serviceType, serviceProviderContext);
    }

    @Override
    protected List<T> loadServiceImplementations()
    {
        //just a simple version for testing - TODO move tests to core-impl so we don't need this class at all
        ServiceLoader<T> serviceLoader = ServiceLoader.load(this.serviceType);

        List<T> result = new ArrayList<T>();
        for(T instance : serviceLoader)
        {
            result.add(instance);
        }
        return result;
    }
}
