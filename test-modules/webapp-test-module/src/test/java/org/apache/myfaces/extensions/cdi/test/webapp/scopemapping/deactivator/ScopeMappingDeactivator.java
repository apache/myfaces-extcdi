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
package org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.deactivator;

import org.apache.myfaces.extensions.cdi.core.api.AbstractClassDeactivator;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

/**
 * Deactivates the MappedJsf2ScopeExtension.
 *
 * This ClassDeactivator is set via env-entry in the related web.xml file.
 *
 * @author Jakob Korherr
 */
public class ScopeMappingDeactivator extends AbstractClassDeactivator
{

    @Override
    protected void deactivateClasses()
    {
        addDeactivatedClass(_getScopeMappingExtensionClass());
    }

    private Class<?> _getScopeMappingExtensionClass()
    {
        try
        {
            return ClassUtils.loadClassForName("org.apache.myfaces.extensions.cdi.jsf2.impl.scope.mapped.MappedJsf2ScopeExtension");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Could not find MappedJsf2ScopeExtension in classpath", e);
        }
    }

}
