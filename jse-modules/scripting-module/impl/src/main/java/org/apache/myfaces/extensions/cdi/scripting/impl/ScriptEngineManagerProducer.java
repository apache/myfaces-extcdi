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
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class ScriptEngineManagerProducer
{
    protected ScriptEngineManagerProducer()
    {
    }

    //this syntax is neede for using it via the expression language
    @Produces
    @Dependent
    @Named(SCRIPT_EXECUTOR_ALIAS)
    public Map<String, Object> createScriptExecutorBeanForAlias()
    {
        return createScriptExecutorBean();
    }

    @Produces
    @Dependent
    @Named(SCRIPT_EXECUTOR)
    //TODO add support for args
    public Map<String, Object> createScriptExecutorBean()
    {
        return createExpressionLanguageHelper();
    }

    private interface PlaceHolderLanguage extends Language{}

    @Produces
    @ScriptLanguage(PlaceHolderLanguage.class)
    @Deprecated
    public ScriptExecutor createScriptExecutor(InjectionPoint injectionPoint, LanguageManager languageManager)
    {
        ScriptEngine scriptEngine = createScriptEngineByLanguageName(injectionPoint, languageManager);

        return createScriptExecutor(scriptEngine);
    }

    private ScriptExecutor createScriptExecutor(ScriptEngine scriptEngine)
    {
        return new DefaultScriptExecutor(scriptEngine);
    }

    @Produces
    @ScriptLanguage(PlaceHolderLanguage.class)
    @Deprecated
    public ScriptBuilder createScriptBuilder(InjectionPoint injectionPoint, LanguageManager languageManager)
    {
        ScriptEngine scriptEngine = createScriptEngineByLanguageName(injectionPoint, languageManager);

        return createScriptBuilder(scriptEngine);
    }

    private ScriptBuilder createScriptBuilder(ScriptEngine scriptEngine)
    {
        return new DefaultScriptBuilder(scriptEngine);
    }

    @Produces
    @ScriptLanguage(PlaceHolderLanguage.class)
    public ScriptEngine createScriptEngineByLanguageName(InjectionPoint injectionPoint, LanguageManager languageManager)
    {
        Class<? extends Language> language =
                injectionPoint.getAnnotated().getAnnotation(ScriptLanguage.class).value();

        String languageName = languageManager.getLanguageName(language);

        return checkedScriptEngine(getCurrentScriptEngineManager().getEngineByName(languageName), languageName);
    }

    /*
    @Produces
    @ScriptExtension("")
    public ScriptEngine createScriptEngineByExtension(InjectionPoint injectionPoint,
                                                      ScriptEngineManager scriptEngineManager)
    {
        String extension = injectionPoint.getAnnotated().getAnnotation(ScriptExtension.class).value();

        return checkedScriptEngine(scriptEngineManager.getEngineByExtension(extension), extension);
    }

    @Produces
    @ScriptMimeType("")
    public ScriptEngine createScriptEngineByMimeType(InjectionPoint injectionPoint,
                                                     ScriptEngineManager scriptEngineManager)
    {
        String mimeType = injectionPoint.getAnnotated().getAnnotation(ScriptMimeType.class).value();

        return checkedScriptEngine(scriptEngineManager.getEngineByMimeType(mimeType), mimeType);
    }
    */

    private ScriptEngine checkedScriptEngine(ScriptEngine scriptEngine, String type)
    {
        if (scriptEngine != null)
        {
            return scriptEngine;
        }
        throw unknownScriptingLanguage(type);
    }
}
