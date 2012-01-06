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
package org.apache.myfaces.extensions.cdi.core.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.activation.ExpressionActivated;
import org.apache.myfaces.extensions.cdi.core.api.interpreter.ExpressionInterpreter;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.inject.Typed;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper methods for handling {@link ExpressionActivated}
 */
@Typed()
public class ActivationUtils
{
    private ActivationUtils()
    {
        // prevent instantiation
    }

    public static boolean isActivated(Class<?> annotatedClass, Class defaultExpressionInterpreterClass)
    {
        ExpressionActivated expressionActivated = annotatedClass.getAnnotation(ExpressionActivated.class);

        if (expressionActivated == null)
        {
            return true;
        }

        String expressions = expressionActivated.value();

        Class<? extends ExpressionInterpreter> interpreterClass = expressionActivated.interpreter();

        if(interpreterClass.equals(ExpressionInterpreter.class))
        {
            interpreterClass = defaultExpressionInterpreterClass;
        }

        ExpressionInterpreter<String, Boolean> expressionInterpreter =
                ClassUtils.tryToInstantiateClass(interpreterClass);

        if(expressionInterpreter == null)
        {
            Logger logger = Logger.getLogger(ActivationUtils.class.getName());

            if(logger.isLoggable(Level.WARNING))
            {
                logger.warning("can't instantiate " + interpreterClass.getClass().getName());
            }
            return true;
        }

        expressions = "configName:" + expressionActivated.configName() + ";" + expressions;
        return expressionInterpreter.evaluate(expressions);
    }
}
