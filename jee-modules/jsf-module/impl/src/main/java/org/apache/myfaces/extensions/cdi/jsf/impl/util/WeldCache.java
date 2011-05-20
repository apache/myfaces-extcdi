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
package org.apache.myfaces.extensions.cdi.jsf.impl.util;

import javax.enterprise.inject.spi.Bean;

/**
 * This class is needed just for a workaround for a Weld v1.1.1 bug (see EXTCDI-191)
 * @author Gerhard Petracek
 */
//TODO
public class WeldCache
{
    private static ThreadLocal<Bean<?>> currentBean = new ThreadLocal<Bean<?>>();

    /**
     * @param bean currently active bean which can't be passed to the api
     */
    public static void setBean(Bean<?> bean)
    {
        currentBean.set(bean);
    }

    /**
     * @return the currently processed bean
     */
    static Bean<?> getBean()
    {
        return currentBean.get();
    }

    /**
     * resets the currently processed bean
     */
    public static void resetBean()
    {
        currentBean.set(null);
        currentBean.remove();
    }
}
