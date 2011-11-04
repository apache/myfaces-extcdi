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

import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.impl.util.NamedLiteral;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

/**
 * Helper used by {@link MappedJsf2ScopeExtension}
 */
class Jsf2BeanWrapper implements AnnotatedType<Object>
{
    private final AnnotatedType wrapped;
    private Map<Class<? extends Annotation>, Annotation> annotations;
    private Set<Annotation> annotationSet; //TODO

    Jsf2BeanWrapper(AnnotatedType wrapped,
                    Class<? extends Annotation> cdiScopeAnnotation,
                    Class<? extends Annotation> jsf2ScopeAnnotation)
    {
        this.wrapped = wrapped;
        Set<Annotation> originalAnnotationSet = wrapped.getAnnotations();
        this.annotations = new HashMap<Class<? extends Annotation>, Annotation>(originalAnnotationSet.size());

        for(Annotation originalAnnotation : originalAnnotationSet)
        {
            if(originalAnnotation.annotationType().equals(ManagedBean.class))
            {
                this.annotations.put(Named.class, new NamedLiteral(((ManagedBean)originalAnnotation).name()));
            }
            else if(!originalAnnotation.annotationType().equals(jsf2ScopeAnnotation))
            {
                this.annotations.put(originalAnnotation.annotationType(), originalAnnotation);
            }
        }

        //TODO
        this.annotations.put(cdiScopeAnnotation, DefaultAnnotation.of(cdiScopeAnnotation));

        this.annotationSet = new HashSet<Annotation>(this.annotations.size());
        this.annotationSet.addAll(this.annotations.values());
    }

    /**
     * {@inheritDoc}
     */
    public Class getJavaClass()
    {
        return wrapped.getJavaClass();
    }

    /**
     * {@inheritDoc}
     */
    public Set getConstructors()
    {
        return wrapped.getConstructors();
    }

    /**
     * {@inheritDoc}
     */
    public Set getMethods()
    {
        return wrapped.getMethods();
    }

    /**
     * {@inheritDoc}
     */
    public Set getFields()
    {
        return wrapped.getFields();
    }

    /**
     * {@inheritDoc}
     */
    public Type getBaseType()
    {
        return wrapped.getBaseType();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Type> getTypeClosure()
    {
        return wrapped.getTypeClosure();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Annotation> T getAnnotation(Class<T> targetClass)
    {
        return (T)this.annotations.get(targetClass);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Annotation> getAnnotations()
    {
        return this.annotationSet;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> targetClass)
    {
        return this.annotations.containsKey(targetClass);
    }
}
