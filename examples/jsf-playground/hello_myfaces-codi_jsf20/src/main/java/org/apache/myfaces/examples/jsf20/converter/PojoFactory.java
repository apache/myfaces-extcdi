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
package org.apache.myfaces.examples.jsf20.converter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Global page bean
 */
@Singleton
@Named
public class PojoFactory
{

    private List<Pojo> pojos;

    public List<Pojo> getPojos()
    {
        if (pojos == null)
        {
            // some demo values
            pojos = new ArrayList<Pojo>();
            pojos.add(new Pojo(1, "Frank"));
            pojos.add(new Pojo(2, "Karl"));
            pojos.add(new Pojo(3, "Adam"));
        }

        return pojos;
    }

    public Pojo getPojo(String idString)
    {
        if (idString == null)
        {
            return null;
        }

        return getPojo(parseId(idString));
    }

    public Pojo getPojo(int id)
    {
        for (Pojo pojo : getPojos())
        {
            if (pojo.getId() == id)
            {
                return pojo;
            }
        }

        return null;
    }

    private int parseId(String idString) throws IllegalArgumentException
    {
        try
        {
            return Integer.parseInt(idString);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Illegal Pojo id: idString", e);
        }
    }

}
