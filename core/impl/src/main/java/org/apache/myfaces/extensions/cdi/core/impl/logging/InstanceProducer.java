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

import org.apache.myfaces.extensions.cdi.core.api.Name;
import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * @author Gerhard Petracek
 * @author Werner Punz
 */
final class InstanceProducer
{
    @Produces
    public Logger getLogger(InjectionPoint injectionPoint)
    {
        return new DefaultLogger(injectionPoint.getBean().getBeanClass().getName());
    }

    @Produces
    @Name("")
    public Logger getLoggerWithName(InjectionPoint injectionPoint)
    {
        Name nameQualifier = injectionPoint.getAnnotated().getAnnotation(Name.class);

        return new DefaultLogger(nameQualifier.value());
    }
}
