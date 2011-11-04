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

import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils;

import javax.enterprise.inject.Typed;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.Reader;

/**
 * Serializable {@link ScriptEngine}
 */
@Typed()
class InjectableScriptEngine implements ScriptEngine
{
    private transient ScriptEngine wrapped;
    private Class<? extends Language> language;

    InjectableScriptEngine(ScriptEngine wrapped, Class<? extends Language> language)
    {
        this.wrapped = wrapped;
        this.language = language;
    }

    private ScriptEngine getScriptEngine()
    {
        if(this.wrapped == null)
        {
            this.wrapped = ScriptingUtils.createScriptEngine(this.language);
        }
        return this.wrapped;
    }

    /*
     * generated
     */

    /**
     * {@inheritDoc}
     */
    public Object eval(String script, ScriptContext context) throws ScriptException
    {
        return getScriptEngine().eval(script, context);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(Reader reader, ScriptContext context) throws ScriptException
    {
        return getScriptEngine().eval(reader, context);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(String script) throws ScriptException
    {
        return getScriptEngine().eval(script);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(Reader reader) throws ScriptException
    {
        return getScriptEngine().eval(reader);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(String script, Bindings n) throws ScriptException
    {
        return getScriptEngine().eval(script, n);
    }

    /**
     * {@inheritDoc}
     */
    public Object eval(Reader reader, Bindings n) throws ScriptException
    {
        return getScriptEngine().eval(reader, n);
    }

    /**
     * {@inheritDoc}
     */
    public void put(String key, Object value)
    {
        getScriptEngine().put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(String key)
    {
        return getScriptEngine().get(key);
    }

    /**
     * {@inheritDoc}
     */
    public Bindings getBindings(int scope)
    {
        return getScriptEngine().getBindings(scope);
    }

    /**
     * {@inheritDoc}
     */
    public void setBindings(Bindings bindings, int scope)
    {
        getScriptEngine().setBindings(bindings, scope);
    }

    /**
     * {@inheritDoc}
     */
    public Bindings createBindings()
    {
        return getScriptEngine().createBindings();
    }

    /**
     * {@inheritDoc}
     */
    public ScriptContext getContext()
    {
        return getScriptEngine().getContext();
    }

    /**
     * {@inheritDoc}
     */
    public void setContext(ScriptContext context)
    {
        getScriptEngine().setContext(context);
    }

    /**
     * {@inheritDoc}
     */
    public ScriptEngineFactory getFactory()
    {
        return getScriptEngine().getFactory();
    }
}
