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
package org.apache.myfaces.extensions.cdi.core.api;

/**
 * Exception wrapper for checked exception for throwing them as {@link RuntimeException} and processing them
 * on a different level of the call-stack.
 */
public class UnhandledException extends RuntimeException
{
    private static final long serialVersionUID = -2218238182931072197L;

    /**
     * @param message message for the exception
     */
    public UnhandledException(String message)
    {
        super(message);
    }

    /**
     * @param cause exception which should be wrapped in an {@link RuntimeException}
     */
    public UnhandledException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message message for the exception
     * @param cause exception which should be wrapped in an {@link RuntimeException}
     */
    public UnhandledException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
