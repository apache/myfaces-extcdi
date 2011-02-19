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

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PostRenderView;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PreRenderView;
import org.apache.myfaces.extensions.cdi.core.api.config.view.View;
import org.apache.myfaces.examples.codi.jsf12.view.DemoPages;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Gerhard Petracek
 */
@View(DemoPages.HelloMyFacesCodi3.class)
@Named
@ConversationScoped
public class PageBean3 implements Serializable
{
    private static final long serialVersionUID = -7695924054621194150L;

    @Inject
    private Conversation conversation;

    private String value = "";

    private boolean beanAccessed = false;

    @PreRenderView
    protected void preRenderView()
    {
        this.value += "preRenderView() called";
        this.beanAccessed = true;
    }

    @PostRenderView
    protected void postRenderView()
    {
        if(this.beanAccessed)
        {
            this.conversation.close();
        }
    }

    public String getValue()
    {
        return value;
    }
}
