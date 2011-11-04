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

import java.io.Serializable;

/**
 * Allows to filter restricted arguments
 */
public interface ArgumentFilter extends Serializable
{
    /**
     * Allows to filter resolved argument values e.g. based on custom rules.
     * Return false to trigger {@link #getDefaultValue(String)}
     *
     * @param expressionBody the body of the expression used in the message
     * @param value the resolved value
     * @return true if the given value should be used - false otherwise
     */
    boolean isArgumentAllowed(String expressionBody, Object value);

    /**
     * Allows to customize the default value e.g. for restricted or unresolved message arguments
     *
     * @param expressionBody the body of the expression used in the message
     * @return the default value for the given expression
     */
    String getDefaultValue(String expressionBody);
}
