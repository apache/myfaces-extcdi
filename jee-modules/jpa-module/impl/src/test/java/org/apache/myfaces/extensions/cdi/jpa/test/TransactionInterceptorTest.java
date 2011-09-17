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
package org.apache.myfaces.extensions.cdi.jpa.test;


import org.apache.myfaces.extensions.cdi.core.test.util.ContainerTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


/**
 * Test for &#064;Transactional interceptor
 */
public class TransactionInterceptorTest extends ContainerTestBase
{
    @Test
    public void testRequestScopedTransactionInterceptor()
    {
        RequestScopedEmTestServiceImpl testService = getBeanInstance(RequestScopedEmTestServiceImpl.class);
        Assert.assertNotNull(testService);

        Assert.assertEquals(testService.doThis(), "this");
        Assert.assertEquals(testService.doThat(), "that");

        EntityManager em = getBeanInstance(EntityManager.class);
        Assert.assertNotNull(em);
        EntityTransaction trans = em.getTransaction();
        Assert.assertNotNull(trans);
    }

    @Test(enabled = false)
    public void testTransactionScopedTransactionInterceptor()
    {
        TransactionScopedEmTestServiceImpl testService = getBeanInstance(TransactionScopedEmTestServiceImpl.class);
        Assert.assertNotNull(testService);

        Assert.assertEquals(testService.doThis(), "this");
        Assert.assertEquals(testService.doThat(), "that");

        try
        {
            EntityManager em = getBeanInstance(EntityManager.class, new AnnotationLiteral<TransactionScopeAware>(){});
            Assert.fail("ContextNotActiveException expected!");
        }
        catch(ContextNotActiveException cnae)
        {
            // all ok
        }

    }
}
