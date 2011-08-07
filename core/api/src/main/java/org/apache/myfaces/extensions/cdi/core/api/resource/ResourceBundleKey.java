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

/**
 * Base class which has to be extended if a key should be injected.
 *
 * @author Gerhard Petracek
 */
public abstract class ResourceBundleKey implements BundleKey
{
    private transient ResourceBundle resourceBundle;

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

    private ResourceBundle getResourceBundle()
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

            this.resourceBundle = BeanManagerProvider.getInstance()
                    .getContextualReference(ResourceBundle.class, DefaultAnnotation.of(Bundle.class));
            this.resourceBundle.useBundle(bundleClass);
        }
        return resourceBundle;
    }
}
