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
package org.apache.myfaces.extensions.cdi.core.test.impl.serviceloader;

import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.provider.ServiceProvider;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.impl.CodiDeactivatorExtension;
import org.apache.myfaces.extensions.cdi.core.impl.activation.ActivationExtension;
import org.apache.myfaces.extensions.cdi.core.impl.config.LocalJndiResolver;
import org.apache.myfaces.extensions.cdi.core.impl.config.PropertyFileResolver;
import org.apache.myfaces.extensions.cdi.core.impl.config.ServiceLoaderResolver;
import org.apache.myfaces.extensions.cdi.core.impl.config.SystemPropertyResolver;
import org.apache.myfaces.extensions.cdi.core.impl.provider.SimpleServiceProvider;
import org.apache.myfaces.extensions.cdi.core.impl.provider.SimpleServiceProviderContext;
import org.apache.myfaces.extensions.cdi.core.test.impl.config.TestConfiguredValueResolver;
import org.apache.myfaces.extensions.cdi.core.test.impl.config.TestInterface;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.enterprise.inject.spi.Extension;
import java.util.Iterator;
import java.util.List;

public class ServiceProviderTest
{
    @Test
    @SuppressWarnings({"unchecked"})
    public void testExtensionsWithDefaultServiceProvider()
    {
        List<Extension> extensionList = ServiceProvider.loadServices(Extension.class);
        Assert.assertEquals(extensionList.size(), 3);

        Iterator<Extension> iterator = extensionList.iterator();

        Extension extension;
        while (iterator.hasNext())
        {
            extension = iterator.next();
            Assert.assertTrue(extension instanceof ActivationExtension ||
                extension instanceof BeanManagerProvider ||
                extension instanceof CodiDeactivatorExtension);

            iterator.remove();
        }

        Assert.assertEquals(extensionList.size(), 0);
    }

    @Test
    @SuppressWarnings({"unchecked"})
    public void testExtensionsWithDefaultServiceProviderWithSimple()
    {
        List<Extension> extensionList =
                ServiceProvider.loadServices(Extension.class, new SimpleServiceProviderContext<Extension>());
        Assert.assertEquals(extensionList.size(), 3);

        Iterator<Extension> iterator = extensionList.iterator();

        Extension extension;
        while (iterator.hasNext())
        {
            extension = iterator.next();
            Assert.assertTrue(extension instanceof ActivationExtension ||
                extension instanceof BeanManagerProvider ||
                extension instanceof CodiDeactivatorExtension);

            iterator.remove();
        }

        Assert.assertEquals(extensionList.size(), 0);
    }

    @Test
    @SuppressWarnings({"unchecked"})
    public void testExtensionsWithSimpleServiceProvider()
    {
        List<Extension> extensionList = new SimpleServiceProvider(Extension.class, new SimpleServiceProviderContext<Extension>()) {
            @Override
            public List<Extension> loadServiceImplementations()
            {
                return super.loadServiceImplementations();
            }
        }.loadServiceImplementations();
        Assert.assertEquals(extensionList.size(), 3);

        Iterator<Extension> iterator = extensionList.iterator();

        Extension extension;
        while (iterator.hasNext())
        {
            extension = iterator.next();
            Assert.assertTrue(extension instanceof ActivationExtension ||
                extension instanceof BeanManagerProvider ||
                extension instanceof CodiDeactivatorExtension);

            iterator.remove();
        }

        Assert.assertEquals(extensionList.size(), 0);
    }

    @Test
    public void testDeactivatedImplementationConditionalExtensions()
    {
        System.setProperty("env", "test");
        List<TestInterface> extensionList = ServiceProvider.loadServices(TestInterface.class);

        Assert.assertEquals(extensionList.size(), 0);
    }

    @Test
    public void testActivatedImplementationConditionalExtensions()
    {
        System.setProperty("env", "prod");
        List<TestInterface> extensionList = ServiceProvider.loadServices(TestInterface.class);

        Assert.assertEquals(extensionList.size(), 1);
    }

    @Test
    public void testOrderedExtensions()
    {
        List<ConfiguredValueResolver> extensionList = ServiceProvider.loadServices(ConfiguredValueResolver.class);

        Iterator<ConfiguredValueResolver> iterator = extensionList.iterator();

        Assert.assertEquals(extensionList.size(), 5);

        Assert.assertTrue(iterator.next() instanceof SystemPropertyResolver);
        Assert.assertTrue(iterator.next() instanceof TestConfiguredValueResolver);
        Assert.assertTrue(iterator.next() instanceof ServiceLoaderResolver);
        Assert.assertTrue(iterator.next() instanceof LocalJndiResolver);
        Assert.assertTrue(iterator.next() instanceof PropertyFileResolver);
    }

    @Test
    public void testDefaultImplementations()
    {
        Assert.assertNotNull(ClassUtils.tryToLoadClassForName(
                "org.apache.myfaces.extensions.cdi.core.impl.provider.DefaultServiceProvider"));
        Assert.assertNotNull(ClassUtils.tryToLoadClassForName(
                "org.apache.myfaces.extensions.cdi.core.impl.provider.SimpleServiceProvider"));
        Assert.assertNotNull(ClassUtils.tryToLoadClassForName(
                "org.apache.myfaces.extensions.cdi.core.impl.provider.DefaultServiceProviderContext"));
        Assert.assertNotNull(ClassUtils.tryToLoadClassForName(
                "org.apache.myfaces.extensions.cdi.core.impl.provider.SimpleServiceProviderContext"));
    }
}
