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
package org.apache.myfaces.extensions.cdi.core.impl.logging;

import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;

import javax.enterprise.inject.Typed;

/**
 * Injectable logger factory
 */
@Typed()
class DefaultLoggerFactory implements Logger.Factory
{
    private static final long serialVersionUID = -4149574697548186019L;

    /**
     * {@inheritDoc}
     */
    public Logger getLogger(String s)
    {
        return new DefaultLogger(s);
    }

    /**
     * {@inheritDoc}
     */
    public Logger getLogger(String s, String s1)
    {
        return new DefaultLogger(s, s1, false);
    }

    /**
     * {@inheritDoc}
     */
    public Logger getAnonymousLogger()
    {
        return new DefaultLogger();
    }

    /**
     * {@inheritDoc}
     */
    public Logger getAnonymousLogger(String s)
    {
        return new DefaultLogger(null, s, true);
    }
}
