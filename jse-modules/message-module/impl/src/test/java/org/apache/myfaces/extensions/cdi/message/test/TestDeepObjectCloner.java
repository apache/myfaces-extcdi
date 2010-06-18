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
package org.apache.myfaces.extensions.cdi.message.test;

import java.io.*;

/**
 * @author Gerhard Petracek
 */
class TestDeepObjectCloner
{
    public static <T> T clone(Serializable source, Class<T> targetClass)
    {
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try
        {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(arrayOutputStream);
            outputStream.writeObject(source);
            outputStream.flush();
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
            inputStream = new ObjectInputStream(arrayInputStream);
            return (T) inputStream.readObject();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("you provided an implementation which isn't serializable or" +
                    "implemented as anonymous class" + e);
        }
        finally
        {
            try
            {
                if(inputStream != null)
                {
                    inputStream.close();
                }

                if(outputStream != null)
                {
                    outputStream.close();
                }
            }
            catch (IOException e)
            {
                //noinspection ThrowFromFinallyBlock
                throw new RuntimeException(e);
            }
        }
    }
}
