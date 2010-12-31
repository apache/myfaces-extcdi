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
import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;
import org.apache.myfaces.extensions.cdi.core.impl.util.ProxyUtils;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractStartupObserver
{
    @Inject
    protected Logger logger;

    @Inject
    protected CodiCoreConfig codiCoreConfig;

    protected String separator = System.getProperty("line.separator");

    //generic alternative to #toString to avoid an overriden #toString at custom implementations
    protected String getConfigInfo(CodiConfig config)
    {
        StringBuilder info = new StringBuilder();

        Set<String> processedMethod = new HashSet<String>();
        createMethodFilter(processedMethod);

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
                if(processedMethod.contains(currentMethod.getName()))
                {
                    continue;
                }

                processedMethod.add(currentMethod.getName());

                info.append("   method-name:\t\t").append(currentMethod.getName());
                info.append(separator);
                Object value;
                try
                {
                    value = currentMethod.invoke(config);
                    info.append("   method-value:\t").append(value.toString());
                }
                catch (IllegalAccessException e)
                {
                    info.append("   method-value:\t[unknown]");
                }
                catch (InvocationTargetException e)
                {
                    info.append("   method-value: [unknown]");
                }
                info.append(separator);
                info.append(separator);
            }

            currentClass = currentClass.getSuperclass();
        }

        return info.toString();
    }

    protected void createMethodFilter(Set<String> processedMethod)
    {
        processedMethod.add("toString");
        processedMethod.add("equals");
        processedMethod.add("hashCode");
        processedMethod.add("finalize");
        processedMethod.add("clone");
    }
}
