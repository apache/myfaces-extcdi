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
package org.apache.myfaces.extensions.cdi.scripting.impl;

import org.apache.myfaces.extensions.cdi.scripting.api.ScriptExecutor;
import org.apache.myfaces.extensions.cdi.scripting.impl.spi.ExternalExpressionInterpreter;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils.resolveExternalExpressionInterpreter;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.ScriptEngine;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class DefaultScriptExecutor implements ScriptExecutor
{
    private ScriptEngine scriptEngine;

    public DefaultScriptExecutor(ScriptEngine scriptEngine)
    {
        this.scriptEngine = scriptEngine;
    }

    public Object eval(String script)
    {
        return eval(script, Object.class);
    }

    public Object eval(String script, Map<String, Object> arguments)
    {
        return eval(script, arguments, Object.class);
    }

    public Object eval(String script, Bindings bindings)
    {
        return eval(script, bindings, Object.class);
    }

    public <T> T eval(String script, Class<T> returnType)
    {
        try
        {
            script = interpreteScript(script);
            return (T)scriptEngine.eval(script);
        }
        catch (ScriptException e)
        {
            throw new RuntimeException(e);
        }
    }

    public <T> T eval(String script, Map<String, Object> arguments, Class<T> returnType)
    {
        return eval(script, new SimpleBindings(arguments), returnType);
    }

    public <T> T eval(String script, Bindings bindings, Class<T> returnType)
    {
        try
        {
            script = interpreteScript(script);
            return (T)scriptEngine.eval(script, bindings);
        }
        catch (ScriptException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String interpreteScript(String script)
    {
        ExternalExpressionInterpreter externalExpressionInterpreter = resolveExternalExpressionInterpreter();
        return externalExpressionInterpreter.transform(script);
    }
}
