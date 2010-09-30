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

import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.DeactivatedCodiConfig;
import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;
import org.apache.myfaces.extensions.cdi.core.impl.utils.ApplicationCache;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Arrays;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ConfigProducer
{
    private static Boolean configInitialized;

    private static Set<CodiConfig> configSet;

    private static Set<Class<? extends CodiConfig>> configFilter;

    @Inject
    private BeanManager beanManager;

    @Produces
    @ApplicationScoped
    public Set<CodiConfig> createCodiConfig()
    {
        if(configInitialized == null)
        {
            initConfig();
        }
        return Collections.unmodifiableSet(configSet);
    }

    private synchronized void initConfig()
    {
        if(configInitialized != null)
        {
            return; //paranoid mode
        }

        createConfigFilter();

        createConfig();

        configInitialized = true;
    }

    @Produces
    @ApplicationScoped
    public ConfigResolver createSpecializedCodiConfig()
    {
        return new ConfigResolver()
        {
            private static final long serialVersionUID = -4410313406799415118L;

            public <T extends CodiConfig> T resolve(Class<T> targetType)
            {
                CodiConfig codiConfig = ApplicationCache.getConfig(targetType);

                if(codiConfig != null)
                {
                    //noinspection unchecked
                    return (T)codiConfig;
                }

                Set<CodiConfig> configs = createCodiConfig();

                for(CodiConfig config : configs)
                {
                    if(targetType.isAssignableFrom(config.getClass()))
                    {
                        if(!config.getClass().getName().startsWith("org.apache.myfaces.extensions.cdi."))
                        {
                            //noinspection unchecked
                            return (T)config;
                        }
                        else
                        {
                            codiConfig = config;
                        }
                    }
                }

                ApplicationCache.setConfig(targetType, codiConfig);
                //noinspection unchecked
                return (T)codiConfig;
            }
        };
    }

    private void createConfigFilter()
    {
        Set<? extends Bean> deactivatedConfigBeans = this.beanManager.getBeans(DeactivatedCodiConfig.class);

        configFilter = new HashSet<Class<? extends CodiConfig>>(deactivatedConfigBeans.size());

        CreationalContext<DeactivatedCodiConfig> creationalContext;
        Class<? extends CodiConfig>[] filteredCodiConfigClasses;
        for(Bean<DeactivatedCodiConfig> deactivatedConfigBean : deactivatedConfigBeans)
        {
            creationalContext = this.beanManager.createCreationalContext(deactivatedConfigBean);

            filteredCodiConfigClasses = deactivatedConfigBean.create(creationalContext).getDeactivatedConfigs();

            configFilter.addAll(Arrays.asList(filteredCodiConfigClasses));
        }
    }

    private void createConfig()
    {
        Set<? extends Bean> configBeans = this.beanManager.getBeans(CodiConfig.class);

        configSet = new HashSet<CodiConfig>(configBeans.size());

        CreationalContext<CodiConfig> creationalContext;
        CodiConfig currentCodiConfig;
        for(Bean<CodiConfig> codiConfigBean : configBeans)
        {
            creationalContext = this.beanManager.createCreationalContext(codiConfigBean);

            currentCodiConfig = codiConfigBean.create(creationalContext);

            if(!configFilter.contains(currentCodiConfig.getClass()))
            {
                configSet.add(currentCodiConfig);
            }
        }
    }
}
