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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationKey;
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

    private Class<? extends Annotation> scopeType;

    private Class<?> groupKey;

    //HashSet due to Serializable warning in checkstyle rules
    private HashSet<Annotation> qualifiers;

    DefaultConversationKey(Class<? extends Annotation> scopeType,
                           Class<?> groupKey,
                           Annotation... qualifiers)
    {
        this.scopeType = scopeType;
        this.groupKey = groupKey;

        //TODO maybe we have to add a real qualifier instead
        Class<? extends Annotation> annotationType;
        for(Annotation qualifier : qualifiers)
        {
            annotationType = qualifier.annotationType();

            if(Any.class.isAssignableFrom(annotationType) ||
                    Default.class.isAssignableFrom(annotationType) ||
                    Named.class.isAssignableFrom(annotationType)   ||
                    ConversationGroup.class.isAssignableFrom(annotationType))
            {
                //won't be used for this key!
                continue;
            }

            if (this.qualifiers == null)
            {
                this.qualifiers = new HashSet<Annotation>();
            }
            this.qualifiers.add(qualifier);
        }
    }

    public Class<? extends Annotation> getScope()
    {
        return this.scopeType;
    }

    public Class<?> getConversationGroup()
    {
        return groupKey;
    }

    public Set<Annotation> getQualifiers()
    {
        if (qualifiers == null)
        {
            return Collections.emptySet();
        }
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
        if (!scopeType.equals(that.scopeType))
        {
            return false;
        }
        if (qualifiers == null && that.qualifiers == null)
        {
            return true;
        }
        if (qualifiers != null && that.qualifiers == null)
        {
            return false;
        }

        if (!that.qualifiers.equals(qualifiers))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = scopeType.hashCode();
        result = 31 * result + groupKey.hashCode();
        result = 31 * result + (qualifiers != null ? qualifiers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("conversation-key\n");

        result.append("\tscope:\t\t");
        result.append(this.scopeType.getName());

        result.append("\n");
        result.append("\tgroup:\t\t");
        result.append(this.groupKey.getName());

        result.append("\n");
        result.append("\tqualifiers:\t");

        if(qualifiers != null)
        {
            for(Annotation qualifier : this.qualifiers)
            {
                result.append(qualifier.annotationType().getName());
                result.append(" ");
            }
        }
        else
        {
            result.append("---");
        }

        return result.toString();
    }
}
