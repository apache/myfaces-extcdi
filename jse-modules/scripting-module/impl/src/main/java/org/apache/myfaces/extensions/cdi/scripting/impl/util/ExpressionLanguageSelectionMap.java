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

import javax.enterprise.inject.Typed;

/**
 * Helper for using server-side scripting within EL-expressions
 */
@Typed()
class ExpressionLanguageSelectionMap extends UnmodifiableMap<String, Object>
{
    private static final long serialVersionUID = 393871900655666197L;

    @Override
    public Object get(Object key)
    {
        if(key == null)
        {
            return null;
        }

        String language;

        if(key instanceof String)
        {
            language = (String)key;
        }
        else
        {
            language = key.toString();
        }

        language = expandLanguageName(language);

        return new ScriptHelperMap(language);
    }

    private String expandLanguageName(String language)
    {
        //TODO
        return language;
    }
}
