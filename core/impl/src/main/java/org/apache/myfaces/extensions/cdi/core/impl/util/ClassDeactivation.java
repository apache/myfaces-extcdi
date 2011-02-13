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

import org.apache.myfaces.extensions.cdi.core.api.AbstractClassDeactivator;
import org.apache.myfaces.extensions.cdi.core.api.ClassDeactivator;

import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
public class ClassDeactivation
{
    private static Logger logger = Logger.getLogger(ClassDeactivation.class.getName());

    public static boolean isClassActivated(Class targetClass)
    {
        ClassDeactivator classDeactivator = ClassDeactivatorStorage.getClassDeactivator();

        if(classDeactivator == null)
        {
            classDeactivator = getClassDeactivator();
        }

        boolean classDeactivated = classDeactivator.getDeactivatedClasses().contains(targetClass);

        return !classDeactivated;
    }

    public static void setClassDeactivator(ClassDeactivator classDeactivator)
    {
        ClassDeactivatorStorage.setClassDeactivator(classDeactivator);
    }

    private static ClassDeactivator getClassDeactivator()
    {
        ClassDeactivator classDeactivator =
                CodiUtils.lookupFromEnvironment(ClassDeactivator.class, new ClassDeactivatorAggregator());

        // use default deactivator
        if (classDeactivator == null)
        {
            classDeactivator = createClassDeactivatorPlaceholder();
        }
        else
        {
            logger.info("used class deactivator: " + classDeactivator.toString());

            // display deactivated classes here once
            // NOTE that isClassActivated() will be called many times for the same class
            for (Class<?> deactivatedClass : classDeactivator.getDeactivatedClasses())
            {
                logger.info("deactivate: " + deactivatedClass);
            }
        }

        ClassDeactivatorStorage.setClassDeactivator(classDeactivator);
        return classDeactivator;
    }

    private static AbstractClassDeactivator createClassDeactivatorPlaceholder()
    {
        return new AbstractClassDeactivator()
        {
            private static final long serialVersionUID = 3365575383802245760L;

            protected void deactivateClasses()
            {
                //do nothing
            }
        };
    }
}
