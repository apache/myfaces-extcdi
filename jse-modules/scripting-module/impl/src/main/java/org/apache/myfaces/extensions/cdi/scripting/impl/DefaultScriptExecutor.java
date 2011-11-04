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
import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import org.apache.myfaces.extensions.cdi.scripting.impl.spi.ExternalExpressionInterpreter;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils.resolveExternalExpressionInterpreter;
import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils;

import javax.enterprise.inject.Typed;
import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.ScriptEngine;
import java.util.Map;

/**
 * Simple script executor
 */
@Typed()
class DefaultScriptExecutor implements ScriptExecutor
{
    private static final long serialVersionUID = 1340953486786561148L;

    private transient ScriptEngine scriptEngine;
    private Class<? extends Language> language;

    /**
     * Constructor which creates the executor which is awae of the current script-engine
     * @param scriptEngine script-engine which should be used
     * @param language current language
     */
    DefaultScriptExecutor(ScriptEngine scriptEngine, Class<? extends Language> language)
    {
        this.scriptEngine = scriptEngine;
        this.language = language;
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(String script)
    {
        return eval(script, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(String script, Map<String, Object> arguments)
    {
        return eval(script, arguments, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(String script, Bindings bindings)
    {
        return eval(script, bindings, Object.class);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T eval(String script, Class<T> returnType)
    {
        try
        {
            script = interpreteScript(script);
            //noinspection unchecked
            return (T)getScriptEngine().eval(script);
        }
        catch (ScriptException e)
        {
            throw new UnhandledException(e);
        }
    }

    private ScriptEngine getScriptEngine()
    {
        if(this.scriptEngine == null)
        {
            this.scriptEngine = ScriptingUtils.createScriptEngine(this.language);
        }
        return this.scriptEngine;
    }

    /**
     * {@inheritDoc}
     */
    public <T> T eval(String script, Map<String, Object> arguments, Class<T> returnType)
    {
        return eval(script, new SimpleBindings(arguments), returnType);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T eval(String script, Bindings bindings, Class<T> returnType)
    {
        try
        {
            script = interpreteScript(script);
            //noinspection unchecked
            return (T)getScriptEngine().eval(script, bindings);
        }
        catch (ScriptException e)
        {
            throw new UnhandledException(e);
        }
    }

    private String interpreteScript(String script)
    {
        ExternalExpressionInterpreter externalExpressionInterpreter = resolveExternalExpressionInterpreter();
        return externalExpressionInterpreter.transform(script);
    }
}
