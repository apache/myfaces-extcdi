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
package org.apache.myfaces.extensions.cdi.scripting.impl;

import org.apache.myfaces.extensions.cdi.scripting.api.language.Language;
import org.apache.myfaces.extensions.cdi.scripting.api.LanguageManager;
import org.apache.myfaces.extensions.cdi.scripting.impl.spi.LanguageBean;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ExceptionUtils.noScriptingLanguageAvailable;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ExceptionUtils.noScriptingLanguageAvailableFor;
import static org.apache.myfaces.extensions.cdi.scripting.impl.util.ExceptionUtils.ambiguousLanguageDefinition;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultLanguageManager implements LanguageManager
{
    private ConcurrentHashMap<Class<? extends Language>, Language> languageCache;

    protected DefaultLanguageManager()
    {
    }

    public String getLanguageName(Class<? extends Language> languageType)
    {
        Language language = this.languageCache.get(languageType);

        if(language == null)
        {
            throw noScriptingLanguageAvailableFor(languageType);
        }
        return language.getName();
    }

    @PostConstruct
    protected void init()
    {
        if(languageCache != null)
        {
            return;
        }

        languageCache = new ConcurrentHashMap<Class<? extends Language>, Language>();

        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

        Set<? extends Bean> foundBeans = beanManager.getBeans(LanguageBean.class);

        if (foundBeans.isEmpty())
        {
            throw noScriptingLanguageAvailable();
        }

        CreationalContext<LanguageBean> creationalContext;
        LanguageBean currentBean;
        for(Bean<LanguageBean> languageBean : foundBeans)
        {
            creationalContext = beanManager.createCreationalContext(languageBean);

            currentBean = languageBean.create(creationalContext);

            if(currentBean == null)
            {
                //TODO
                continue;
            }

            if(this.languageCache.containsKey(currentBean.getId()))
            {
                throw ambiguousLanguageDefinition(
                        currentBean.getId(),this.languageCache.get(currentBean.getId()), currentBean);
            }
            this.languageCache.put(currentBean.getId(), currentBean);
        }
    }
}
