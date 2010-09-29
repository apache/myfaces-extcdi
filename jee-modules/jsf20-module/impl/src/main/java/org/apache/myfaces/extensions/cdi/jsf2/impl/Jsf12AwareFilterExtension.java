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
package org.apache.myfaces.extensions.cdi.jsf2.impl;

import org.apache.myfaces.extensions.cdi.jsf.impl.request.DefaultRequestTypeResolver;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.DefaultWindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;

import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.event.Observes;

/**
 * filters implementations for jsf 1.2
 * this module contains all replacements for jsf 2.0
 * 
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Jsf12AwareFilterExtension implements Extension, Deactivatable
{
    public void filterJsfPhaseListeners(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        Class beanClass = processAnnotatedType.getAnnotatedType().getJavaClass();
        if(DefaultRequestTypeResolver.class.isAssignableFrom(beanClass))
        {
            //veto the RequestTypeResolver for jsf 1.2
            processAnnotatedType.veto();
        }
        else if(DefaultWindowContextConfig.class.getName().equals(beanClass.getName()))
        {
            //veto the config for jsf 1.2
            processAnnotatedType.veto();
        }
    }

    public boolean isActivated()
    {
        return ClassUtils.isClassActivated(getClass());
    }
}
