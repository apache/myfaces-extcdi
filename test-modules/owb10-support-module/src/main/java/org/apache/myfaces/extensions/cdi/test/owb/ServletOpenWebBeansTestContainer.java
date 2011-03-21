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

import org.apache.myfaces.extensions.cdi.test.spi.ServletContainerAwareCdiTestContainer;
import org.apache.webbeans.cditest.CdiTestContainerLoader;

/**
 * @author Gerhard Petracek
 */
public class ServletOpenWebBeansTestContainer
        extends AbstractOpenWebBeansTestContainer implements ServletContainerAwareCdiTestContainer
{
    public ServletOpenWebBeansTestContainer()
    {
        this.testContainer = CdiTestContainerLoader.getCdiContainer();
    }

    public void startSession()
    {
        try
        {
            this.testContainer.startSessionScope();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void startRequest()
    {
        try
        {
            this.testContainer.startRequestScope();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void stopRequest()
    {
        try
        {
            this.testContainer.stopRequestScope();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void stopSession()
    {
        try
        {
            this.testContainer.stopSessionScope();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
