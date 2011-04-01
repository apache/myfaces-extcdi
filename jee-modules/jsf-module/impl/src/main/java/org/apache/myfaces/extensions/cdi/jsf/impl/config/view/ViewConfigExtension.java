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
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigExtractor;
import org.apache.myfaces.extensions.cdi.jsf.impl.listener.phase.ViewControllerInterceptor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ViewConfigExtension implements Extension, Deactivatable
{
    private Logger logger = Logger.getLogger(ViewConfigExtension.class.getName());

    /**
     * Initializes the whole view-config data-structures.
     * @param processAnnotatedType current process-annotated-type
     */
    public void processPageDefinitions(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        CodiStartupBroadcaster.broadcastStartup();

        if(processAnnotatedType.getAnnotatedType().isAnnotationPresent(InlineViewConfigRoot.class))
        {
            if(this.logger.isLoggable(Level.INFO))
            {
                this.logger.info(InlineViewConfigRoot.class.getName() + " found at " +
                        processAnnotatedType.getAnnotatedType().getJavaClass().getName());
            }

            setInlineViewConfigRootMarker(processAnnotatedType.getAnnotatedType().getJavaClass());
            vetoBean(processAnnotatedType);

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
                vetoBean(processAnnotatedType);
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
        if(this.logger.isLoggable(Level.INFO))
        {
            this.logger.info(pageDefinitionClass.getName() + " will be used as page-definition.");
        }

        ViewConfigDescriptor newEntry = createViewConfigDescriptor(pageDefinitionClass);

        if(newEntry != null)
        {
            ViewConfigDescriptor existingDescriptor = ViewConfigCache.getViewConfig(newEntry.getViewConfig());

            //TODO introduce an SPI with a better name
            if(/*viewConfigDescriptor != null*/existingDescriptor instanceof DefaultViewConfigDescriptor
                    && ((DefaultViewConfigDescriptor)existingDescriptor).isSimpleEntryMode())
            {
                //in this case the alternative view-controller approach which just adds page-beans was invoked before
                //-> we just have to use the page bean of the existing entry

                //here we have a simple-entry!   (which just contains page-bean definitions)
                for(PageBeanDescriptor pageBeanDescriptor : existingDescriptor.getPageBeanDescriptors())
                {
                    //add page-beans to the real entry
                    if(newEntry instanceof EditableViewConfigDescriptor)
                    {
                        ((EditableViewConfigDescriptor)newEntry).addPageBean(pageBeanDescriptor.getBeanClass());
                    }
                }
                ViewConfigCache.replaceViewConfigDescriptor(newEntry.getViewId(), newEntry);
                return;
            }

            //add created entry
            //if there is already an normal (not simple!) entry force an exception
            ViewConfigCache.addViewConfigDescriptor(newEntry.getViewId(), newEntry);
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
        if(this.logger.isLoggable(Level.INFO))
        {
            this.logger.info(beanClass.getName() + " will be used as inline-page-definition.");
        }

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

        if(this.logger.isLoggable(Level.INFO))
        {
            this.logger.info(annotatedType.getJavaClass().getName() + " will be used as page-bean.");
        }

        for(Class<? extends ViewConfig> viewConfigClass : view.value())
        {
            ViewConfigDescriptor viewConfigDescriptor = ViewConfigCache.getViewConfig(viewConfigClass);

            if(viewConfigDescriptor == null)
            {
                ViewConfigDescriptor entry = createViewConfigDescriptor(viewConfigClass);

                if(entry != null)
                {
                    if(entry instanceof EditableViewConfigDescriptor)
                    {
                        ((EditableViewConfigDescriptor)entry).addPageBean(annotatedType.getJavaClass());
                    }

                    if(entry instanceof DefaultViewConfigDescriptor)
                    {
                        //TODO introduce an SPI with a better name
                        ((DefaultViewConfigDescriptor)entry).activateSimpleEntryMode();
                    }
                    ViewConfigCache.addViewConfigDescriptor(entry.getViewId(), entry);
                }
            }
            else
            {
                if(viewConfigDescriptor instanceof EditableViewConfigDescriptor)
                {
                    ((EditableViewConfigDescriptor)viewConfigDescriptor).addPageBean(annotatedType.getJavaClass());
                }
            }
        }
    }

    protected ViewConfigDescriptor createViewConfigDescriptor(Class<? extends ViewConfig> viewDefinitionClass)
    {
        //we use abstract classes for nesting definitions
        //TODO log a warning in case of project-stage dev
        if(Modifier.isAbstract(viewDefinitionClass.getModifiers()))
        {
            return null;
        }

        ViewConfigDescriptor result = getViewConfigExtractor().extractViewConfig(viewDefinitionClass);
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

    private void vetoBean(ProcessAnnotatedType processAnnotatedType)
    {
        if(this.logger.isLoggable(Level.FINER))
        {
            this.logger.finer(processAnnotatedType.getAnnotatedType().getJavaClass().getName() +
                    " won't be used as CDI bean");
        }

        processAnnotatedType.veto();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
