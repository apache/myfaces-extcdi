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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.NavigationMode;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBean;

import javax.inject.Named;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.beans.Introspector;

/**
 * @author Gerhard Petracek
 */
public class ViewConfigEntry
{
    private final String viewId;
    private final Class<? extends ViewConfig> viewDefinitionClass;
    private final NavigationMode navigationMode;
    private final List<PageBeanConfigEntry> beanDefinition;

    public ViewConfigEntry(String viewId,
                               Class<? extends ViewConfig> viewDefinitionClass,
                               NavigationMode navigationMode)
    {
        this.viewId = viewId;
        this.viewDefinitionClass = viewDefinitionClass;
        this.navigationMode = navigationMode;

        beanDefinition = Collections.unmodifiableList(findPageBeanDefinitions(viewDefinitionClass));
        //TODO validate view-id
    }

    public String getViewId()
    {
        return viewId;
    }

    public Class<? extends ViewConfig> getViewDefinitionClass()
    {
        return viewDefinitionClass;
    }

    public NavigationMode getNavigationMode()
    {
        return navigationMode;
    }

    List<PageBeanConfigEntry> getBeanDefinitions()
    {
        return beanDefinition;
    }

    private List<PageBeanConfigEntry> findPageBeanDefinitions(Class<? extends ViewConfig> viewDefinitionClass)
    {
        if(!viewDefinitionClass.isAnnotationPresent(PageBean.class) &&
                !viewDefinitionClass.isAnnotationPresent(PageBean.List.class))
        {
            return Collections.emptyList();
        }

        List<PageBeanConfigEntry> result = new ArrayList<PageBeanConfigEntry>();

        if(viewDefinitionClass.isAnnotationPresent(PageBean.class))
        {
            result.add(extractBeanEntry(viewDefinitionClass.getAnnotation(PageBean.class)));
        }

        if(viewDefinitionClass.isAnnotationPresent(PageBean.List.class))
        {
            result.addAll(extractBeanEntries(viewDefinitionClass.getAnnotation(PageBean.List.class)));
        }

        return result;
    }

    private List<PageBeanConfigEntry> extractBeanEntries(PageBean.List pageBeanList)
    {
        List<PageBeanConfigEntry> result = new ArrayList<PageBeanConfigEntry>();
        for(PageBean pageBean : pageBeanList.value())
        {
            result.add(extractBeanEntry(pageBean));
        }
        return result;
    }

    private PageBeanConfigEntry extractBeanEntry(PageBean pageBean)
    {
        if(!"".equals(pageBean.name()))
        {
            return new PageBeanConfigEntry(pageBean.name(), pageBean.value());
        }

        Class<?> pageBeanClass = pageBean.value();
        String pageBeanName = null;

        //TODO allow indirect usage of @Named
        if(pageBeanClass.isAnnotationPresent(Named.class))
        {
            String beanName = pageBeanClass.getAnnotation(Named.class).value();

            if(!"".equals(beanName))
            {
                pageBeanName = beanName;
            }
        }

        if(pageBeanName == null)
        {
            pageBeanName = Introspector.decapitalize(pageBeanClass.getSimpleName());
        }

        return new PageBeanConfigEntry(pageBeanName, pageBeanClass);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ViewConfigEntry))
        {
            return false;
        }

        ViewConfigEntry that = (ViewConfigEntry) o;

        if (!viewId.equals(that.viewId))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return viewId.hashCode();
    }
}
