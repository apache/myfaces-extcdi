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
package org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config;

import org.apache.myfaces.extensions.cdi.core.api.config.ConfigEntry;
import org.apache.myfaces.extensions.cdi.core.api.config.AbstractAttributeAware;
import org.apache.myfaces.extensions.cdi.core.api.config.CodiConfig;

import javax.enterprise.context.ApplicationScoped;

/**
 * Configuration for CODI conversations - it's customizable via the @Alternative or @Specializes mechanism of CDI.
 *
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class ConversationConfig extends AbstractAttributeAware implements CodiConfig
{
    private static final long serialVersionUID = -1637900766842152725L;

    protected ConversationConfig()
    {
    }

    /**
     * Timeout for {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped} beans,
     * which will be used if the conversation doesn't get closed manually.
     * 
     * @return timeout in minutes
     */
    @ConfigEntry
    public int getConversationTimeoutInMinutes()
    {
        return 30;
    }

    /*
     * event config
     */

    /**
     * Specifies if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.ScopeBeanEvent}
     * will be fired.
     *
     * @return true if the event should be fired, false otherwise
     */
    @ConfigEntry
    public boolean isScopeBeanEventEnabled()
    {
        return false;
    }

    /**
     * Specifies if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.AccessBeanEvent}
     * will be fired.
     *
     * @return true if the event should be fired, false otherwise
     */
    @ConfigEntry
    public boolean isAccessBeanEventEnabled()
    {
        return false;
    }

    /**
     * Specifies if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.UnscopeBeanEvent}
     * will be fired.
     *
     * @return true if the event should be fired, false otherwise
     */
    @ConfigEntry
    public boolean isUnscopeBeanEventEnabled()
    {
        return false;
    }

    /**
     * Specifies if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.StartConversationEvent}
     * will be fired.
     *
     * @return true if the event should be fired, false otherwise
     */
    @ConfigEntry
    public boolean isStartConversationEventEnabled()
    {
        return false;
    }

    /**
     * Specifies if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.CloseConversationEvent}
     * will be fired.
     *
     * @return true if the event should be fired, false otherwise
     */
    @ConfigEntry
    public boolean isCloseConversationEventEnabled()
    {
        return false;
    }

    /**
     * Specifies if the
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.RestartConversationEvent}
     * will be fired.
     *
     * @return true if the event should be fired, false otherwise
     */
    @ConfigEntry
    public boolean isRestartConversationEventEnabled()
    {
        return false;
    }
}
