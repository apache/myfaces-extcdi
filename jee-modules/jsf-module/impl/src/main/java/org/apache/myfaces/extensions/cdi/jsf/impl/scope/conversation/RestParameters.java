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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import javax.enterprise.context.Dependent;

/**
 * This class holds information about the last used RestParameters for a given window.
 */
@Dependent // the final implementation might be RequestScoped
public abstract class RestParameters
{
    /**
     * Check the view parameters of the given viewId.
     * The restId must also contain the viewId itself!
     * The restId must be different if either the view or
     * any of it's parameters get changed.
     *
     * @return a key which uniquely identifies the view and all it's GET parameters
     */
    public abstract String getRestId();

    /**
     * @return <code>true</code> if the current request is a POST request.
     */
    public abstract boolean isPostback();

}
