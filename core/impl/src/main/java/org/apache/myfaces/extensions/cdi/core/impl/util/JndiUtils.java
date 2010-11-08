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
package org.apache.myfaces.extensions.cdi.core.impl.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the internal helper class for low level access to JNDI
 */
public final class JndiUtils
{

    private final static Logger log = Logger.getLogger(JndiUtils.class.getName());

    private static InitialContext initialContext = null;

    static
    {
        try
        {
            initialContext = new InitialContext();
        }
        catch (Exception e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    private JndiUtils()
    {
        // prevent instantiation
    }

    public static InitialContext getInitialContext()
    {
        return initialContext;
    }

    public static void bind(String name, Object object)
    {
        try
        {
            Context context = initialContext;

            String[] parts = name.split("/");

            for(int i = 0; i < parts.length - 1; i++)
            {
                try
                {
                    context = (Context)initialContext.lookup(parts[i]);
                }
                catch(NameNotFoundException e)
                {
                    context = initialContext.createSubcontext(parts[i]);
                }
            }

            context.bind(parts[parts.length - 1], object);
        }
        catch (NamingException e)
        {
            throw new RuntimeException("Could not bind " + name + " to JNDI", e);
        }
    }

    public static void unbind(String name)
    {
        try
        {
            initialContext.unbind(name);

        }
        catch (NamingException e)
        {
            throw new RuntimeException("Could not unbind " + name + " from JNDI", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookup(String name, Class<? extends T> expectedClass)
    {
        try
        {
            Object lookedUp = initialContext.lookup(name);

            if (lookedUp != null)
            {
                if (expectedClass.isAssignableFrom(lookedUp.getClass()))
                {
                    // we have a value and the type fits
                    return (T) lookedUp;
                }
                else
                {
                    // we have a value, but the value does not fit
                    log.log(Level.SEVERE,
                            "JNDI lookup for key " + name
                            + " should return a value of " + expectedClass);
                }
            }

            return null;
        }
        catch (NamingException e)
        {
            throw new RuntimeException("Could not get " + name + " from JNDI", e);
        }
    }

}
