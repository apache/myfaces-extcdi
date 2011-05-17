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
package org.apache.myfaces.extensions.cdi.jsf.test.alternative.config;

import org.apache.myfaces.extensions.cdi.jsf.alternative.config.AlternativeJsfModuleConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.config.JsfModuleConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class JsfModuleConfigTest
{
    @Test
    public void testAlternativeCodiCoreConfig()
    {
        JsfModuleConfig jsfModuleConfig = new AlternativeJsfModuleConfig();

        assertEquals(jsfModuleConfig.isAlwaysKeepMessages(), true);
        assertEquals(jsfModuleConfig.isInitialRedirectEnabled(), true);
        assertEquals(jsfModuleConfig.isInvalidValueAwareMessageInterpolatorEnabled(), true);
        assertEquals(jsfModuleConfig.isUseViewConfigsAsNavigationCasesEnabled(), true);
    }
}
