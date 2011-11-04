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
package org.apache.myfaces.extensions.cdi.test.strategy;

import org.apache.myfaces.extensions.cdi.test.TestContainerFactory;
import org.apache.myfaces.extensions.cdi.test.spi.CdiTestContainer;
import org.apache.myfaces.extensions.cdi.test.spi.ServletContainerAwareCdiTestContainer;

import javax.enterprise.context.ContextNotActiveException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows dependency injection in JUnit tests and implementing JUnit tests in web-projects.
 */
public abstract class AbstractServletAwareTestStrategy implements TestStrategy
{
    protected CdiTestContainer testContainer;

    /**
     * {@inheritDoc}
     */
    public void beforeMethod()
    {
        before();
    }

    /**
     * {@inheritDoc}
     */
    public void afterMethod()
    {
        try
        {
            after();
        }
        catch (ContextNotActiveException e)
        {
            Logger logger = Logger.getLogger(AbstractServletAwareTestStrategy.class.getName());

            if(logger.isLoggable(Level.WARNING))
            {
                logger.log(Level.WARNING, "this exception usually occurs due to an owb issue", e);
            }
        }
    }

    /**
     * Bootstraps a new container
     */
    //@Before
    public void before()
    {
        this.testContainer = TestContainerFactory.createTestContainer(ServletContainerAwareCdiTestContainer.class);

        this.testContainer.initEnvironment();
        this.testContainer.startContainer();
        this.testContainer.startContexts();
        ((ServletContainerAwareCdiTestContainer)this.testContainer).startSession();
        ((ServletContainerAwareCdiTestContainer)this.testContainer).startRequest();

        this.testContainer.injectFields(this);
    }

    /**
     * Stops the current container
     */
    //@After
    public void after()
    {
        ((ServletContainerAwareCdiTestContainer)this.testContainer).stopSession();
        ((ServletContainerAwareCdiTestContainer)this.testContainer).stopRequest();
        this.testContainer.stopContexts();
        this.testContainer.stopContainer();
    }
}
