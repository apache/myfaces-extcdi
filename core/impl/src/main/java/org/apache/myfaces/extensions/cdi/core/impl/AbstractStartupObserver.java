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
package org.apache.myfaces.extensions.cdi.core.impl;

import org.apache.myfaces.extensions.cdi.core.api.config.AbstractAttributeAware;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfigEntry;
import org.apache.myfaces.extensions.cdi.core.impl.util.ProxyUtils;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base implementation for startup observers
 */
public abstract class AbstractStartupObserver
{
    protected Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    protected CodiCoreConfig codiCoreConfig;

    protected String separator = System.getProperty("line.separator");

    //generic alternative to #toString to avoid an overriden #toString at custom implementations
    protected String getConfigInfo(CodiConfig config)
    {
        StringBuilder info = new StringBuilder();

        List<String> methodNames = new ArrayList<String>();

        Class currentClass = ProxyUtils.getUnproxiedClass(config.getClass());
        while (currentClass != null &&
                !Object.class.getName().equals(currentClass.getName()) &&
                !AbstractAttributeAware.class.getName().equals(currentClass.getName()))
        {

            info.append("config implementation: ");
            info.append(currentClass.getName());
            info.append(separator);

            //inspect the other methods of the implementing class
            for(Method currentMethod : currentClass.getDeclaredMethods())
            {
                if(!currentMethod.isAnnotationPresent(ConfigEntry.class) ||
                        methodNames.contains(currentMethod.getName()))
                {
                    continue;
                }

                methodNames.add(currentMethod.getName());

                info.append("   method:\t").append(currentMethod.getName());
                info.append(separator);
                Object value;
                try
                {
                    value = currentMethod.invoke(config);
                    info.append("   value:\t").append(value.toString());
                }
                catch (IllegalAccessException e)
                {
                    info.append("   value:\t[unknown]");
                }
                catch (InvocationTargetException e)
                {
                    info.append("   value: [unknown]");
                }
                info.append(separator);
                info.append(separator);
            }

            currentClass = currentClass.getSuperclass();
        }

        return info.toString();
    }
}
