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

import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
class ArgumentAwareScriptHelperMap extends ScriptHelperMap
{
    private static final long serialVersionUID = 393871900655666197L;
    private String language;
    private Map<String, Object> arguments = new HashMap<String, Object>();

    public ArgumentAwareScriptHelperMap(String language, String arguments)
    {
        this.language = language;

        parseArguments(arguments);
    }

    @Override
    public Object get(Object key)
    {
        String script;

        if(key instanceof String)
        {
            script = (String)key;
        }
        else
        {
            script = key.toString();
        }

        return evalScript(this.language, script, this.arguments);
    }

    private void parseArguments(String arguments)
    {
        arguments = arguments.substring(1, arguments.length() - 1);

        String[] argumentArray = arguments.split(",");

        String key;
        String value;

        for(String currentArgument : argumentArray)
        {
            key = currentArgument.substring(0, currentArgument.indexOf(':'));
            value = currentArgument.substring(currentArgument.indexOf(':') + 1);

            //TODO restrict arg overriding
            //TODO eval is specific to js -> we need an spi for it
            //TODO add pluggable support for value-bindings
            this.arguments.put(key, evalScript(this.language, "eval('" + value + "')", null));
        }
    }
}