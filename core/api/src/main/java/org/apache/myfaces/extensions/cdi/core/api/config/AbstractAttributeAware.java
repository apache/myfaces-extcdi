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
package org.apache.myfaces.extensions.cdi.core.api.config;

import java.util.Map;
import java.util.HashMap;

/**
 * Base implementation which implements {@link org.apache.myfaces.extensions.cdi.core.api.config.AttributeAware}
 * to support custom attributes.
 *  
 * @author Gerhard Petracek
 */
public abstract class AbstractAttributeAware implements AttributeAware
{
    private static final long serialVersionUID = 7845412079015046108L;

    private Map<String, Object> configAttributes;

    /**
     * {@inheritDoc}
     */
    public boolean setAttribute(String name, Object value)
    {
        return setAttribute(name, value, true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setAttribute(String name, Object value, boolean forceOverride)
    {
        if(!forceOverride && containsAttribute(name))
        {
            return false;
        }
        this.getConfigAttributeMap().put(name, value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAttribute(String name)
    {
        return this.getConfigAttributeMap().containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getAttribute(String name, Class<T> targetType)
    {
        return (T)this.getConfigAttributeMap().get(name);
    }

    private Map<String, Object> getConfigAttributeMap()
    {
        if(this.configAttributes == null)
        {
            initConfig();
            this.configAttributes = new HashMap<String, Object>();
        }
        return this.configAttributes;
    }

    protected void initConfig()
    {
        //override if needed
    }
}
