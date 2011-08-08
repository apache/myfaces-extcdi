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
package org.apache.myfaces.extensions.cdi.core.test.impl.resource.bundle;

import org.apache.myfaces.extensions.cdi.core.api.resource.bundle.BundleKey;
import org.apache.myfaces.extensions.cdi.core.api.resource.bundle.BundleValue;

import javax.enterprise.inject.Typed;
import javax.inject.Named;

@Typed()
public interface Testbundle
{
    public static class MyValue implements Testbundle, BundleKey {}

    @Named("my.value")
    public static class MyValue1 implements Testbundle, BundleKey {}

    public static class MyValue2 extends BundleValue implements Testbundle {}

    @Named("my.value2")
    public static class MyValue3 extends BundleValue implements Testbundle {}
}
