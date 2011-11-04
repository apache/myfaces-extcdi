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
package org.apache.myfaces.examples.codi.jsf12.scripting;

import org.apache.myfaces.extensions.cdi.scripting.impl.spi.ExternalExpressionInterpreter;
import static org.apache.myfaces.extensions.validator.util.ExtValUtils.getELHelper;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * very simple impl. just for demo cases!
 * currently we don't have a default impl. out-of-the-box due to the handling of null values...
 */
@ApplicationScoped
public class ExpressionLanguageAwareInterpreter implements ExternalExpressionInterpreter
{
    public String transform(String sourceScript)
    {
        Map<String, String> evaluatedExpressions = evalExpressions(sourceScript);

        return replaceExpressions(sourceScript, evaluatedExpressions);
    }

    private static final Pattern MESSAGE_ARGS_PATTERN = Pattern.compile("#\\{([^\\}]+?)\\}");

    private Map<String, String> evalExpressions(String script)
    {
        Map<String, String> result = new HashMap<String, String>();

        Matcher matcher = MESSAGE_ARGS_PATTERN.matcher(script);
        FacesContext facesContext = FacesContext.getCurrentInstance();

        String expression;
        Object value;
        while (matcher.find())
        {
            expression = matcher.group();
            value = getELHelper().getValueOfExpression(facesContext, new ValueBindingExpression(expression));

            if(value == null)
            {
                value = "null";
            }
            result.put(expression, value.toString());
        }

        return result;
    }

    private String replaceExpressions(String sourceScript, Map<String, String> evaluatedExpressions)
    {
        for(Map.Entry<String, String> entry : evaluatedExpressions.entrySet())
        {
            sourceScript = sourceScript.replace(entry.getKey(), entry.getValue());
        }

        return sourceScript;
    }
}
