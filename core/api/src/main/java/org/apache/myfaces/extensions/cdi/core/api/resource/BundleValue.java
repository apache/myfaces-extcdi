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
package org.apache.myfaces.extensions.cdi.core.api.resource;

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;

import java.lang.annotation.Annotation;

/**
 * Base class which has to be extended if a key resolves directly to the value.
 * Compared to {@link BundleKey} it isn't needed to use the {@link ResourceBundle} manually
 * for resolving the value. It's possible to call #toString in-/directly.
 *
 * @author Gerhard Petracek
 */
public abstract class BundleValue implements BundleKey
{
    private transient ResourceBundle resourceBundle;

    private static Class<? extends Annotation> qualifierClass;

    static
    {
        try
        {
            qualifierClass = (Class<? extends Annotation>)
                    Class.forName("org.apache.myfaces.extensions.cdi.jsf.api.Jsf");
        }
        catch (Exception e)
        {
            qualifierClass = null;
        }
    }

    /**
     * Returns the value of the resource-bundle represented by this key
     *
     * @return the value of the resource-bundle represented by this key
     */
    @Override
    public String toString()
    {
        return getResourceBundle().getValue(getClass());
    }

    //override it e.g. for test-cases
    protected ResourceBundle getResourceBundle()
    {
        if(this.resourceBundle == null)
        {
            Class bundleClass = getClass().getSuperclass();

            if(!bundleClass.isAnnotationPresent(Bundle.class))
            {
                bundleClass = null;
                for(Class interfaceClass : getClass().getInterfaces())
                {
                    if(interfaceClass.isAnnotationPresent(Bundle.class))
                    {
                        bundleClass = interfaceClass;
                        break;
                    }
                }
            }

            if(bundleClass == null)
            {
                throw new IllegalStateException(getClass() + " has to extend a class or implement an interface " +
                        "which is annotated with @" + Bundle.class.getName());
            }

            if(qualifierClass != null)
            {
                this.resourceBundle = BeanManagerProvider.getInstance()
                        .getContextualReference(ResourceBundle.class, DefaultAnnotation.of(qualifierClass));
            }
            else
            {
                this.resourceBundle = BeanManagerProvider.getInstance()
                        .getContextualReference(ResourceBundle.class);
            }
            this.resourceBundle.useBundle(bundleClass);
        }
        return resourceBundle;
    }
}
