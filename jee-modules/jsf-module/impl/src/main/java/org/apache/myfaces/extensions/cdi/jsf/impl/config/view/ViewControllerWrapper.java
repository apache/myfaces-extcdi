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

import org.apache.myfaces.extensions.cdi.core.api.config.view.View;

import javax.enterprise.inject.spi.AnnotatedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
class ViewControllerWrapper implements AnnotatedType<Object>
{
    private final AnnotatedType wrapped;
    private Map<Class<? extends Annotation>, Annotation> annotations;
    private Set<Annotation> annotationSet; //TODO

    public ViewControllerWrapper(AnnotatedType wrapped)
    {
        this.wrapped = wrapped;
        Set<Annotation> originalAnnotationSet = wrapped.getAnnotations();

        this.annotations = new HashMap<Class<? extends Annotation>, Annotation>(originalAnnotationSet.size());

        for(Annotation originalAnnotation : originalAnnotationSet)
        {
            if(!(originalAnnotation instanceof View))
            {
                this.annotations.put(originalAnnotation.annotationType(), originalAnnotation);
            }
        }
        this.annotationSet = new HashSet<Annotation>(this.annotations.size());
        this.annotationSet.addAll(this.annotations.values());
    }

    public Class getJavaClass()
    {
        return wrapped.getJavaClass();
    }

    public Set getConstructors()
    {
        return wrapped.getConstructors();
    }

    public Set getMethods()
    {
        return wrapped.getMethods();
    }

    public Set getFields()
    {
        return wrapped.getFields();
    }

    public Type getBaseType()
    {
        return wrapped.getBaseType();
    }

    public Set<Type> getTypeClosure()
    {
        return wrapped.getTypeClosure();
    }

    public <T extends Annotation> T getAnnotation(Class<T> targetClass)
    {
        return (T)this.annotations.get(targetClass);
    }

    public Set<Annotation> getAnnotations()
    {
        return this.annotationSet;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> targetClass)
    {
        return this.annotations.containsKey(targetClass);
    }
}
