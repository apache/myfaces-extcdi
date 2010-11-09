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
package org.apache.myfaces.extensions.cdi.message.test;

import de.odysseus.el.util.SimpleContext;
import org.apache.myfaces.extensions.cdi.message.impl.spi.SimpleELContext;

import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
class TestELContext extends SimpleELContext
{
    private SimpleContext simpleContext;

    public TestELContext(ELResolver elResolver)
    {
        this.simpleContext = new SimpleContext(elResolver);
    }

    /*
     * generated
     */
    public ValueExpression setVariable(String s, ValueExpression valueExpression)
    {
        return simpleContext.setVariable(s, valueExpression);
    }

    public FunctionMapper getFunctionMapper()
    {
        return simpleContext.getFunctionMapper();
    }

    public VariableMapper getVariableMapper()
    {
        return simpleContext.getVariableMapper();
    }

    public ELResolver getELResolver()
    {
        return simpleContext.getELResolver();
    }
}
