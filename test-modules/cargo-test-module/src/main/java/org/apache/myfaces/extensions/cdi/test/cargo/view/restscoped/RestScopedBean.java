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
package org.apache.myfaces.extensions.cdi.test.cargo.view.restscoped;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.RestScoped;

import javax.annotation.PostConstruct;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Named;
import java.io.Serializable;

/**
 * This sample &#064;RequestScoped page just increments an invocation counter,
 * which starts at zero if a new instance got created.
 *
 * The {@link #param} parameter gets set via f:viewParam and defines the Rest parameter.
 */
@Named("restscoped")
@RestScoped
public class RestScopedBean implements Serializable
{
    private static final long serialVersionUID = -3165721387186644462L;

    private int invocationCount;
    private String param;


    @PostConstruct
    protected void init()
    {
        invocationCount = 0;
    }

    public void preRender(ComponentSystemEvent ev)
    {
        invocationCount++;
    }

    public int getInvocationCount()
    {
        return invocationCount;
    }

    public String getParam()
    {
        return param;
    }

    public void setParam(String param)
    {
        this.param = param;
    }
}
