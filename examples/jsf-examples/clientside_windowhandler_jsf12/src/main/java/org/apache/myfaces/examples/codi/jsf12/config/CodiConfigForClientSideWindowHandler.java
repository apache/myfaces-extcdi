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
package org.apache.myfaces.examples.codi.jsf12.config;

import org.apache.myfaces.extensions.cdi.jsf.impl.config.DefaultWindowContextConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * You also have to activate
 * {@link org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.ClientSideWindowHandler}
 * in beans.xml
 * 
 * @author Gerhard Petracek
 */
@ApplicationScoped
@Alternative
public class CodiConfigForClientSideWindowHandler extends DefaultWindowContextConfig
{
    private static final long serialVersionUID = -4094949435815088068L;

    @Override
    public boolean isInitialRedirectEnabled()
    {
        return false;
    }

    @Override
    public boolean isAddWindowIdToActionUrlsEnabled()
    {
        return false;
    }
}
