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
package org.apache.myfaces.extensions.cdi.core.api.tools;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;

import java.util.Comparator;
import java.io.Serializable;

/**
 * {@link Comparator} which allows to sort artifacts based on {@link InvocationOrder}
 */
public class InvocationOrderComparator<T> implements Comparator<T>, Serializable
{
    private static final long serialVersionUID = -7492852803631628400L;

    /**
     * {@inheritDoc}
     */
    public int compare(T t1, T t2)
    {
        Class<?> t1Class;
        Class<?> t2Class;

        if(t1 instanceof Class)
        {
            t1Class = (Class)t1;
        }
        else
        {
            t1Class = t1.getClass();
        }

        if(t2 instanceof Class)
        {
            t2Class = (Class)t2;
        }
        else
        {
            t2Class = t2.getClass();
        }

        if (hasPriority(t1Class) && hasPriority(t2Class))
        {
            return isPriorityHigher(t1Class.getAnnotation(InvocationOrder.class),
                    t2Class.getAnnotation(InvocationOrder.class));
        }
        if (!hasPriority(t1Class) && !hasPriority(t2Class))
        {
            return 0;
        }
        return hasPriority(t1Class) ? -1 : 1;
    }

    private int isPriorityHigher(InvocationOrder priority1, InvocationOrder priority2)
    {
        if (priority1.value() == priority2.value())
        {
            return 0;
        }

        return priority1.value() < priority2.value() ? -1 : 1;
    }

    private boolean hasPriority(Class o)
    {
        return o.isAnnotationPresent(InvocationOrder.class);
    }
}
