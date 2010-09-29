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
package org.apache.myfaces.extensions.cdi.core.impl;

import org.apache.myfaces.extensions.cdi.core.api.CodiInformation;
import org.apache.myfaces.extensions.cdi.core.api.Deactivatable;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import java.util.logging.Logger;

/**
 * @author Gerhard Petracek
 */
public class CodiInformationExtension implements Extension, Deactivatable
{
    protected final Logger logger = Logger.getLogger(CodiInformationExtension.class.getName());

    @SuppressWarnings({"UnusedDeclaration"})
    public void setBeanManager(@Observes AfterDeploymentValidation afterDeploymentValidation)
    {
        if(!isActivated())
        {
            return;
        }

        if(CodiInformation.VERSION != null && !CodiInformation.VERSION.startsWith("null"))
        {
            this.logger.info("starting up MyFaces CODI (Extensions CDI) v" + CodiInformation.VERSION);
        }
        else
        {
            this.logger.info("starting up MyFaces CODI (Extensions CDI)");
        }
    }

    public boolean isActivated()
    {
        return ClassUtils.isClassActivated(getClass());
    }
}