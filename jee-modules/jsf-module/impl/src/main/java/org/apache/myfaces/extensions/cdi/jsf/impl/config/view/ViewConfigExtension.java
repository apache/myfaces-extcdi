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
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.InlineViewConfigRoot;
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
    public void processPageDefinitions(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        CodiStartupBroadcaster.broadcastStartup();

        if(processAnnotatedType.getAnnotatedType().isAnnotationPresent(InlineViewConfigRoot.class))
        {
            setInlineViewConfigRootMarker(processAnnotatedType.getAnnotatedType().getJavaClass());
            processAnnotatedType.veto();
            return;
        }

        beginViewConfigExtraction();

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(Page.class))
        {
            validateViewConfigDefinition(processAnnotatedType.getAnnotatedType().getJavaClass());

            @SuppressWarnings({"unchecked"})
            Class<? extends ViewConfig> beanClass = processAnnotatedType.getAnnotatedType().getJavaClass();

            ViewConfigExtractor viewConfigExtractor = getViewConfigExtractor();
            if(isInlineViewConfig(viewConfigExtractor, beanClass))
            {
                addInlinePageDefinition(viewConfigExtractor, beanClass);
            }
            else
            {
                addPageDefinition(beanClass);
                processAnnotatedType.veto();
            }
        }

        if (processAnnotatedType.getAnnotatedType().isAnnotationPresent(View.class) &&
                !processAnnotatedType.getAnnotatedType().getJavaClass().equals(ViewControllerInterceptor.class))
        {
            addPageBean(processAnnotatedType.getAnnotatedType());

            //noinspection unchecked
            processAnnotatedType.setAnnotatedType(
                                new ViewControllerWrapper(processAnnotatedType.getAnnotatedType()));
        }

        endViewConfigExtraction();
    }

    protected void beginViewConfigExtraction()
    {
        ViewConfigCache.activateWriteMode();
    }

    protected void endViewConfigExtraction()
    {
        ViewConfigCache.deactivateWriteMode();
    }

    protected void setInlineViewConfigRootMarker(Class markerClass)
    {
        ViewConfigCache.setInlineViewConfigRootMarker(markerClass);
    }

    protected void addPageDefinition(Class<? extends ViewConfig> pageDefinitionClass)
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

    protected boolean isInlineViewConfig(Class<? extends ViewConfig> beanClass)
    {
        return isInlineViewConfig(getViewConfigExtractor(), beanClass);
    }

    private boolean isInlineViewConfig(ViewConfigExtractor viewConfigExtractor, Class<? extends ViewConfig> beanClass)
    {
        return viewConfigExtractor.isInlineViewConfig(beanClass);
    }

    protected void addInlinePageDefinition(Class<? extends ViewConfig> beanClass)
    {
        addInlinePageDefinition(getViewConfigExtractor(), beanClass);
    }

    private void addInlinePageDefinition(ViewConfigExtractor viewConfigExtractor, Class<? extends ViewConfig> beanClass)
    {
        ViewConfigCache.queueInlineViewConfig(viewConfigExtractor, beanClass);
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

    protected ViewConfigEntry createViewConfigEntry(Class<? extends ViewConfig> viewDefinitionClass)
    {
        //we use abstract classes for nesting definitions
        //TODO log a warning in case of project-stage dev
        if(Modifier.isAbstract(viewDefinitionClass.getModifiers()))
        {
            return null;
        }

        ViewConfigEntry result = getViewConfigExtractor().extractViewConfig(viewDefinitionClass);
        return result;
    }

    private void validateViewConfigDefinition(Class beanClass)
    {
        if(!ViewConfig.class.isAssignableFrom(beanClass))
        {
            throw new IllegalArgumentException(
                    "the page definition " + beanClass.getName() + " has to implement "
                            + ViewConfig.class.getName());
        }
    }

    private ViewConfigExtractor getViewConfigExtractor()
    {
        ViewConfigExtractor viewConfigExtractor = CodiUtils.lookupFromEnvironment(ViewConfigExtractor.class);

        if(viewConfigExtractor == null)
        {
            viewConfigExtractor = new DefaultViewConfigExtractor();
        }
        return viewConfigExtractor;
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
