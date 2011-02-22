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
package org.apache.myfaces.extensions.cdi.message.impl.formatter;

/**
 * @author Gerhard Petracek
 */
public interface NumberFormatterConfigKeys
{
    String GROUPING_SEPARATOR_KEY = "grouping_separator";

    String DECIMAL_SEPARATOR_KEY = "decimal_separator";

    String MINIMUM_FRACTION_DIGITS_KEY = "minimum_fraction_digits";

    String MINIMUM_INTEGER_DIGITS_KEY = "minimum_integer_digits";

    String MAXIMUM_FRACTION_DIGITS = "maximum_fraction_digits";
    
    String MAXIMUM_INTEGER_DIGITS = "maximum_integer_digits";
    
    String EXPONENT_SEPARATOR = "exponent_separator";
}