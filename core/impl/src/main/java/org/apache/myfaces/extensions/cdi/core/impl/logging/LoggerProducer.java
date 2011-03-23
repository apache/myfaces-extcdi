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
package org.apache.myfaces.extensions.cdi.core.impl.logging;

import org.apache.myfaces.extensions.cdi.core.api.logging.LoggerDetails;
import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.Field;

/**
 * @author Gerhard Petracek
 * @author Werner Punz
 */
@ApplicationScoped
public class LoggerProducer
{
    @Produces
    @Dependent
    public Logger getLogger(InjectionPoint injectionPoint)
    {
        Bean<?> bean = injectionPoint.getBean();
        String name = null;

        if(bean != null)
        {
            name = bean.getBeanClass().getName();
        }
        //workaround for weld - only in some constellations
        else if(injectionPoint.getClass().getName().contains(".weld."))
        {
            name = tryToExtractName(injectionPoint);
        }

        if(name == null)
        {
            throw new IllegalStateException("InjectionPoint#getBean returns null");
        }
        return new DefaultLogger(name);
    }

    //workaround for weld
    private String tryToExtractName(InjectionPoint injectionPoint)
    {
        try
        {
            Object field1 = tryToGetFieldValue(injectionPoint, "field", Object.class);

            Field field2 = tryToGetFieldValue(field1, "field", Field.class);

            return field2.getDeclaringClass().getName();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Produces
    @Dependent
    public Logger.Factory getLoggerFactory(InjectionPoint injectionPoint)
    {
        return new DefaultLogger(injectionPoint.getBean().getBeanClass().getName()).getFactory();
    }

    @Produces
    @Dependent
    @LoggerDetails
    public Logger getLoggerForDetails(InjectionPoint injectionPoint)
    {
        LoggerDetails loggerDetails = injectionPoint.getAnnotated().getAnnotation(LoggerDetails.class);

        DefaultLogger logger = new DefaultLogger(loggerDetails.name(),
                                                 loggerDetails.resourceBundleName(),
                                                 loggerDetails.anonymous());

        if(!logger.isValid())
        {
            java.util.logging.Logger.getLogger(LoggerProducer.class.getName())
                    .warning("an injection point in " + injectionPoint.getBean().getBeanClass().getName() +
                             " uses an empty qualifier of type " + LoggerDetails.class.getName() +
                             " - please remove it!");
        }
        return logger;
    }

    //TODO
    public <T> T tryToGetFieldValue(Object object, String fieldName, Class<T> resultType)
    {
        Class currentClass = object.getClass();
        while (currentClass != null && !Object.class.getName().equals(currentClass.getName()))
        {
            for(Field currentField : currentClass.getDeclaredFields())
            {
                if(currentField.getName().equals(fieldName))
                {
                    boolean accessibleState = currentField.isAccessible();

                    try
                    {
                        currentField.setAccessible(true);
                        return (T)currentField.get(object);
                    }
                    catch (IllegalAccessException e)
                    {
                        return null;
                    }
                    finally
                    {
                        currentField.setAccessible(accessibleState);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return null;
    }
}
