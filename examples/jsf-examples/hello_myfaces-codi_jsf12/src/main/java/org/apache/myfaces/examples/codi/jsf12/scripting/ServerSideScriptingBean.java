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
package org.apache.myfaces.examples.codi.jsf12.scripting;

import org.apache.myfaces.extensions.cdi.scripting.api.ScriptLanguage;
import org.apache.myfaces.extensions.cdi.scripting.api.ScriptExecutor;
import org.apache.myfaces.extensions.cdi.scripting.api.language.JavaScript;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.annotation.PostConstruct;

/**
 * @author Gerhard Petracek
 */
@Named
@RequestScoped
public class ServerSideScriptingBean
{
    @Inject
    @ScriptLanguage(JavaScript.class)
    private ScriptExecutor scriptExecutor;

    //or manually
    @Inject
    @ScriptLanguage(JavaScript.class)
    private ScriptEngine scriptEngine;

    Double result1;
    Double result2;

    @PostConstruct
    protected void init()
    {
        result1 = this.scriptExecutor.eval("10 + 4", Double.class);

        //or manually
        try
        {
            result2 = (Double)this.scriptEngine.eval("3 + 4");
        }
        catch (ScriptException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Double getResult1()
    {
        return result1;
    }

    public Double getResult2()
    {
        return result2;
    }
}
