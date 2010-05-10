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
package org.apache.myfaces.blank.message.advanced;

import org.apache.myfaces.extensions.cdi.message.impl.spi.ELProvider;
import org.apache.myfaces.extensions.cdi.message.impl.spi.SimpleELContext;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleResolver;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;

/**
 * Provides the implementations which allow to use el-expressions in messages
 *
 * @author Gerhard Petracek
 */
public class DefaultELProvider implements ELProvider
{
    public ExpressionFactory createExpressionFactory()
    {
        return new ExpressionFactoryImpl();
    }

    public SimpleELContext createELContext(ELResolver elResolver)
    {
        return new DefaultELContext(elResolver);
    }

    public ELResolver createELResolver()
    {
        return new SimpleResolver(true);
    }
}
