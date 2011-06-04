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
package org.apache.myfaces.extensions.cdi.scripting.test.impl;

import org.apache.myfaces.extensions.cdi.scripting.api.ScriptBuilder;
import org.apache.myfaces.extensions.cdi.scripting.api.ScriptExecutor;
import org.apache.myfaces.extensions.cdi.scripting.api.ScriptLanguage;
import org.apache.myfaces.extensions.cdi.scripting.api.language.JavaScript;
import org.apache.myfaces.extensions.cdi.test.junit4.AbstractCdiAwareTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Gerhard Petracek
 */
@RunWith(JUnit4.class)
public class SimpleScriptingTest extends AbstractCdiAwareTest
{
    @Inject
    @ScriptLanguage(JavaScript.class)
    private ScriptExecutor scriptExecutor;

    @Inject
    @ScriptLanguage(JavaScript.class)
    private ScriptBuilder scriptBuilder;

    @Inject
    @ScriptLanguage(JavaScript.class)
    private ScriptEngine scriptEngine;

    @Test
    public void testSimpleScriptEval()
    {
        assertEquals(new Double(14), this.scriptExecutor.eval("10 + 4", Double.class));
    }

    @Test
    public void testScriptBuilder()
    {
        assertEquals(new Double(14), add(10d, 4d));
    }

    private Double add(Double a, Double b)
    {
       return this.scriptBuilder
                .script("x + y")
                .namedArgument("x", a)
                .namedArgument("y", b)
                .eval(Double.class);
    }

    @Test
    public void testScriptEngine()
    {
        try
        {
            assertEquals(14d, this.scriptEngine.eval("10 + 4"));
        }
        catch (ScriptException e)
        {
            fail();
        }
    }
}
