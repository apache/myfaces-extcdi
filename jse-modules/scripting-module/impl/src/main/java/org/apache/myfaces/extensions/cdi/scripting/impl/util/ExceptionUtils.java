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

/**
 * @author Gerhard Petracek
 */
public class ExceptionUtils
{
    public static RuntimeException unknownScriptingLanguage(String name)
    {
        return new RuntimeException("No scripting engine found for: " + name);
    }

    public static RuntimeException noScriptingLanguageAvailable()
    {
        return new RuntimeException("No bean of type " + LanguageBean.class.getName() + " found!");
    }

    public static RuntimeException noScriptingLanguageAvailableFor(Class<? extends Language> languageType)
    {
        return new RuntimeException("No language is mapped to " + languageType.getName());
    }

    public static RuntimeException ambiguousLanguageDefinition(
            Class<? extends Language> id, Language foundLanguage, Language newLanguage)
    {
        return new RuntimeException("Invalid approach detected to replace " + id.getName() + ". Can't replace " +
            foundLanguage.getClass().getName() + " with " + newLanguage.getClass().getName());
    }

    public static RuntimeException overrideBuilderState(String newValueHint)
    {
        return new RuntimeException("Invalid script builder (" + ScriptBuilder.class.getName() + ") state. " +
            "It isn't allowed to override the existing state of the builder with new " + newValueHint);
    }
}
