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

/**
 * This class holds information about the last used RestParameters for a given window.
 */
// this could be @WindowScoped, but due to a bug in OpenWebBeans 1.1.1 and below, we get passivation errors
public abstract class RestParameters
{
    /**
     * Check and update the view parameters of the given viewId.
     *
     *
     * @return <code>true</code> if the viewParameters are now different than at the last invocation.
     *         In this default implementation it always returns false!
     */
    public abstract boolean checkForNewViewParameters();

    /**
     * This method will get called to reset the stored viewParameters
     */
    public abstract void reset();
}