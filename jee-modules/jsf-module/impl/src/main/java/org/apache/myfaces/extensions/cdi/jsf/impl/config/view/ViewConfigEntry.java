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
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.NavigationMode;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBean;

import javax.inject.Named;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Gerhard Petracek
 */
public class ViewConfigEntry
{
    private final String viewId;
    private final Class<? extends ViewConfig> viewDefinitionClass;
    private final NavigationMode navigationMode;
    private final List<PageBeanConfigEntry> beanDefinition;

    //security
    private final Class<? extends AccessDecisionVoter>[] accessDecisionVoters;
    private final Class<? extends ViewConfig> customErrorView;

    //meta-data
    private List<Annotation> metaDataList;

    public ViewConfigEntry(String viewId,
                           Class<? extends ViewConfig> viewDefinitionClass,
                           NavigationMode navigationMode,
                           List<Class<? extends AccessDecisionVoter>> accessDecisionVoters,
                           Class<? extends ViewConfig> errorView,
                           List<Annotation> metaDataList)
    {
        this.viewId = viewId;
        this.viewDefinitionClass = viewDefinitionClass;
        this.navigationMode = navigationMode;

        this.metaDataList = metaDataList;

        beanDefinition = Collections.unmodifiableList(findPageBeanDefinitions(viewDefinitionClass));
        //TODO validate view-id

        //noinspection unchecked
        this.accessDecisionVoters = accessDecisionVoters.toArray(new Class[accessDecisionVoters.size()]);

        if(errorView != null)
        {
            this.customErrorView = errorView;
        }
        else
        {
            this.customErrorView = DefaultErrorView.class;
        }
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

    public void invokeInitViewMethods()
    {
        for(PageBeanConfigEntry beanEntry : getBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getInitViewMethods());
        }
    }

    public void invokePrePageActionMethods()
    {
        for(PageBeanConfigEntry beanEntry : getBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getPrePageActionMethods());
        }
    }

    void invokePreRenderViewMethods()
    {
        for(PageBeanConfigEntry beanEntry : getBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getPreRenderViewMethods());
        }
    }

    private void processCallbacks(PageBeanConfigEntry beanEntry, List<Method> methodList)
    {
        Object bean;
        if (!methodList.isEmpty())
        {
            //TODO provide a detailed error message in case of a missing bean
            bean = CodiUtils.getOrCreateScopedInstanceOfBeanByName(beanEntry.getBeanName(), Object.class);

            if (bean == null)
            {
                return;
            }

            for (Method callbackMethod : methodList)
            {
                invokeMethod(bean, callbackMethod);
            }
        }
    }

    private void invokeMethod(Object bean, Method preProcessMethod)
    {
        try
        {
            preProcessMethod.setAccessible(true);
            preProcessMethod.invoke(bean);
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }

    public Class<? extends AccessDecisionVoter>[] getAccessDecisionVoters()
    {
        return accessDecisionVoters;
    }

    public Class<? extends ViewConfig> getErrorView()
    {
        return customErrorView;
    }

    public List<Annotation> getMetaData()
    {
        return metaDataList;
    }

    public synchronized void addMetaData(Annotation annotation)
    {
        this.metaDataList.add(annotation);
    }

    public synchronized List<Annotation> resetMetaData()
    {
        try
        {
            return new ArrayList<Annotation>(this.metaDataList);
        }
        finally
        {
            this.metaDataList.clear();
        }
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
