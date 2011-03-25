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
package org.apache.myfaces.extensions.cdi.test.strategy.cargo;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;

import javax.enterprise.inject.Typed;

/**
 * Removes support of dependency injection for a higher performances if it isn't needed.
 *
 * @author Gerhard Petracek
 */
@Typed()
public abstract class AbstractSimpleCargoTestStrategy extends AbstractContainerAwareCargoTestStrategy
{
    /**
     * {@inheritDoc}
     */
    @Override
    //@Before
    public void before()
    {
        webClient = new WebClient(getBrowserVersion());
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //@After
    public void after()
    {
        webClient.closeAllWindows();
        webClient = null;
        ViewConfigCache.reset();
    }
}
