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
package org.apache.myfaces.extensions.cdi.jsf.impl.message;

import org.apache.myfaces.extensions.cdi.message.api.LocaleResolver;

import javax.faces.context.FacesContext;
import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public class JsfAwareLocaleResolver implements LocaleResolver
{
    private static final long serialVersionUID = 5945811297524654438L;

    public Locale getLocale()
    {
        Locale locale = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null && facesContext.getViewRoot() != null)
        {
            locale = facesContext.getViewRoot().getLocale();
        }
        return locale != null ? locale : Locale.getDefault();
    }
}
