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
package org.apache.myfaces.extensions.cdi.core.test.impl.resource;

import org.apache.myfaces.extensions.cdi.core.api.resource.ResourceBundle;
import org.apache.myfaces.extensions.cdi.core.impl.resource.ResourceBundleProducer;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.enterprise.inject.spi.InjectionPoint;

public class ResourceBundleTest
{
    @Test
    public void testNonTypesafeBundleKey() throws Exception
    {
        ResourceBundle resourceBundle = getResourceBundle();
        Assert.assertEquals(resourceBundle.useBundle(getClass().getPackage().getName() + ".testBundle").getValue("value1"), "1");
    }

    @Test
    public void testTypesafeBundleKey() throws Exception
    {
        ResourceBundle resourceBundle = getResourceBundle();
        Assert.assertEquals(resourceBundle.getValue(Testbundle.MyValue.class), "2");
        Assert.assertEquals(resourceBundle.getValue(Testbundle.MyValue1.class), "3");
    }

    private ResourceBundle getResourceBundle()
    {
        return new ResourceBundleProducer()
        {
            @Override
            public ResourceBundle injectableResourceBundle(InjectionPoint injectionPoint)
            {
                return createDefaultResourceBundle();
            }
        }.injectableResourceBundle(null);
    }
}
