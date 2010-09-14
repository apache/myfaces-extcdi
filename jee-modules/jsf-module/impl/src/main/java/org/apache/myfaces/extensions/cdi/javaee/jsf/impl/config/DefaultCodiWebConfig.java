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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.config;

import org.apache.myfaces.extensions.cdi.javaee.jsf.api.config.CodiWebConfig12;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.api.ConfigParameter.TRANSACTION_TOKEN_ENABLED;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.api.ConfigParameter.TRANSACTION_TOKEN_ENABLED_DEFAULT;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultCodiWebConfig extends CodiWebConfig12
{
    private static final long serialVersionUID = 2195635039365964148L;

    private Boolean configInitialized;

    public boolean isTransactionTokenEnabled()
    {
        lazyInit();
        return getAttribute(TRANSACTION_TOKEN_ENABLED, Boolean.class);
    }

    private void lazyInit()
    {
        if(configInitialized == null)
        {
            init(FacesContext.getCurrentInstance());
        }
    }

    private synchronized void init(FacesContext facesContext)
    {
        if(configInitialized != null || facesContext == null)
        {
            return;
        }

        configInitialized = true;

        initTransactionTokenEnabled(facesContext);
    }

    private void initTransactionTokenEnabled(FacesContext facesContext)
    {
        boolean transactionTokenEnabled = TRANSACTION_TOKEN_ENABLED_DEFAULT;

        if("true".equalsIgnoreCase(facesContext.getExternalContext().getInitParameter(TRANSACTION_TOKEN_ENABLED)))
        {
            transactionTokenEnabled = true;
        }

        setAttribute(TRANSACTION_TOKEN_ENABLED, transactionTokenEnabled);
    }
}
