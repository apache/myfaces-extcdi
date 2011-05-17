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

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.inject.Typed;
import java.util.*;

/**
 * @author Gerhard Petracek
 */
@Typed()
@InvocationOrder(400)
public class PropertyFileResolver extends AbstractConfiguredValueResolver
{
    private static final String BASE_NAME = "org.apache.myfaces.extensions.cdi.";

    /**
     * {@inheritDoc}
     */
    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        String key = "" + descriptor.getKey();

        if (!(String.class.isAssignableFrom(descriptor.getTargetType()) &&
                key.contains(".") && key.contains("_")))
        {
            return Collections.emptyList();
        }

        String bundleName = BASE_NAME + key.substring(0, key.indexOf("."));

        ResourceBundle resourceBundle;

        try
        {
            try
            {
                resourceBundle = ResourceBundle
                        .getBundle(bundleName, Locale.getDefault(), ClassUtils.getClassLoader(null));
            }
            catch (MissingResourceException e)
            {
                resourceBundle = ResourceBundle.getBundle("myfaces-extcdi");
            }

            if (resourceBundle != null)
            {
                String configuredValue = resourceBundle.getString(key.substring(key.indexOf(".") + 1));

                add(configuredValue);

                if (configuredValue == null)
                {
                    return Collections.emptyList();
                }
            }
        }
        catch (MissingResourceException e2)
        {
            return Collections.emptyList();
        }

        return getConfiguredValues(descriptor.getTargetType());
    }
}