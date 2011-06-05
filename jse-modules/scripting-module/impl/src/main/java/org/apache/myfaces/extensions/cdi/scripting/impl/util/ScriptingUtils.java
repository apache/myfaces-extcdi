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

import org.apache.myfaces.extensions.cdi.core.impl.util.UnmodifiableMap;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.scripting.api.LanguageManager;
import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import org.apache.myfaces.extensions.cdi.scripting.impl.spi.ExternalExpressionInterpreter;

import javax.enterprise.inject.Typed;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Gerhard Petracek
 */
@Typed()
public abstract class ScriptingUtils
{
    private ScriptingUtils()
    {
    }

    private static ThreadLocal<ScriptEngineManager> scriptEngineManagerCache = new ThreadLocal<ScriptEngineManager>();

    /**
     * Resolves the current {@link ScriptEngineManager}
     * @return the current script-engine-manager
     */
    public static ScriptEngineManager getCurrentScriptEngineManager()
    {
        ScriptEngineManager scriptEngineManager = scriptEngineManagerCache.get();

        if(scriptEngineManager == null)
        {
            scriptEngineManager = new ScriptEngineManager();
            scriptEngineManagerCache.set(scriptEngineManager);
        }

        return scriptEngineManager;
    }

    /**
     * Creates a new el helper map
     * @return a new el helper
     */
    public static UnmodifiableMap<String, Object> createExpressionLanguageHelper()
    {
        return new ExpressionLanguageSelectionMap();
    }

    /**
     * Resolves an {@link ExternalExpressionInterpreter}
     * @return a scoped custom bean or a new instance of the default implementation
     */
    public static ExternalExpressionInterpreter resolveExternalExpressionInterpreter()
    {
        ExternalExpressionInterpreter externalExpressionInterpreter =
                CodiUtils.getContextualReferenceByClass(ExternalExpressionInterpreter.class, true);

        return externalExpressionInterpreter != null ?
                externalExpressionInterpreter :
                new DefaultExternalExpressionInterpreter();
    }


    /**
     * Resolves the {@link ScriptEngine} based on the given language definition
     * @param language current language
     * @return script-engine for the given language
     */
    public static ScriptEngine createScriptEngine(Class<? extends Language> language)
    {
        LanguageManager languageManager = CodiUtils.getContextualReferenceByClass(LanguageManager.class);

        String languageName = languageManager.getLanguageName(language);
        return getCurrentScriptEngineManager().getEngineByName(languageName);
    }
}
