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
package org.apache.myfaces.extensions.cdi.jsf2.impl.scope.mapped;

import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.util.ClassDeactivation;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;

import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.event.Observes;
import javax.faces.bean.ManagedBean;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
public class MappedJsf2ScopeExtension implements Extension, Deactivatable
{
    private final Logger logger = Logger.getLogger(MappedJsf2ScopeExtension.class.getName());

    private Map<Class<? extends Annotation>, Class<? extends Annotation>> mappedJsfScopes
            = new HashMap<Class<? extends Annotation>, Class<? extends Annotation>>();

    public MappedJsf2ScopeExtension()
    {
        this.mappedJsfScopes.put(javax.faces.bean.ApplicationScoped.class,
                                 javax.enterprise.context.ApplicationScoped.class);
        this.mappedJsfScopes.put(javax.faces.bean.SessionScoped.class,
                                 javax.enterprise.context.SessionScoped.class);
        this.mappedJsfScopes.put(javax.faces.bean.RequestScoped.class,
                                 javax.enterprise.context.RequestScoped.class);

        //there is no cdi scope for it - we just need it for @ManagedBean
        this.mappedJsfScopes.put(javax.faces.bean.ViewScoped.class,
                                 javax.faces.bean.ViewScoped.class);
    }

    protected void convertJsf2Scopes(@Observes ProcessAnnotatedType processAnnotatedType)
    {
        if(!isActivated())
        {
            return;
        }

        Class<? extends Annotation> jsf2ScopeAnnotation = getJsf2ScopeAnnotation(processAnnotatedType);

        if(jsf2ScopeAnnotation != null && !beanUsesUnsupportedManagedBeanAnnotation(processAnnotatedType))
        {
            //noinspection unchecked
            processAnnotatedType.setAnnotatedType(
                    convertBean(processAnnotatedType.getAnnotatedType(), jsf2ScopeAnnotation));
        }
    }

    private AnnotatedType convertBean(AnnotatedType annotatedType, Class<? extends Annotation> jsf2ScopeAnnotation)
    {
        logConvertedBean(annotatedType, jsf2ScopeAnnotation);

        return new Jsf2BeanWrapper(annotatedType, this.mappedJsfScopes.get(jsf2ScopeAnnotation), jsf2ScopeAnnotation);
    }

    private Class<? extends Annotation> getJsf2ScopeAnnotation(ProcessAnnotatedType processAnnotatedType)
    {
        for(Class<? extends Annotation> currentJsfScope : this.mappedJsfScopes.keySet())
        {
            if(processAnnotatedType.getAnnotatedType().getJavaClass().isAnnotationPresent(currentJsfScope))
            {
                return currentJsfScope;
            }
        }
        return null;
    }

    private void logConvertedBean(AnnotatedType annotatedType, Class<? extends Annotation> jsf2ScopeAnnotation)
    {
        ProjectStage projectStage = ProjectStageProducer.getInstance().getProjectStage();

        if (projectStage == ProjectStage.Development)
        {
            logger.info("JSF2 bean was converted to a CDI bean. type: " + annotatedType.getJavaClass().getName() +
                    " original scope: " + jsf2ScopeAnnotation.getName());
        }
    }

    private boolean beanUsesUnsupportedManagedBeanAnnotation(ProcessAnnotatedType processAnnotatedType)
    {
        Class<?> beanClass = processAnnotatedType.getAnnotatedType().getJavaClass();

        if(!beanClass.isAnnotationPresent(ManagedBean.class))
        {
            return false;
        }

        ManagedBean managedBeanAnnotation = beanClass.getAnnotation(ManagedBean.class);

        if(managedBeanAnnotation.eager())
        {
            ProjectStage projectStage = ProjectStageProducer.getInstance().getProjectStage();

            if (projectStage == ProjectStage.Development)
            {
                logger.warning("Bean of type " + beanClass.getName() + " uses @" + ManagedBean.class.getName() +
                        " in combination with #eager = true. That's not supported with CDI. " +
                        "The bean won't get transformed to a CDI bean.");
            }
            return true;
        }
        return false;
    }

    public boolean isActivated()
    {
        return ClassDeactivation.isClassActivated(getClass());
    }
}
