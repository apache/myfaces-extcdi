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
package org.apache.myfaces.examples.codi.jsf12;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.CloseConversationGroup;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.examples.codi.jsf12.config.view.Pages;

import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewAccessScoped
public class HelloWorldBacking implements Serializable
{
    private static final long serialVersionUID = -3134047327542339708L;

    private String name;

    public Class<? extends ViewConfig> next()
    {
        return Pages.Page2.class;
    }

    public Class<? extends ViewConfig> back()
    {
        return Pages.Page1.class;
    }

    @CloseConversationGroup
    public Class<? extends ViewConfig> end()
    {
        return Pages.Page1.class;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
