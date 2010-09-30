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
package org.apache.myfaces.extensions.cdi.test.webapp.scopemapping.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Jakob Korherr
 */
@Named
@RequestScoped
public class CdiBean
{

    @Inject
    private JsfApplicationScopedBean jsfApplicationScopedBean;

    @Inject
    private JsfSessionScopedBean jsfSessionScopedBean;

    @Inject
    private JsfRequestScopedBean jsfRequestScopedBean;

    @Inject
    private JsfViewScopedBean jsfViewScopedBean;

    public JsfApplicationScopedBean getJsfApplicationScopedBean()
    {
        return jsfApplicationScopedBean;
    }

    public JsfSessionScopedBean getJsfSessionScopedBean()
    {
        return jsfSessionScopedBean;
    }

    public JsfRequestScopedBean getJsfRequestScopedBean()
    {
        return jsfRequestScopedBean;
    }

    public JsfViewScopedBean getJsfViewScopedBean()
    {
        return jsfViewScopedBean;
    }
}
