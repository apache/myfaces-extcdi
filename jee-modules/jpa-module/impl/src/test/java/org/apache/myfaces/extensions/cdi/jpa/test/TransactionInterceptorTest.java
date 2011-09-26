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


import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.test.util.ContainerTestBase;
import org.apache.myfaces.extensions.cdi.jpa.api.TransactionHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import sun.rmi.rmic.iiop.Type;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.concurrent.Callable;


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

    @Test
    public void testTransactionScopedTransactionInterceptor()
    {
        TransactionScopedEmTestServiceImpl testService = getBeanInstance(TransactionScopedEmTestServiceImpl.class);
        Assert.assertNotNull(testService);

        Assert.assertEquals(testService.doThis(), "this");
        Assert.assertEquals(testService.doThat(), "that");

        try
        {
            EntityManager em = getBeanInstance(EntityManager.class, new AnnotationLiteral<TransactionScopeAware>(){});
            em.getTransaction();
            Assert.fail("ContextNotActiveException expected!");
        }
        catch(ContextNotActiveException cnae)
        {
            // all ok
        }

    }

    @Test
    public void testTransactionHelper() throws Exception
    {
        try
        {
            resolveEntityManager();
            Assert.fail("ContextNotActiveException expected!");
        }
        catch(ContextNotActiveException cnae)
        {
            // this was expected, all is fine!
        }

        Integer retVal = TransactionHelper.getInstance().executeTransactional( new Callable<Integer>() {

            public Integer call() throws Exception
            {
                resolveEntityManager();

                return Integer.valueOf(3);
            }
        });

        Assert.assertEquals(retVal, Integer.valueOf(3));


        try
        {
            resolveEntityManager();
            Assert.fail("ContextNotActiveException expected!");
        }
        catch(ContextNotActiveException cnae)
        {
            // this was expected, all is fine!
        }
    }

    private void resolveEntityManager()
    {
        EntityManager em = BeanManagerProvider.getInstance().
                getContextualReference(EntityManager.class,
                        new AnnotationLiteral<TransactionScopeAware>()
                                            {
                                            });
        Assert.assertNotNull(em);
        EntityTransaction et = em.getTransaction();
        Assert.assertNotNull(et);
    }
}
