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
package org.apache.myfaces.extensions.cdi.core.api.provider;

import org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

    public static boolean isActive()
    {
        return bmp != null;
    }

    /**
     * Singleton accessor
     * @return the singleton BeanManagerProvider
     */
    public static BeanManagerProvider getInstance()
    {
        if(bmp == null)
        {
            //workaround for Mojarra (in combination with OWB and a custom WebBeansConfigurationListener and a custom
            //StartupBroadcaster for bootstrapping CDI)
            CodiStartupBroadcaster.broadcastStartup();
            //here bmp might not be null (depends on the broadcasters)
        }
        if(bmp == null)
        {
            throw new IllegalStateException("no " + BeanManagerProvider.class.getName() + " in place! " +
                "Please ensure that you configured the CDI implementation of your choice properly. " +
                "If your setup is correct, please clear all caches and compiled artifacts. " +
                "If there is still a problem, try one of the controlled bootstrapping add-ons for the CDI " +
                    "implementation you are using.");
        }
        return bmp;
    }


    /**
     * @return The {@link BeanManager}
     */
    public BeanManager getBeanManager()
    {
        ClassLoader cl = ClassUtils.getClassLoader(null);

        BeanManager result = bms.get(cl);

        if (result == null)
        {
            result = resolveBeanManagerViaJndi();

            if(result != null)
            {
                bms.put(cl, result);
            }
        }
        return result;
    }

    /**
     * Get the BeanManager from the JNDI registry.
     *
     * Workaround for jboss 6 (EXTCDI-74)
     * {@link #setBeanManager(javax.enterprise.inject.spi.AfterBeanDiscovery, javax.enterprise.inject.spi.BeanManager)}
     * is called in context of a different classloader
     *
     * @return current {@link javax.enterprise.inject.spi.BeanManager} which is provided via jndi
     */
    private BeanManager resolveBeanManagerViaJndi()
    {
        try
        {
            return (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        }
        catch (NamingException e)
        {
            //workaround didn't work -> force NPE
            return null;
        }
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

        CodiStartupBroadcaster.broadcastStartup();
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
