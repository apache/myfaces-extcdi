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
package org.apache.myfaces.extensions.cdi.jsf.test.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;

/**
 * view-config
 */
abstract class SimpleNestedViewConfig implements ViewConfig
{
    @Page
    public class Page1 implements ViewConfig
    {
    }

    @Page
    public class Page2 extends SimpleNestedViewConfig
    {
    }

    public class SubFolder extends SimpleNestedViewConfig
    {
        @Page(name = "customPage3")
        public class Page3 extends SimpleNestedViewConfig
        {
        }

        @Page(basePath = "my", name = "customPage4")
        public class Page4 extends SimpleNestedViewConfig
        {
        }
    }

    @Page(basePath = "subFolder") //overridden
    public class SubFolder2 extends SimpleNestedViewConfig
    {
        @Page(basePath = "/", name = "customPage5")
        public class Page5 extends SimpleNestedViewConfig
        {
        }

        @Page(basePath = "my", name = "customPage6")
        public class Page6 extends SimpleNestedViewConfig
        {
        }
    }
}