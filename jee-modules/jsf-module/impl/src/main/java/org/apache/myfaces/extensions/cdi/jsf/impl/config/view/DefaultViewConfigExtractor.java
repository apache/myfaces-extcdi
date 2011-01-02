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
import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.security.Secured;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigExtractor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Gerhard Petracek
 */
class DefaultViewConfigExtractor implements ViewConfigExtractor
{
    public ViewConfigEntry extractViewConfig(Class<? extends ViewConfig> viewDefinitionClass)
    {
        //use the interface to make clear which information we really need
        ViewConfigEntry scannedViewConfig = new DefaultViewConfigDefinitionEntry(viewDefinitionClass);

        scanViewConfigClass(viewDefinitionClass, (DefaultViewConfigDefinitionEntry)scannedViewConfig);

        return new DefaultViewConfigEntry(scannedViewConfig.getViewId(),
                                          viewDefinitionClass,
                                          scannedViewConfig.getNavigationMode(),
                                          scannedViewConfig.getViewParameter(),
                                          scannedViewConfig.getAccessDecisionVoters(),
                                          scannedViewConfig.getErrorView(),
                                          scannedViewConfig.getMetaData());
    }

    private Collection<Annotation> extractViewMetaData(
            Class<?> targetClass, DefaultViewConfigDefinitionEntry entry)
    {
        List<Annotation> result = new ArrayList<Annotation>();

        for(Annotation annotation : targetClass.getAnnotations())
        {
            if(annotation.annotationType().isAnnotationPresent(ViewMetaData.class))
            {
                ViewMetaData metaData = annotation.annotationType().getAnnotation(ViewMetaData.class);

                if(!entry.getFoundAndBlockedMetaDataTypes().contains(annotation.annotationType()))
                {
                    result.add(annotation);

                    if(metaData.override())
                    {
                        entry.blockMetaData(annotation.annotationType());
                    }
                }
            }
        }

        return result;
    }

    private void scanViewConfigClass(Class<?> viewDefinitionClass, DefaultViewConfigDefinitionEntry scannedViewConfig)
    {
        String defaultExtension = DefaultViewConfigDefinitionEntry.DEFAULT_EXTENSION;
        String defaultPageName = DefaultViewConfigDefinitionEntry.DEFAULT_PAGE_NAME;
        String rootPath = DefaultViewConfigDefinitionEntry.ROOT_PATH;

        String currentBasePath;
        Page pageAnnotation;
        Secured securedAnnotation;

        Class<?> currentClass = viewDefinitionClass;
        while (currentClass != null && !Object.class.getName().equals(currentClass.getName()))
        {
            //security
            if (currentClass.isAnnotationPresent(Secured.class))
            {
                securedAnnotation = currentClass.getAnnotation(Secured.class);
                scannedViewConfig.addAccessDecisionVoters(securedAnnotation.value());

                if (scannedViewConfig.getErrorView() == null &&
                        !DefaultErrorView.class.getName().equals(securedAnnotation.errorView().getName()))
                {
                    scannedViewConfig.setErrorView(securedAnnotation.errorView());
                }
            }

            //meta-data
            scannedViewConfig.addMetaData(
                    extractViewMetaData(currentClass, scannedViewConfig));

            //page definition
            if (currentClass.isAnnotationPresent(Page.class))
            {
                pageAnnotation = currentClass.getAnnotation(Page.class);

                if (!pageAnnotation.extension().equals(defaultExtension))
                {
                    scannedViewConfig.setExtension(pageAnnotation.extension());
                }

                if (!pageAnnotation.basePath().equals(rootPath))
                {
                    currentBasePath = pageAnnotation.basePath();

                    if (!".".equals(currentBasePath))
                    {
                        scannedViewConfig
                                .setSimpleClassNameToPathMapping(currentClass.getSimpleName(), currentBasePath);
                    }

                    if (rootPath.equals(scannedViewConfig.getBasePath()))
                    {
                        scannedViewConfig.setBasePath(currentBasePath);
                    }
                }

                if (!scannedViewConfig.isKnownNavigationMode() &&
                        !pageAnnotation.navigation().equals(Page.NavigationMode.DEFAULT))
                {
                    scannedViewConfig.setNavigationMode(pageAnnotation.navigation());
                }

                if (!scannedViewConfig.isKnownViewParameter() &&
                        !pageAnnotation.viewParams().equals(Page.ViewParameter.DEFAULT))
                {
                    scannedViewConfig.setViewParameter(pageAnnotation.viewParams());
                }

                if (!pageAnnotation.name().equals(defaultPageName))
                {
                    scannedViewConfig.setPageName(pageAnnotation.name());
                }
            }

            //scan interfaces
            for(Class interfaceClass : currentClass.getInterfaces())
            {
                scanViewConfigClass(interfaceClass, scannedViewConfig);
            }

            //scan super class
            currentClass = currentClass.getSuperclass();
        }
    }
}
