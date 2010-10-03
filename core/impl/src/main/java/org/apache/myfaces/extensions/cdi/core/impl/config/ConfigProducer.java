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
import org.apache.myfaces.extensions.cdi.core.api.resolver.ConfigResolver;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.utils.ApplicationCache;
import org.apache.myfaces.extensions.cdi.core.impl.utils.ClassDeactivation;
import static org.apache.myfaces.extensions.cdi.core.impl.config.ConfigStorage.*;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ConfigProducer
{
    @Inject
    private BeanManager beanManager;

    @Produces
    @ApplicationScoped
    public Set<CodiConfig> createCodiConfig()
    {
        ClassLoader classLoader = getClassLoader();

        if(!isConfigInitialized(classLoader))
        {
            initConfig(classLoader);
        }
        return Collections.unmodifiableSet(getCodiConfig(classLoader));
    }

    private synchronized void initConfig(ClassLoader classLoader)
    {
        if(isConfigInitialized(classLoader))
        {
            return; //paranoid mode
        }

        createConfig(classLoader);

        setConfigInitialized(classLoader);
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

    private void createConfig(ClassLoader classLoader)
    {
        Set<? extends Bean> configBeans = this.beanManager.getBeans(CodiConfig.class);

        initConfigCache(configBeans.size(), classLoader);

        CreationalContext<CodiConfig> creationalContext;
        CodiConfig currentCodiConfig;
        for(Bean<CodiConfig> codiConfigBean : configBeans)
        {
            creationalContext = this.beanManager.createCreationalContext(codiConfigBean);

            currentCodiConfig = codiConfigBean.create(creationalContext);

            if(ClassDeactivation.isClassActivated(currentCodiConfig.getClass()))
            {
                addCodiConfig(currentCodiConfig, classLoader);
            }
        }
    }

    private static ClassLoader getClassLoader()
    {
        return ClassUtils.getClassLoader(null);
    }
}
