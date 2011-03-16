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
package org.apache.myfaces.extensions.cdi.test.owb;

import org.apache.myfaces.extensions.cdi.test.spi.WebAppAwareCdiTestContainer;
import org.apache.webbeans.context.ContextFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

/**
 * @author Gerhard Petracek
 */
public class ServletOpenWebBeans10TestContainer
        extends AbstractOpenWebBeans10TestContainer implements WebAppAwareCdiTestContainer
{
    public ServletOpenWebBeans10TestContainer()
    {
        this.testContainer = new HybridCdiTestOpenWebBeansContainer();
    }

    public void beginSession()
    {
        ContextFactory.activateContext(SessionScoped.class);
    }

    public void beginRequest()
    {
        ContextFactory.activateContext(RequestScoped.class);
    }

    public void endRequest()
    {
        ContextFactory.deActivateContext(RequestScoped.class);
    }

    public void endSession()
    {
        ContextFactory.deActivateContext(SessionScoped.class);
    }
}
