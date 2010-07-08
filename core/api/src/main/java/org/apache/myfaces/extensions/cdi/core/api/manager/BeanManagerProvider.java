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
package org.apache.myfaces.extensions.cdi.core.api.manager;

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>This class provides access to the BeanManager
 * by registring the current BeanManager in an extension and
 * making it available via a singleton factory</p>
 * <p>This is really handy if you like to access CDI functionality
 * from places where no injection is available.</p>
 *
 * <p>Usage:<p/>
 * <pre>
 * BeanManager bm = BeanManagerProvider.getInstance().getBeanManager();
 * </pre>
 */
public class BeanManagerProvider implements Extension
{

    private static BeanManagerProvider bmp = null;

    private volatile Map<ClassLoader, BeanManager> bms = new ConcurrentHashMap<ClassLoader, BeanManager>();


    /**
     * Singleton accessor
     * @return the singleton BeanManagerProvider
     */
    public static BeanManagerProvider getInstance()
    {
        return bmp;
    }


    /**
     * @return The {@link BeanManager}
     */
    public BeanManager getBeanManager()
    {
        ClassLoader cl = ClassUtils.getClassLoader(null);

        return bms.get(cl);
    }

    /**
     * It basiscally doesn't matter which of the system events we use,
     * but basically we
     * @param abd event which we don't actually use ;)
     * @param beanManager the BeanManager we store and make available.
     */
    public void setBeanManager(@Observes AfterBeanDiscovery abd, BeanManager beanManager)
    {
        BeanManagerProvider bmpFirst = setBeanManagerProvider(this);

        ClassLoader cl = ClassUtils.getClassLoader(null);
        bmpFirst.bms.put(cl, beanManager);
    }

    /**
     * This function exists to prevent findbugs to complain about
     * setting a static member from a non-static function.
     * @return the first BeanManagerProvider 
     */
    private static BeanManagerProvider setBeanManagerProvider(BeanManagerProvider bmpIn)
    {
        if (bmp == null)
        {
            bmp = bmpIn;
        }

        return bmp;
    }
}
