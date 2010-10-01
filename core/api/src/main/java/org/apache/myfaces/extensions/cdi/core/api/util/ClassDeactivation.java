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
package org.apache.myfaces.extensions.cdi.core.api.util;

import java.util.logging.Logger;
import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.tryToInstantiateClassForName;
import org.apache.myfaces.extensions.cdi.core.api.ClassDeactivator;
import org.apache.myfaces.extensions.cdi.core.api.AbstractClassDeactivator;

/**
 * @author Gerhard Petracek
 */
public class ClassDeactivation
{
    private static Logger logger = Logger.getLogger(ClassDeactivation.class.getName());

    private static final String DEFAULT_CLASS_DEACTIVATOR = "org.apache.myfaces.extensions.cdi.ClassDeactivator";

    public static boolean isClassActivated(Class targetClass)
    {
        ClassDeactivator classDeactivator = ClassDeactivatorStorage.getClassDeactivator();

        if(classDeactivator == null)
        {
            classDeactivator = addClassDeactivator();
        }

        boolean classDeactivated = classDeactivator.getDeactivatedClasses().contains(targetClass);

        if(classDeactivated)
        {
            logger.info("deactivate: " + targetClass);
        }

        return !classDeactivated;
    }

    public static void setClassDeactivator(ClassDeactivator classDeactivator)
    {
        ClassDeactivatorStorage.setClassDeactivator(classDeactivator);
    }

    private static ClassDeactivator addClassDeactivator()
    {
        ClassDeactivator classDeactivator = tryToInstantiateClassForName(
                DEFAULT_CLASS_DEACTIVATOR , ClassDeactivator.class);

        //overrule with vm param
        String classDeactivatorName = System.getProperty(DEFAULT_CLASS_DEACTIVATOR);

        if (classDeactivatorName != null)
        {
            classDeactivator = tryToInstantiateClassForName(classDeactivatorName, ClassDeactivator.class);
        }

        if(classDeactivator == null)
        {
            classDeactivator = createClassDeactivatorPlaceholder();
        }
        else
        {
            logger.info("used class deactivator: " + classDeactivator.getClass().getName());
        }

        ClassDeactivatorStorage.setClassDeactivator(classDeactivator);
        return classDeactivator;
    }

    private static AbstractClassDeactivator createClassDeactivatorPlaceholder()
    {
        return new AbstractClassDeactivator()
        {
            protected void deactivateClasses()
            {
                //do nothing
            }
        };
    }
}
