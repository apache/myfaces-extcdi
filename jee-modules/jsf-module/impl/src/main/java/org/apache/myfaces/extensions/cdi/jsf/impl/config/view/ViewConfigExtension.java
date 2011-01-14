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
import org.apache.myfaces.extensions.cdi.core.api.config.view.View;
import org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.PageBeanConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigExtractor;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase.ViewControllerInterceptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.Modifier;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ViewConfigExtension implements Extension, Deactivatable
{
    private static final String VIEW_CONFIG_EXTRACTOR_PROPERTY_NAME =
            "org.apache.myfaces.extensions.cdi.ViewConfigExtractor";

    private static final String VIEW_CONFIG_EXTRACTOR_JNDI_NAME =
            "java:comp/env/myfaces-codi/ViewConfigExtractor";

    public void processPageDefinitions(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        CodiStartupBroadcaster.broadcastStartup();

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(Page.class))
        {
            addPageDefinition(processAnnotatedType.getAnnotatedType().getJavaClass());
            processAnnotatedType.veto();
        }

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(View.class) &&
                !processAnnotatedType.getAnnotatedType().getJavaClass().equals(ViewControllerInterceptor.class))
        {
            addPageBean(processAnnotatedType.getAnnotatedType());

            //noinspection unchecked
            processAnnotatedType.setAnnotatedType(
                                new ViewControllerWrapper(processAnnotatedType.getAnnotatedType()));
        }
    }

    protected void addPageDefinition(Class pageDefinitionClass)
    {
        ViewConfigEntry newEntry = createViewConfigEntry(pageDefinitionClass);

        if(newEntry != null)
        {
            ViewConfigEntry existingEntry = ViewConfigCache.getViewDefinition(newEntry.getViewDefinitionClass());

            //TODO introduce an SPI with a better name
            if(/*viewConfigEntry != null*/existingEntry instanceof DefaultViewConfigEntry
                    && ((DefaultViewConfigEntry)existingEntry).isSimpleEntryMode())
            {
                //in this case the alternative view-controller approach which just adds page-beans was invoked before
                //-> we just have to use the page bean of the existing entry

                //here we have a simple-entry!   (which just contains page-bean definitions)
                for(PageBeanConfigEntry pageBeanConfigEntry : existingEntry.getPageBeanDefinitions())
                {
                    //add page-beans to the real entry
                    newEntry.addPageBean(pageBeanConfigEntry.getBeanClass());
                }
                ViewConfigCache.replaceViewDefinition(newEntry.getViewId(), newEntry);
                return;
            }

            //add created entry
            //if there is already an normal (not simple!) entry force an exception
            ViewConfigCache.addViewDefinition(newEntry.getViewId(), newEntry);
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

                    //TODO introduce an SPI with a better name
                    if(entry instanceof DefaultViewConfigEntry)
                    {
                        ((DefaultViewConfigEntry)entry).activateSimpleEntryMode();
                    }
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

        ViewConfigExtractor viewConfigExtractor = CodiUtils.lookupFromEnvironment(VIEW_CONFIG_EXTRACTOR_PROPERTY_NAME,
                                                                                  VIEW_CONFIG_EXTRACTOR_JNDI_NAME,
                                                                                  ViewConfigExtractor.class);
        
        if(viewConfigExtractor == null)
        {
            viewConfigExtractor = new DefaultViewConfigExtractor();
        }

        ViewConfigEntry result = viewConfigExtractor.extractViewConfig(viewDefinitionClass);
        return result;
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
