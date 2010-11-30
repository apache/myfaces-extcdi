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
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewMetaData;
import org.apache.myfaces.extensions.cdi.core.api.config.view.View;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.core.api.security.Secured;
import org.apache.myfaces.extensions.cdi.core.api.security.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.NavigationMode;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.ViewParameter;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase.ViewControllerInterceptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ViewConfigExtension implements Extension, Deactivatable
{
    public void processPageDefinitions(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(Page.class))
        {
            addPageDefinition(processAnnotatedType.getAnnotatedType().getJavaClass());
            processAnnotatedType.veto();
        }

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(View.class) &&
                !processAnnotatedType.getAnnotatedType().getJavaClass().equals(ViewControllerInterceptor.class))
        {
            addPageBean(processAnnotatedType.getAnnotatedType());

            processAnnotatedType.setAnnotatedType(
                                new ViewControllerWrapper(processAnnotatedType.getAnnotatedType()));
        }
    }

    protected void addPageDefinition(Class pageDefinitionClass)
    {
        ViewConfigEntry entry = createViewConfigEntry(pageDefinitionClass);

        if(entry != null)
        {
            ViewConfigEntry viewConfigEntry = ViewConfigCache.getViewDefinition(entry.getViewDefinitionClass());

            if(viewConfigEntry != null && viewConfigEntry.isSimpleEntryMode())
            {
                //in this case the alternative view-controller approach which just adds page-beans was invoked before
                //-> we just have to use the page bean of the existing entry

                for(Class pageBeanClass : viewConfigEntry.getPageBeanClasses())
                {
                    entry.addPageBean(pageBeanClass);
                }
                ViewConfigCache.replaceViewDefinition(entry.getViewId(), entry);
                return;
            }

            //add created entry
            //if there is already an normal (not simple!) entry force an exception
            ViewConfigCache.addViewDefinition(entry.getViewId(), entry);
        }
    }

    /**
     * important: {@link org.apache.myfaces.extensions.cdi.core.api.config.view.View#inline()} isn't supported!
     *
     * @param annotatedType current annotated type
     */
    private void addPageBean(AnnotatedType annotatedType)
    {
        View view = annotatedType.getAnnotation(View.class);

        if(!"".equals(view.inline()[0]))
        {
            //TODO move exceptions to util class
            throw new IllegalStateException("Definition error at: " + annotatedType.getJavaClass().getName() +
                    " it isn't allowed to define a class level @" + View.class.getName() +
                    " without a typesafe view config. Please don't use @View(inline=\"...\") for this use-case!");
        }

        String viewId;
        for(Class<? extends ViewConfig> viewConfigClass : view.value())
        {
            ViewConfigEntry viewConfigEntry = ViewConfigCache.getViewDefinition(viewConfigClass);

            if(viewConfigEntry == null)
            {
                ViewConfigEntry entry = createViewConfigEntry(viewConfigClass);

                if(entry != null)
                {
                    entry.addPageBean(annotatedType.getJavaClass());
                    entry.activateSimpleEntryMode();
                    ViewConfigCache.addViewDefinition(entry.getViewId(), entry);
                }
            }
            else
            {
                viewConfigEntry.addPageBean(annotatedType.getJavaClass());
            }
        }
    }

    protected ViewConfigEntry createViewConfigEntry(Class pageDefinitionClass)
    {
        if(!ViewConfig.class.isAssignableFrom(pageDefinitionClass))
        {
            throw new IllegalArgumentException(
                    "the page definition " + pageDefinitionClass.getName() + " has to implement "
                            + ViewConfig.class.getName());
        }

        @SuppressWarnings({"unchecked"})
        Class<? extends ViewConfig> viewDefinitionClass = (Class<? extends ViewConfig>)pageDefinitionClass;

        //we use abstract classes for nesting definitions
        //TODO log a warning in case of project-stage dev
        if(Modifier.isAbstract(viewDefinitionClass.getModifiers()))
        {
            return null;
        }

        Class<?> currentClass = viewDefinitionClass;

        String rootPath = "/";
        String basePath = rootPath;
        String currentBasePath;
        Map<String, String> simpleClassNameToPathMapping = new HashMap<String, String>();

        String defaultPageName = "";
        String pageName = defaultPageName;

        String defaultExtension = Page.Extension.XHTML;
        String extension = defaultExtension;

        NavigationMode defaultNavigationMode = NavigationMode.DEFAULT;
        NavigationMode navigationMode = null;

        ViewParameter defaultViewParameter = ViewParameter.DEFAULT;
        ViewParameter viewParameter = null;

        //security
        List<Class<? extends AccessDecisionVoter>> foundVoters = new ArrayList<Class<? extends AccessDecisionVoter>>();
        Class<? extends ViewConfig> errorView = null;

        List<Annotation> viewMetaDataList = new ArrayList<Annotation>();
        List<Class<? extends Annotation>> foundAndBlockedMetaDataTypes = new ArrayList<Class<? extends Annotation>>();

        //TODO
        Page pageAnnotation;
        Secured securedAnnotation;
        while(!Object.class.getName().equals(currentClass.getName()))
        {
            //security
            if(currentClass.isAnnotationPresent(Secured.class))
            {
                securedAnnotation = currentClass.getAnnotation(Secured.class);
                Collections.addAll(foundVoters, securedAnnotation.value());

                if(errorView == null &&
                        !DefaultErrorView.class.getName().equals(securedAnnotation.errorView().getName()))
                {
                    errorView = securedAnnotation.errorView();
                }
            }

            //meta-data
            viewMetaDataList.addAll(extractViewMetaData(currentClass, foundAndBlockedMetaDataTypes));

            //page definition
            if(currentClass.isAnnotationPresent(Page.class))
            {
                pageAnnotation = currentClass.getAnnotation(Page.class);

                if(!pageAnnotation.extension().equals(defaultExtension))
                {
                    extension = pageAnnotation.extension();
                }

                if(!pageAnnotation.basePath().equals(rootPath))
                {
                    currentBasePath = pageAnnotation.basePath();

                    if(!".".equals(currentBasePath))
                    {
                        simpleClassNameToPathMapping.put(currentClass.getSimpleName(), currentBasePath);
                    }

                    if(rootPath.equals(basePath))
                    {
                        basePath = currentBasePath;
                    }
                }

                if(navigationMode == null && !pageAnnotation.navigation().equals(defaultNavigationMode))
                {
                    navigationMode = pageAnnotation.navigation();
                }

                if(viewParameter == null && !pageAnnotation.viewParams().equals(defaultViewParameter))
                {
                    viewParameter = pageAnnotation.viewParams();
                }

                if(!pageAnnotation.name().equals(defaultPageName))
                {
                    pageName = pageAnnotation.name();
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        if(navigationMode == null)
        {
            navigationMode = defaultNavigationMode;
        }

        if(viewParameter == null)
        {
            viewParameter = defaultViewParameter;
        }

        StringBuilder viewId = new StringBuilder(basePath);
        if(pageName.equals(""))
        {
            String className = viewDefinitionClass.getName();

            //MyClass$MyInnerClass will be converted to /MyClass/MyInnerClass
            if(className.contains("$") && ".".equals(basePath))
            {
                basePath = "";
                className = className.substring(className.lastIndexOf(".") + 1);
                className = convertToPathSyntax(className, simpleClassNameToPathMapping);
            }
            else if(className.contains("$"))
            {
                className = className.substring(className.lastIndexOf("$") + 1);
            }
            else
            {
                className = className.substring(className.lastIndexOf(".") + 1);
            }
            className = className.substring(0, 1).toLowerCase() + className.substring(1);
            viewId.append(className);
        }
        //nested classes with manually defined page name and shared basePath
        else if(!simpleClassNameToPathMapping.isEmpty())
        {
            String className = viewDefinitionClass.getName();

            basePath = "";
            className = className.substring(className.lastIndexOf(".") + 1);
            className = convertToPathSyntax(className, simpleClassNameToPathMapping);
            className = className.substring(0, 1).toLowerCase() + className.substring(1);
            className = className.substring(0, className.lastIndexOf("/") + 1);
            className += pageName;
            viewId.append(className);
        }
        else
        {
            viewId.append(pageName);
        }
        viewId.append(".");
        viewId.append(extension);
        String result = viewId.toString();

        if(result.startsWith("."))
        {
            if(result.startsWith("./"))
            {
                result = result.substring(1);
            }
            else
            {
                result = rootPath + result.substring(1);
            }
        }

        result = ensureValidViewIds(result);

        return new ViewConfigEntry(
                result, viewDefinitionClass, navigationMode, viewParameter, foundVoters, errorView, viewMetaDataList);
    }

    private String ensureValidViewIds(String result)
    {
        if(!result.startsWith("/"))
        {
            result = "/" + result;
        }

        //TODO
        return result.replace("///", "/").replace("//", "/");
    }

    private String convertToPathSyntax(String className, Map<String, String> simpleClassNameToPathMapping)
    {
        String[] parts = className.split("\\$");
        StringBuilder path = new StringBuilder();

        for(String part : parts)
        {
            if(simpleClassNameToPathMapping.containsKey(part))
            {
                path.append(simpleClassNameToPathMapping.get(part));
            }
            else
            {
                path.append(part.substring(0, 1).toLowerCase());
                path.append(part.substring(1));
            }
            path.append("/");
        }
        String result = path.toString();
        return result.substring(0, result.length() - 1);
    }

    private Collection<Annotation> extractViewMetaData(
            Class<?> targetClass, List<Class<? extends Annotation>> blockedMetaDataTypes)
    {
        List<Annotation> result = new ArrayList<Annotation>();

        for(Annotation annotation : targetClass.getAnnotations())
        {
            if(annotation.annotationType().isAnnotationPresent(ViewMetaData.class))
            {
                ViewMetaData metaData = annotation.annotationType().getAnnotation(ViewMetaData.class);

                if(!blockedMetaDataTypes.contains(annotation.annotationType()))
                {
                    result.add(annotation);

                    if(metaData.override())
                    {
                        blockedMetaDataTypes.add(annotation.annotationType());
                    }
                }
            }
        }

        return result;
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
