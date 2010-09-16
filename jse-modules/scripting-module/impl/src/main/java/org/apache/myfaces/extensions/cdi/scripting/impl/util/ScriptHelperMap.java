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
package org.apache.myfaces.extensions.cdi.scripting.impl.util;

import org.apache.myfaces.extensions.cdi.core.impl.utils.UnmodifiableMap;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils.getCurrentScriptEngineManager;

import javax.script.ScriptException;
import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
class ScriptHelperMap extends UnmodifiableMap<String, Object>
{
    private static final long serialVersionUID = 393871900655666197L;
    private String language;

    protected ScriptHelperMap()
    {
    }

    ScriptHelperMap(String language)
    {
        this.language = language;
    }

    @Override
    public Object get(Object key)
    {
        String argumentsOrScript;

        if(key instanceof String)
        {
            argumentsOrScript = (String)key;
        }
        else
        {
            argumentsOrScript = key.toString();
        }

        if(argumentsOrScript.startsWith("[") && argumentsOrScript.endsWith("]"))
        {
            return new ArgumentAwareScriptHelperMap(this.language, argumentsOrScript);
        }

        return evalScript(this.language, argumentsOrScript, null);
    }

    protected Object evalScript(String language, String script, Map<String, Object> arguments)
    {
        try
        {
            Bindings bindings = null;
            if(arguments != null)
            {
                bindings = new SimpleBindings(arguments);
            }

            if(bindings != null)
            {
                return getCurrentScriptEngineManager().getEngineByName(language).eval(script, bindings);
            }
            return getCurrentScriptEngineManager().getEngineByName(language).eval(script);
        }
        catch (ScriptException e)
        {
            throw new RuntimeException(e);
        }
    }
}