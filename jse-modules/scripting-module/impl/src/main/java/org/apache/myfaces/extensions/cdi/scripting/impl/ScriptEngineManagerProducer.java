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

import org.apache.myfaces.extensions.cdi.scripting.api.ScriptLanguage;
import org.apache.myfaces.extensions.cdi.scripting.api.LanguageManager;
import org.apache.myfaces.extensions.cdi.scripting.api.ScriptExecutor;
import org.apache.myfaces.extensions.cdi.scripting.api.ScriptBuilder;
import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import static org.apache.myfaces.extensions.cdi.scripting.api.ScriptingModuleBeanNames.*;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ExceptionUtils.unknownScriptingLanguage;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ScriptingUtils.*;

import javax.script.ScriptEngine;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Map;

/**
 * Producer for scripting artifacts
 */
//TODO producers have to create serializable instances

@ApplicationScoped
public class ScriptEngineManagerProducer
{
    protected ScriptEngineManagerProducer()
    {
    }

    //this syntax is neede for using it via the expression language

    /**
     * Creates an alias for #createScriptExecutorBean
     * which creates a helper which allows to use a {@link ScriptExecutor} within el-expressions
     * @return el-helper for the script-executor
     */
    @Produces
    @Dependent
    @Named(SCRIPT_EXECUTOR_ALIAS)
    public Map<String, Object> createScriptExecutorBeanForAlias()
    {
        return createScriptExecutorBean();
    }

    /**
     * Creates a helper which allows to use a {@link ScriptExecutor} within el-expressions
     * @return el-helper for the script-executor
     */
    @Produces
    @Dependent
    @Named(SCRIPT_EXECUTOR)
    //TODO add support for args
    public Map<String, Object> createScriptExecutorBean()
    {
        return createExpressionLanguageHelper();
    }

    private interface PlaceHolderLanguage extends Language{}

    /**
     * Creates a {@link ScriptExecutor} for the specified language
     * @param injectionPoint target injection-point
     * @param languageManager current language-manager
     * @return script-executor for the language specified at the given injection-point
     */
    @Produces
    @ScriptLanguage(PlaceHolderLanguage.class)
    @Dependent
    public ScriptExecutor createScriptExecutor(InjectionPoint injectionPoint, LanguageManager languageManager)
    {
        ScriptEngine scriptEngine = createScriptEngineByLanguageName(injectionPoint, languageManager);

        return createScriptExecutorForScriptEngine(scriptEngine, injectionPoint);
    }

    private ScriptExecutor createScriptExecutorForScriptEngine(ScriptEngine scriptEngine, InjectionPoint injectionPoint)
    {
        Class<? extends Language> language =
                injectionPoint.getAnnotated().getAnnotation(ScriptLanguage.class).value();

        return new DefaultScriptExecutor(scriptEngine, language);
    }

    /**
     * Creates a {@link ScriptBuilder} for the specified language
     * @param injectionPoint target injection-point
     * @param languageManager current language-manager
     * @return script-builder for the language specified at the given injection-point
     */
    @Produces
    @ScriptLanguage(PlaceHolderLanguage.class)
    @Dependent
    public ScriptBuilder createScriptBuilder(InjectionPoint injectionPoint, LanguageManager languageManager)
    {
        ScriptEngine scriptEngine = createScriptEngineByLanguageName(injectionPoint, languageManager);

        return createScriptBuilderForEngine(scriptEngine, injectionPoint);
    }

    private ScriptBuilder createScriptBuilderForEngine(ScriptEngine scriptEngine, InjectionPoint injectionPoint)
    {
        Class<? extends Language> language =
                injectionPoint.getAnnotated().getAnnotation(ScriptLanguage.class).value();

        return new DefaultScriptBuilder(scriptEngine, language);
    }

    /**
     * Creates a {@link ScriptEngine} for the specified language
     * @param injectionPoint target injection-point
     * @param languageManager current language-manager
     * @return script-engine for the language specified at the given injection-point
     */
    @Produces
    @ScriptLanguage(PlaceHolderLanguage.class)
    @Dependent
    public InjectableScriptEngine createScriptEngineByLanguageName(InjectionPoint injectionPoint,
                                                                   LanguageManager languageManager)
    {
        Class<? extends Language> language =
                injectionPoint.getAnnotated().getAnnotation(ScriptLanguage.class).value();

        String languageName = languageManager.getLanguageName(language);

        ScriptEngine result = checkedScriptEngine(
                getCurrentScriptEngineManager().getEngineByName(languageName), languageName);

        return new InjectableScriptEngine(result, language);
    }

    private ScriptEngine checkedScriptEngine(ScriptEngine scriptEngine, String type)
    {
        if (scriptEngine != null)
        {
            return scriptEngine;
        }
        throw unknownScriptingLanguage(type);
    }
}
