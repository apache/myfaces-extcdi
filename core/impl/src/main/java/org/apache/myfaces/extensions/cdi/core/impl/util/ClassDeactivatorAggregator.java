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
package org.apache.myfaces.extensions.cdi.core.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.Aggregatable;
import org.apache.myfaces.extensions.cdi.core.api.ClassDeactivator;

import javax.enterprise.inject.Typed;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Gerhard Petracek
 */
@Typed()
class ClassDeactivatorAggregator implements Aggregatable<ClassDeactivator>, ClassDeactivator
{
    private static final long serialVersionUID = 5996031456559606240L;

    private Set<Class> deactivatedClasses = new HashSet<Class>();

    private String deactivators = "";

    public void add(ClassDeactivator classDeactivator)
    {
        this.deactivatedClasses.addAll(classDeactivator.getDeactivatedClasses());

        if(this.deactivators.length() > 0)
        {
            this.deactivators = this.deactivators + "\n" + classDeactivator.getClass().getName();
        }
        else
        {
            this.deactivators = classDeactivator.getClass().getName();
        }
    }

    public ClassDeactivator create()
    {
        return this;
    }

    public Set<Class> getDeactivatedClasses()
    {
        return this.deactivatedClasses;
    }

    @Override
    public String toString()
    {
        return this.deactivators;
    }
}
