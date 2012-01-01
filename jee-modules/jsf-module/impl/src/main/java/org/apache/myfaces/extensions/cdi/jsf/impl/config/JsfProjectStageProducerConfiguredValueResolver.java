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
package org.apache.myfaces.extensions.cdi.jsf.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.InvocationOrder;
import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueDescriptor;
import org.apache.myfaces.extensions.cdi.core.impl.config.AbstractConfiguredValueResolver;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.myfaces.extensions.cdi.jsf.impl.projectstage.JsfProjectStageProducer;

import javax.enterprise.inject.Typed;
import java.util.Collections;
import java.util.List;

/**
 * {@link org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver}
 * which allows to overrule the default {@link ProjectStageProducer}.
 * (If a custom {@link ProjectStageProducer} gets returned by a custom
 * {@link org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver},
 * it will be ignored.)
 */
@Typed()
@InvocationOrder(500)
public class JsfProjectStageProducerConfiguredValueResolver extends AbstractConfiguredValueResolver
{
    public <K, T> List<T> resolveInstances(ConfiguredValueDescriptor<K, T> descriptor)
    {
        if(ProjectStageProducer.class.isAssignableFrom(descriptor.getTargetType()))
        {
            add(JsfProjectStageProducer.class);
            return getConfiguredValues(descriptor.getTargetType());
        }
        else
        {
            return Collections.emptyList();
        }
    }
}
