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
package org.apache.myfaces.extensions.cdi.test.cargo;

import org.apache.myfaces.extensions.cdi.test.cargo.strategy.AbstractSimpleCargoTestStrategy;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for cargo tests
 */
public abstract class AbstractSimpleCargoTest extends AbstractSimpleCargoTestStrategy
{
    /**
     * {@inheritDoc}
     */
    @Before
    @Override
    public void beforeMethod()
    {
        super.beforeMethod();
    }

    /**
     * {@inheritDoc}
     */
    @After
    @Override
    public void afterMethod()
    {
        super.afterMethod();
    }
}
