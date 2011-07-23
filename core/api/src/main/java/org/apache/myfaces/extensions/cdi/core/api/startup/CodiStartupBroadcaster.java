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
package org.apache.myfaces.extensions.cdi.core.api.startup;

import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProvider;
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProviderContext;
import org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEventBroadcaster;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.tools.InvocationOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * This broadcaster has to be part of the api module because
 * {@link org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider}
 * has to use it. You don't have to use it directly. It's possible to register a
 * {@link org.apache.myfaces.extensions.cdi.core.api.startup.event.StartupEventBroadcaster}
 * for broadcasting custom events.
 * 
 * @author Gerhard Petracek
 */
public abstract class CodiStartupBroadcaster
{
    private CodiStartupBroadcaster()
    {
    }

    private static volatile Map<ClassLoader, Boolean> initialized = new HashMap<ClassLoader, Boolean>();

    private static transient Map<ClassLoader, List<Class<? extends StartupEventBroadcaster>>> broadcasterFilter =
            new ConcurrentHashMap<ClassLoader, List<Class<? extends StartupEventBroadcaster>>>();


    /**
     * Triggers the {@link StartupEventBroadcaster}s
     */
    public static void broadcastStartup()
    {
        ClassLoader classLoader = ClassUtils.getClassLoader(null);
        if (!initialized.containsKey(classLoader))
        {
            invokeStartupEventBroadcaster(classLoader);
        }
    }

    private static synchronized void invokeStartupEventBroadcaster(final ClassLoader classLoader)
    {
        // switch into paranoia mode
        if (initialized.containsKey(classLoader))
        {
            return;
        }

        List<StartupEventBroadcaster> startupEventBroadcasterList =
                ServiceProvider.loadServices(StartupEventBroadcaster.class,
                        new ServiceProviderContext<StartupEventBroadcaster>() {
                    @Override
                    public ClassLoader getClassLoader()
                    {
                        return classLoader;
                    }
                });

        List<Class<? extends StartupEventBroadcaster>> filter = broadcasterFilter.get(classLoader);
        if (filter == null)
        {
            filter = new CopyOnWriteArrayList<Class<? extends StartupEventBroadcaster>>();
            broadcasterFilter.put(classLoader, filter);
        }

        List<StartupEventBroadcaster> broadcasters = new ArrayList<StartupEventBroadcaster>();

        for (StartupEventBroadcaster startupEventBroadcaster : startupEventBroadcasterList)
        {
            if (!filter.contains(startupEventBroadcaster.getClass()))
            {
                filter.add(startupEventBroadcaster.getClass());
                broadcasters.add(startupEventBroadcaster);
            }
        }

        Collections.sort(broadcasters, new InvocationOrderComparator<StartupEventBroadcaster>());

        for (StartupEventBroadcaster startupEventBroadcaster : broadcasters)
        {
            startupEventBroadcaster.broadcastStartup();
        }

        initialized.put(classLoader, Boolean.TRUE);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException
    {
        objectInputStream.defaultReadObject();
    }
}
