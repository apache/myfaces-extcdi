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

import org.apache.myfaces.extensions.cdi.scripting.impl.spi.LanguageBean;
import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import org.apache.myfaces.extensions.cdi.scripting.api.ScriptBuilder;
import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;

import javax.enterprise.inject.Typed;

/**
 * @author Gerhard Petracek
 */
@Typed()
public abstract class ExceptionUtils
{
    private ExceptionUtils()
    {
        // prevent instantiation
    }

    /**
     * Creates an exception if the script-language isn't known
     * @param name unknown name
     * @return exception which can be thrown
     */
    public static RuntimeException unknownScriptingLanguage(String name)
    {
        return new UnhandledException("No scripting engine found for: " + name);
    }

    /**
     * Creates an exception if there is no script-language type available
     * @return exception which can be thrown
     */
    public static RuntimeException noScriptingLanguageAvailable()
    {
        return new UnhandledException("No bean of type " + LanguageBean.class.getName() + " found!");
    }

    /**
     * Creates an exception if the type of the script-language isn't known
     * @param languageType unknown language type
     * @return exception which can be thrown
     */
    public static RuntimeException noScriptingLanguageAvailableFor(Class<? extends Language> languageType)
    {
        return new UnhandledException("No language is mapped to " + languageType.getName());
    }

    /**
     * Creates an exception if there are multiple language types
     * @param id language id
     * @param foundLanguage registered language
     * @param newLanguage found language
     * @return exception which can be thrown
     */
    public static RuntimeException ambiguousLanguageDefinition(
            Class<? extends Language> id, Language foundLanguage, Language newLanguage)
    {
        return new UnhandledException("Invalid approach detected to replace " + id.getName() + ". Can't replace " +
            foundLanguage.getClass().getName() + " with " + newLanguage.getClass().getName());
    }

    /**
     * Creates an exception if e.g. bindings would be overruled
     * @param newValueHint name of the argument
     * @return exception which can be thrown
     */
    public static RuntimeException overrideBuilderState(String newValueHint)
    {
        return new UnhandledException("Invalid script builder (" + ScriptBuilder.class.getName() + ") state. " +
            "It isn't allowed to override the existing state of the builder with new " + newValueHint);
    }
}
