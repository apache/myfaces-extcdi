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
package org.apache.myfaces.extensions.cdi.jsf2.impl.component;

import org.apache.myfaces.extensions.cdi.jsf2.impl.component.spi.TemporaryUIViewRoot;

import javax.faces.component.UIViewRoot;

//needed due to EXTCDI-167
/**
 * @author Gerhard Petracek
 */
public class DefaultTemporaryUIViewRoot extends UIViewRoot implements TemporaryUIViewRoot
{
    private static final long serialVersionUID = -1800819266253071047L;

    private boolean activateTemporaryMode;

    /**
     * {@inheritDoc}
     */
    public void setTemporaryMode(boolean activateTemporaryMode)
    {
        this.activateTemporaryMode = activateTemporaryMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        //noinspection SimplifiableIfStatement
        if(this.activateTemporaryMode)
        {
            //workaround for PreDestroyViewMapEvent which would be caused by the security check
            return true;
        }
        return super.equals(obj);
    }

    //needed due to checkstyle rules
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}