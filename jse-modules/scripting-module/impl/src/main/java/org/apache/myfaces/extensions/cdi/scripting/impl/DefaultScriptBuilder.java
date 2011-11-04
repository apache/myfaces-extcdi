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

import org.apache.myfaces.extensions.cdi.scripting.api.ScriptBuilder;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ExceptionUtils.overrideBuilderState;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils.resolveExternalExpressionInterpreter;

import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import org.apache.myfaces.extensions.cdi.scripting.impl.spi.ExternalExpressionInterpreter;
import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import javax.script.ScriptException;
import javax.script.ScriptEngine;
import java.util.Map;
import java.util.HashMap;

/**
 * Simple script builder
 */
class DefaultScriptBuilder implements ScriptBuilder
{
    private static final long serialVersionUID = -7746031361776472273L;

    private transient ScriptEngine scriptEngine;
    private Class<? extends Language> language;
    private transient Map<String, Object> arguments;
    private transient String script;
    private transient Bindings bindings;

    private DefaultScriptBuilder()
    {
    }

    DefaultScriptBuilder(ScriptEngine scriptEngine, Class<? extends Language> language)
    {
        this.scriptEngine = scriptEngine;
        this.language = language;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptBuilder script(String script)
    {
        DefaultScriptBuilder newScriptBuilder = new DefaultScriptBuilder(getScriptEngine(), this.language);
        newScriptBuilder.script = script;
        return newScriptBuilder;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptBuilder namedArgument(String name, Object value)
    {
        if(this.bindings != null)
        {
            throw overrideBuilderState("(named) argument/s");
        }

        if(this.arguments == null)
        {
            this.arguments = new HashMap<String, Object>();
        }
        this.arguments.put(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptBuilder bindings(Bindings bindings)
    {
        if(this.arguments != null)
        {
            throw overrideBuilderState("bindings");
        }

        this.bindings = bindings;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Object eval()
    {
        return eval(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T eval(Class<T> returnType)
    {
        try
        {
            this.script = interpreteScript(this.script);

            if(this.bindings == null && this.arguments == null)
            {
                return (T)getScriptEngine().eval(this.script);
            }

            Bindings scriptBindings = this.bindings;

            if(scriptBindings == null)
            {
                scriptBindings = new SimpleBindings(this.arguments);
            }

            return (T)getScriptEngine().eval(this.script, scriptBindings);
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

    private ScriptEngine getScriptEngine()
    {
        if(this.scriptEngine == null)
        {
            this.scriptEngine = ScriptingUtils.createScriptEngine(this.language);
        }
        return this.scriptEngine;
    }
}
