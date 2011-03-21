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
package org.apache.myfaces.extensions.cdi.test;

import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.impl.config.ServiceLoaderResolver;
import org.apache.myfaces.extensions.cdi.test.spi.WebAppAwareCdiTestContainer;
import org.apache.myfaces.extensions.cdi.test.spi.CdiTestContainer;
import org.apache.myfaces.extensions.cdi.test.spi.WebAppTestContainer;

import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class TestContainerFactory
{
    public static WebAppTestContainer getNewJsfTestContainer()
    {
        List<WebAppTestContainer> testContainers =
                new ServiceLoaderResolver()
                        .resolveInstances(new ConfiguredValueDescriptor<String, WebAppTestContainer>()
                {
                    public String getKey()
                    {
                        return WebAppTestContainer.class.getSimpleName();
                    }

                    public Class<WebAppTestContainer> getTargetType()
                    {
                        return WebAppTestContainer.class;
                    }
                });

        if(testContainers.size() != 1)
        {
            if(testContainers.size() == 0)
            {
                throw new IllegalStateException("no implementation of " + WebAppTestContainer.class.getName()
                    + " detected.");
            }
            StringBuilder foundImplementations = new StringBuilder();

            for(WebAppTestContainer webAppTestContainer : testContainers)
            {
                foundImplementations.append("\n");
                foundImplementations.append(webAppTestContainer.getClass().getName());
            }
            throw new IllegalStateException("only one implementation of " + WebAppTestContainer.class.getName()
                    + " is allowed. Found implementations: " + foundImplementations.toString());
        }

        return testContainers.iterator().next();
    }

    public static CdiTestContainer getNewCdiTestContainer(boolean createServletContext /*TODO*/)
    {
        List<CdiTestContainer> testContainers =
                new ServiceLoaderResolver().resolveInstances(new ConfiguredValueDescriptor<String, CdiTestContainer>()
                {
                    public String getKey()
                    {
                        return CdiTestContainer.class.getSimpleName();
                    }

                    public Class<CdiTestContainer> getTargetType()
                    {
                        return CdiTestContainer.class;
                    }
                });

        CdiTestContainer standaloneTestContainer = null;
        for(CdiTestContainer testContainer : testContainers)
        {
            if(createServletContext && isServletContextAwareContainer(testContainer))
            {
                return testContainer;
            }
            else if(!isServletContextAwareContainer(testContainer))
            {
                standaloneTestContainer = testContainer;
            }
        }

        if(standaloneTestContainer != null)
        {
            return standaloneTestContainer;
        }
        if(createServletContext)
        {
            throw new IllegalStateException("no embedded test-cdi-container found");
        }
        else
        {
            throw new IllegalStateException("no stand-alone test-cdi-container found");
        }
    }

    private static boolean isServletContextAwareContainer(CdiTestContainer testContainer)
    {
        return WebAppAwareCdiTestContainer.class.isAssignableFrom(testContainer.getClass());
    }
}
