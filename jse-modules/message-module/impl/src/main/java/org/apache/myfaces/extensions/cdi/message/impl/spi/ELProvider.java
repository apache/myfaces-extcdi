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
package org.apache.myfaces.extensions.cdi.message.impl.spi;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import java.io.Serializable;

/**
 * Decouples codi from an el-implementation which provides a simple-el-context e.g. like juel
 *
 * @author Gerhard Petracek
 */
public interface ELProvider extends Serializable
{
    /**
     * Creates an {@link ExpressionFactory}
     * @return a new expression-factory
     */
    ExpressionFactory createExpressionFactory();

    /**
     * Create a {@link SimpleELContext}
     * @param elResolver current el-resolver
     * @return a new simple-el-context
     */
    SimpleELContext createELContext(ELResolver elResolver);

    /**
     * Creates an {@link ELResolver}
     * @return a new el-resolver
     */
    ELResolver createELResolver();
}
