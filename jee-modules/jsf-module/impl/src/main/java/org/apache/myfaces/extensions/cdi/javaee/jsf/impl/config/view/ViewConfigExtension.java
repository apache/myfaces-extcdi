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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.config.view.JsfViewExtension;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.config.view.NavigationMode;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ViewConfigExtension implements Extension
{
    public void processPageDefinitions(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(Page.class))
        {
            addPageDefinition(processAnnotatedType.getAnnotatedType().getJavaClass());
            processAnnotatedType.veto();
        }
    }

    private void addPageDefinition(Class pageDefinitionClass)
    {
        if(!ViewConfig.class.isAssignableFrom(pageDefinitionClass))
        {
            throw new IllegalArgumentException(
                    "the page definition " + pageDefinitionClass.getName() + " has to implement "
                            + ViewConfig.class.getName());
        }

        @SuppressWarnings({"unchecked"})
        Class<? extends ViewConfig> viewDefinitionClass = (Class<? extends ViewConfig>)pageDefinitionClass;

        if(Modifier.isAbstract(viewDefinitionClass.getModifiers()))
        {
            return;
        }

        Class<?> currentClass = viewDefinitionClass;

        String rootPath = "/";
        String basePath = rootPath;
        String currentBasePath;
        Map<String, String> simpleClassNameToPathMapping = new HashMap<String, String>();

        String defaultPageName = "";
        String pageName = defaultPageName;

        String defaultExtension = JsfViewExtension.XHTML;
        String extension = defaultExtension;

        NavigationMode defaultNavigationMode = NavigationMode.FORWARD;
        NavigationMode navigationMode = defaultNavigationMode;

        //TODO
        Page pageAnnotation;
        while(!Object.class.getName().equals(currentClass.getName()))
        {
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

                if(!pageAnnotation.navigation().equals(defaultNavigationMode))
                {
                    navigationMode = pageAnnotation.navigation();
                }

                if(!pageAnnotation.name().equals(defaultPageName))
                {
                    pageName = pageAnnotation.name();
                }
            }

            currentClass = currentClass.getSuperclass();
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
        ViewConfigCache.addViewDefinition(
                result, new ViewConfigEntry(result, viewDefinitionClass, navigationMode));
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
}
