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
package org.apache.myfaces.blank.conversation.grouped;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;

import javax.inject.Named;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gerhard Petracek
 */
@Named
@ConversationScoped(ConversationGroup1.class)
public class ConversationDemoBean1 implements Serializable
{
    private String value = "Hello grouped conversation1! ";
    private Date createdAt;
    private static final long serialVersionUID = -4238520498463300564L;

    @PostConstruct
    public void init()
    {
        this.createdAt = new Date();
    }

    public String getValue()
    {
        return value + createdAt.toLocaleString();
    }
}
