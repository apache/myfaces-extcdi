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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.core.api.tools.annotate.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationGroup;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Any;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
class DefaultConversationKey implements ConversationKey
{
    private static final long serialVersionUID = 3577945095460042939L;

    private static final String INVALID_WINDOW_SCOPE_DEFINITION =
            ": It isn't allowed to use qualifiers in combination with " + WindowScoped.class.getName();

    private static final String INVALID_VIEW_ACCESS_SCOPE_DEFINITION =
            ": It isn't allowed to use qualifiers in combination with " + WindowScoped.class.getName();

    private final Class<?> groupKey;
    private final Set<Annotation> qualifiers = new HashSet<Annotation>();

    //TODO remove as soon as the new version is tested
    //old version
    //workaround
    //private static final ViewAccessScoped VIEW_ACCESS_SCOPED = DefaultAnnotation.of(ViewAccessScoped.class);
    private static final Default DEFAULT_QUALIFIER = DefaultAnnotation.of(Default.class);

    //workaround
    private boolean viewAccessScopedAnnotationPresent;

    DefaultConversationKey(Class<?> groupKey, Annotation... qualifiers)
    {
        this.groupKey = groupKey;

        //TODO maybe we have to add a real qualifier instead
        Class<? extends Annotation> annotationType;
        for(Annotation qualifier : qualifiers)
        {
            annotationType = qualifier.annotationType();

            if(ViewAccessScoped.class.isAssignableFrom(annotationType))
            {
                this.viewAccessScopedAnnotationPresent = true;
            }
            else if(Any.class.isAssignableFrom(annotationType) ||
                    Default.class.isAssignableFrom(annotationType) ||
                    Named.class.isAssignableFrom(annotationType) && "".equals(((Named)qualifier).value()) ||
                    ConversationGroup.class.isAssignableFrom(annotationType))
            {
                //won't be used for this key!
            }
            else
            {
                this.qualifiers.add(qualifier);
            }
        }

        //for easier manual usage of the WindowContextManager
        if(this.qualifiers.isEmpty())
        {
            this.qualifiers.add(DEFAULT_QUALIFIER);
        }

        validate();
    }

    private void validate()
    {
        boolean defaultQualifierUsed = isDefaultQualifier();

        if(isWindowScope() && !defaultQualifierUsed)
        {
            throw new IllegalStateException(this.groupKey.getName() + INVALID_WINDOW_SCOPE_DEFINITION);
        }

        if(isViewAccessScope() && !defaultQualifierUsed)
        {
            throw new IllegalStateException(this.groupKey.getName() + INVALID_VIEW_ACCESS_SCOPE_DEFINITION);
        }
    }

    boolean isViewAccessScopedAnnotationPresent()
    {
        return viewAccessScopedAnnotationPresent;
    }

    private boolean isWindowScope()
    {
        return WindowScoped.class.isAssignableFrom(this.groupKey.getClass());
    }

    @Deprecated
    private boolean isViewAccessScope()
    {
        return ViewAccessScoped.class.isAssignableFrom(this.groupKey.getClass());
    }

    private boolean isDefaultQualifier()
    {
        for(Annotation qualifier : this.qualifiers)
        {
            if(Default.class.isAssignableFrom(qualifier.getClass()))
            {
                return true;
            }
        }
        return false;
    }

    public Class<?> getConversationGroup()
    {
        return groupKey;
    }

    public Set<Annotation> getQualifiers()
    {
        return Collections.unmodifiableSet(this.qualifiers);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DefaultConversationKey))
        {
            return false;
        }

        DefaultConversationKey that = (DefaultConversationKey) o;

        if (!groupKey.equals(that.groupKey))
        {
            return false;
        }
        if (!qualifiers.equals(that.qualifiers))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = groupKey.hashCode();
        result = 31 * result + qualifiers.hashCode();
        return result;
    }
}
