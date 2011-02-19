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
package org.apache.myfaces.examples.jsf20.conversation.viewaccess;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;


@Named("vaBean2")
@ViewAccessScoped
public class ViewAccessDemoBean2 implements Serializable
{
    private static final long serialVersionUID = -4238520498463300564L;

    private String value = "Hello view access scoped 2! ";
    private Date createdAt;
    private String viewParam;
    private String sampleField = null;


    @PostConstruct
    public void init()
    {
        this.createdAt = new Date();
        this.viewParam = "unchanged";
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getViewParam()
    {
        return viewParam;
    }

    public void setViewParam(String viewParam)
    {
        this.viewParam = viewParam;
    }

    public String getSampleField()
    {
        return sampleField;
    }

    public void setSampleField(String sampleField)
    {
        this.sampleField = sampleField;
    }

    public String toString()
    {
        return createdAt.toLocaleString() +
                " viewParam: " + viewParam +
                " sampleField: " + sampleField;
    }

    /** dummy action */
    public String update()
    {
        return null;
    }
}