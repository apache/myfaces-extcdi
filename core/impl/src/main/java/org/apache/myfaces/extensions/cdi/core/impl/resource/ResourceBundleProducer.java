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
package org.apache.myfaces.extensions.cdi.core.impl.resource;

import org.apache.myfaces.extensions.cdi.core.api.resource.Bundle;
import org.apache.myfaces.extensions.cdi.core.api.resource.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class ResourceBundleProducer
{
    @Produces
    @Dependent
    protected ResourceBundle injectableResourceBundle(InjectionPoint injectionPoint)
    {
        Bundle bundle = getBundleClass(injectionPoint.getAnnotated().getAnnotations());

        if(bundle != null)
        {
            Class bundleClass = bundle.value();

            if(bundleClass != null && !Class.class.getName().equals(bundleClass.getName()))
            {
                return createDefaultResourceBundle().useBundle(bundleClass);
            }

            if(!"".equals(bundle.name()))
            {
                return createDefaultResourceBundle().useBundle(bundle.name());
            }
        }
        return createDefaultResourceBundle();
    }

    private static Bundle getBundleClass(Set<Annotation> qualifiers)
    {
        for(Annotation qualifier : qualifiers)
        {
            if(Bundle.class.isAssignableFrom(qualifier.annotationType()))
            {
                return ((Bundle)qualifier);
            }
        }

        return null;
    }

    protected ResourceBundle createDefaultResourceBundle()
    {
        return new DefaultResourceBundle();
    }
}
