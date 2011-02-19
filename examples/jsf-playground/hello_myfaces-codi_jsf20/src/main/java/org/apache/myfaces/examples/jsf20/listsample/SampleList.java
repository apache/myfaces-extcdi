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
package org.apache.myfaces.examples.jsf20.listsample;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * backing bean which holds the list of our SampleListEntry beans
 */
@ViewAccessScoped
@Named
public class SampleList implements Serializable
{
    private static final long serialVersionUID = 5655240534902984821L;

    @Inject
    private Logger logger;

    /**
     * This just logs a INFO for each bean creation
     */
    @PostConstruct
    public void init()
    {
        logger.info("SampleList bean got created");
    }
    
    public List<Integer> getEntryIds()
    {
        List<Integer> entries = new ArrayList<Integer>();
        entries.add(1);
        entries.add(2);
        entries.add(3);

        return entries;
    }
}
