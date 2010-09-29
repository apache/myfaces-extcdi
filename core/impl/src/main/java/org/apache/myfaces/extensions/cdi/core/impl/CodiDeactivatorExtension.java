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
package org.apache.myfaces.extensions.cdi.core.impl;

import static org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils.isClassActivated;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.interceptor.Interceptor;

/**
 * Allows the deactivation of interceptors
 * 
 * @author Gerhard Petracek
 */
public class CodiDeactivatorExtension implements Extension
{
    public void filterInterceptors(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isClassActivated(getClass()))
        {
            return;
        }

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(Interceptor.class))
        {
            if(!isClassActivated(processAnnotatedType.getAnnotatedType().getJavaClass()))
            {
                processAnnotatedType.veto();
            }
        }
    }
}
