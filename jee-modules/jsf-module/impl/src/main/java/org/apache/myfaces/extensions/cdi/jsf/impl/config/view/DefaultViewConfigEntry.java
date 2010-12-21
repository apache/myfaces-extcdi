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
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBean;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.PageBeanConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigEntry;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.getOrCreateScopedInstanceOfBeanByName;

/**
 * @author Gerhard Petracek
 */
public class DefaultViewConfigEntry implements ViewConfigEntry
{
        private final String viewId;
    private final Class<? extends ViewConfig> viewDefinitionClass;
    private final Page.NavigationMode navigationMode;

    private List<PageBeanConfigEntry> beanDefinition;

    //security
    private final Class<? extends AccessDecisionVoter>[] accessDecisionVoters;
    private final Class<? extends ViewConfig> customErrorView;

    private Page.ViewParameter viewParameter;
    //meta-data
    private List<Annotation> metaDataList;

    private boolean simpleEntryMode = false;

    private BeanManager beanManager;

    public DefaultViewConfigEntry(String viewId,
                           Class<? extends ViewConfig> viewDefinitionClass,
                           Page.NavigationMode navigationMode,
                           Page.ViewParameter viewParameter,
                           Class<? extends AccessDecisionVoter>[] accessDecisionVoters,
                           Class<? extends ViewConfig> errorView,
                           List<Annotation> metaDataList)
    {
        this.viewId = viewId;
        this.viewDefinitionClass = viewDefinitionClass;
        this.navigationMode = navigationMode;
        this.viewParameter = viewParameter;

        this.metaDataList = metaDataList;

        beanDefinition = Collections.unmodifiableList(findPageBeanDefinitions(viewDefinitionClass));
        //TODO validate view-id

        //noinspection unchecked
        this.accessDecisionVoters = accessDecisionVoters;

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

    public Page.NavigationMode getNavigationMode()
    {
        return navigationMode;
    }

    public Page.ViewParameter getViewParameter()
    {
        return viewParameter;
    }

    public List<PageBeanConfigEntry> getPageBeanDefinitions()
    {
        return beanDefinition;
    }

    public void invokeInitViewMethods()
    {
        for(PageBeanConfigEntry beanEntry : getPageBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getInitViewMethods());
        }
    }

    public void invokePrePageActionMethods()
    {
        for(PageBeanConfigEntry beanEntry : getPageBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getPrePageActionMethods());
        }
    }

    public void invokePreRenderViewMethods()
    {
        for(PageBeanConfigEntry beanEntry : getPageBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getPreRenderViewMethods());
        }
    }

    public void invokePostRenderViewMethods()
    {
        for(PageBeanConfigEntry beanEntry : getPageBeanDefinitions())
        {
            processCallbacks(beanEntry, beanEntry.getPostRenderViewMethods());
        }
    }

    private void processCallbacks(PageBeanConfigEntry beanEntry, List<Method> methodList)
    {
        Object bean;
        if (!methodList.isEmpty())
        {
            //TODO provide a detailed error message in case of a missing bean
            bean = getOrCreateScopedInstanceOfBeanByName(getBeanManager(), beanEntry.getBeanName(), Object.class);

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

    public void addPageBean(Class pageBeanClass)
    {
        List<PageBeanConfigEntry> newList = new ArrayList<PageBeanConfigEntry>(this.beanDefinition);

        PageBeanConfigEntry newEntry = new DefaultPageBeanConfigEntry(getBeanName(pageBeanClass) , pageBeanClass);

        newList.add(newEntry);

        this.beanDefinition = Collections.unmodifiableList(newList);
    }

    void activateSimpleEntryMode()
    {
        this.simpleEntryMode = true;
    }

    boolean isSimpleEntryMode()
    {
        return simpleEntryMode;
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
            return new DefaultPageBeanConfigEntry(pageBean.name(), pageBean.value());
        }

        Class<?> pageBeanClass = pageBean.value();
        String pageBeanName = null;

        //TODO allow indirect usage of @Named
        pageBeanName = getBeanName(pageBeanClass);

        return new DefaultPageBeanConfigEntry(pageBeanName, pageBeanClass);
    }

    private String getBeanName(Class<?> pageBeanClass)
    {
        if(pageBeanClass.isAnnotationPresent(Named.class))
        {
            String beanName = pageBeanClass.getAnnotation(Named.class).value();

            if(!"".equals(beanName))
            {
                return beanName;
            }
        }

        return Introspector.decapitalize(pageBeanClass.getSimpleName());
    }

    private BeanManager getBeanManager()
    {
        if(this.beanManager == null)
        {
            this.beanManager = BeanManagerProvider.getInstance().getBeanManager();
        }

        return this.beanManager;
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

        if (!viewId.equals(that.getViewId()))
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
