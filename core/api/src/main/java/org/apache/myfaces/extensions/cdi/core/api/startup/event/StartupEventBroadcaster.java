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
package org.apache.myfaces.extensions.cdi.core.api.startup.event;

/**
 * Broadcasters which implement this interface don't use CDI concepts and have to be configured via the
 * {@link java.util.ServiceLoader} approach for broadcasting the startup of CODI which might happen before the CDI
 * container is up and running. E.g. add-ons like the controlled bootstrapping add-on can implement this interface
 * in order to force the bootstrapping of the CDI container before CODI continues with the startup process.
 * Further details are available at {@link org.apache.myfaces.extensions.cdi.core.api.startup.CodiStartupBroadcaster}
 *
 * @author Gerhard Petracek
 */
public interface StartupEventBroadcaster
{
    /**
     * Allows to trigger any custom mechanism during the bootstrapping process.
     */
    void broadcastStartup();
}
