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
package org.apache.myfaces.extensions.cdi.test.cargo.runner;

import org.apache.myfaces.extensions.cdi.test.cargo.ContainerNotStartedException;
import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
public class JUnit4WithCargo extends BlockJUnit4ClassRunner
{
    private static final Logger LOGGER = Logger.getLogger(JUnit4WithCargo.class.getName());

    public JUnit4WithCargo(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier)
    {
        Description description = describeChild(method);
        EachTestNotifier eachTestNotifier = new EachTestNotifier(notifier, description);

        if (method.getAnnotation(Ignore.class) != null)
        {
            eachTestNotifier.fireTestIgnored();
            return;
        }

        eachTestNotifier.fireTestStarted();

        try
        {
            methodBlock(method).evaluate();
        }
        catch (AssumptionViolatedException e)
        {
            eachTestNotifier.addFailedAssumption(e);
        }
        catch (ContainerNotStartedException e)
        {
            LOGGER.fine("In this step the container isn't started -> test result is ignored for now. " +
                    "Please make sure that the test gets executed in a later phase.");
        }
        catch (Throwable e)
        {
            eachTestNotifier.addFailure(e);
        }
        finally
        {
            eachTestNotifier.fireTestFinished();
        }
    }
}
