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
package org.apache.myfaces.extensions.cdi.core.test.impl.logging;

import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.*;

public class SimpleLoggingTest
{
    @Test
    public void testSerialization()
    {
        ManualLoggingClient loggingClient = new ManualLoggingClient();

        loggingClient.logMessage();

        byte[] serialized = new byte[0];
        try
        {
            serialized = serializeObject(loggingClient);
        }
        catch (IOException e)
        {
            fail("failed to serialize instance of " + Logger.class.getName(), e);
        }

        loggingClient = null;
        assertEquals(loggingClient, null);
        assertTrue(serialized.length > 0);

        try
        {
            loggingClient = deserializeData(serialized);
        }
        catch (Exception e)
        {
            fail("failed to deserialize instance of " + Logger.class.getName(), e);
        }

        loggingClient.logMessage();

        assertTrue(loggingClient.getLogger().getWrapped() instanceof java.util.logging.Logger);
    }

    private byte[] serializeObject(Object object) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private ManualLoggingClient deserializeData(byte[] serialized)
            throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        try
        {
            return (ManualLoggingClient)objectInputStream.readObject();
        }
        finally
        {
            byteArrayInputStream.close();
        }
    }
}
