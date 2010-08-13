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
package org.apache.myfaces.extensions.cdi.core.test.utils;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.webbeans.cditest.CdiTestContainer;
import org.apache.webbeans.cditest.CdiTestContainerLoader;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.logging.Logger;

/**
 */
public class ContainerTestBase
{

    protected Logger log;
    protected static volatile CdiTestContainer containerStarter;
    protected static volatile int containerRefCount = 0;

    /**
     * Returns an instance of the given class.
     *
     * @param <T>
     *            Type of Bean
     * @param clazz
     *            Class of Bean
     * @param qualifiers
     *            List of qualifiers
     * @return Bean instance
     */
    protected <T> T getBeanInstance(Class<T> clazz, Annotation... qualifiers)
    {
        return containerStarter.getInstance(clazz, qualifiers);
    }

    /**
     * Starts container or clean contextual instances before each test.
     * 
     * @throws Exception in case of severe problem
     */
    @BeforeMethod
    public final void setUp() throws Exception
    {
        log = Logger.getLogger(this.getClass().getName());
        containerRefCount++;

        if (containerStarter == null)
        {
            ProjectStageProducer.setProjectStage(ProjectStage.UnitTest);

            containerStarter = CdiTestContainerLoader.getCdiContainer();
            containerStarter.bootContainer();
        }
        else
        {
            cleanInstances();
        }
    }

    @BeforeClass
    public final void beforeClass() throws Exception
    {
        setUp();
    }
    /**
     * Shuts down container.
     * @throws Exception in case of severe problem
     */
    @AfterMethod
    public final void tearDown() throws Exception
    {
        containerRefCount--;
    }

    public final void cleanInstances() throws Exception
    {
        containerStarter.stopContexts();
        containerStarter.startContexts();
    }

    public void finalize() throws Throwable
    {
        if (containerStarter != null)
        {
            containerStarter.shutdownContainer();
            containerStarter = null;
        }

        super.finalize();
    }
}
