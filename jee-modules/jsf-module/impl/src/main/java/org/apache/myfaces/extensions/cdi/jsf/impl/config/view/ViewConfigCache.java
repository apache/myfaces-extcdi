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
package org.apache.myfaces.extensions.cdi.jsf.impl.config.view;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigExtractor;

import org.apache.myfaces.extensions.cdi.core.api.config.view.DefaultErrorView;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.*;

import javax.enterprise.inject.Typed;

/**
 * TODO move exceptions to util class
 *
 * @author Gerhard Petracek
 */
@Typed()
public class ViewConfigCache
{
    //we don't need a ConcurrentHashMap - write access is only allowed during the startup (by one thread)

    private static Map<ClassLoader, Map<String, ViewConfigEntry>>
            viewIdToViewDefinitionEntryMapping
            = new HashMap<ClassLoader, Map<String, ViewConfigEntry>>();

    private static Map<ClassLoader, Map<Class<? extends ViewConfig>, ViewConfigEntry>>
            viewDefinitionToViewDefinitionEntryMapping =
            new HashMap<ClassLoader, Map<Class<? extends ViewConfig>, ViewConfigEntry>>();

    private static Map<ClassLoader, List<InlineViewConfigEntry>>
            inlineViewDefinitionEntryList =
            new HashMap<ClassLoader, List<InlineViewConfigEntry>>();

    private static Map<ClassLoader, ViewConfigEntry>
            defaultErrorView =
            new HashMap<ClassLoader, ViewConfigEntry>();

    private static Map<ClassLoader, Class>
            inlineViewConfigRootMarker =
            new HashMap<ClassLoader, Class>();

    private static Map<ClassLoader, Boolean>
            lazyInitAllowed =
            new HashMap<ClassLoader, Boolean>();

    private ViewConfigCache()
    {
    }

    static void activateWriteMode()
    {
        setLazyInit(false);
    }

    static void deactivateWriteMode()
    {
        setLazyInit(true);
    }

    private static void setLazyInit(boolean newValue)
    {
        lazyInitAllowed.put(getClassloader(), newValue);
    }

    static void addViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry)
    {
        storeViewDefinition(viewId, viewDefinitionEntry, false);
    }

    static void replaceViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry)
    {
        storeViewDefinition(viewId, viewDefinitionEntry, true);
    }

    public static ViewConfigEntry getViewDefinition(String viewId)
    {
        return getViewIdToViewDefinitionEntryMapping(true).get(viewId);
    }

    public static Collection<ViewConfigEntry> getViewConfigEntries()
    {
        Map<String, ViewConfigEntry> entryMap = getViewIdToViewDefinitionEntryMapping(true);

        return entryMap.values();
    }

    public static ViewConfigEntry getViewDefinition(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return getViewDefinitionToViewDefinitionEntryMapping(true).get(viewDefinitionClass);
    }

    public static ViewConfigEntry getDefaultErrorView()
    {
        lazyInlineViewConfigCompilation();
        return defaultErrorView.get(getClassloader());
    }

    /**
     * resets the whole cache - e.g. needed for junit tests
     */
    public static void reset()
    {
        getViewIdToViewDefinitionEntryMapping(false).clear();
        getViewDefinitionToViewDefinitionEntryMapping(false).clear();
        getInlineViewDefinitionToViewDefinitionEntryList().clear();

        defaultErrorView.put(getClassloader(), null);
        inlineViewConfigRootMarker.put(getClassloader(), null);
    }

    static void queueInlineViewConfig(ViewConfigExtractor viewConfigExtractor, Class<? extends ViewConfig> beanClass)
    {
        getInlineViewDefinitionToViewDefinitionEntryList()
                .add(new InlineViewConfigEntry(viewConfigExtractor, beanClass));
    }

    static void setInlineViewConfigRootMarker(Class viewConfigRootClass)
    {
        Class storedPageClass = getInlineViewConfigRootMarker();

        if(storedPageClass != null)
        {
            if(!storedPageClass.equals(viewConfigRootClass))
            {
                throw ambiguousViewConfigRootException(storedPageClass, viewConfigRootClass);
            }
        }
        else
        {
            inlineViewConfigRootMarker.put(getClassloader(), viewConfigRootClass);
        }
    }

    static Class getInlineViewConfigRootMarker()
    {
        return inlineViewConfigRootMarker.get(getClassloader());
    }

    static void storeViewDefinition(String viewId,
                                    ViewConfigEntry viewDefinitionEntry,
                                    boolean allowReplace,
                                    boolean lazyInit)
    {
        if(getViewIdToViewDefinitionEntryMapping(lazyInit).containsKey(viewId) && !allowReplace)
        {
            throw ambiguousViewDefinitionException(
                    viewId,
                    viewDefinitionEntry.getViewDefinitionClass(),
                    getViewIdToViewDefinitionEntryMapping(lazyInit).get(viewId).getViewDefinitionClass());
        }

        tryToStorePageAsDefaultErrorPage(viewDefinitionEntry);

        getViewIdToViewDefinitionEntryMapping(lazyInit).put(viewId, viewDefinitionEntry);
        getViewDefinitionToViewDefinitionEntryMapping(lazyInit)
                .put(viewDefinitionEntry.getViewDefinitionClass(), viewDefinitionEntry);
    }

    static void storeViewDefinition(String viewId, ViewConfigEntry viewDefinitionEntry, boolean allowReplace)
    {
        storeViewDefinition(viewId, viewDefinitionEntry, allowReplace, true);
    }

    private static Map<String, ViewConfigEntry> getViewIdToViewDefinitionEntryMapping(boolean lazyInit)
    {
        if(lazyInit)
        {
            lazyInlineViewConfigCompilation();
        }

        Map<String, ViewConfigEntry> result = viewIdToViewDefinitionEntryMapping.get(getClassloader());

        if(result == null)
        {
            result = new HashMap<String, ViewConfigEntry>();
            viewIdToViewDefinitionEntryMapping.put(getClassloader(), result);
        }

        return result;
    }

    private static Map<Class<? extends ViewConfig>, ViewConfigEntry>
        getViewDefinitionToViewDefinitionEntryMapping(boolean lazyInit)
    {
        if(lazyInit)
        {
            lazyInlineViewConfigCompilation();
        }

        Map<Class<? extends ViewConfig>, ViewConfigEntry> result =
                viewDefinitionToViewDefinitionEntryMapping.get(getClassloader());

        if(result == null)
        {
            result = new HashMap<Class<? extends ViewConfig>, ViewConfigEntry>();
            viewDefinitionToViewDefinitionEntryMapping.put(getClassloader(), result);
        }
        return result;
    }

    private static List<InlineViewConfigEntry> getInlineViewDefinitionToViewDefinitionEntryList()
    {
        List<InlineViewConfigEntry> inlineViewConfigEntryList = inlineViewDefinitionEntryList.get(getClassloader());

        if(inlineViewConfigEntryList == null)
        {
            inlineViewConfigEntryList = new ArrayList<InlineViewConfigEntry>();
            inlineViewDefinitionEntryList.put(getClassloader(), inlineViewConfigEntryList);
        }
        return inlineViewConfigEntryList;
    }

    private static void tryToStorePageAsDefaultErrorPage(ViewConfigEntry viewDefinitionEntry)
    {
        if(DefaultErrorView.class.isAssignableFrom(viewDefinitionEntry.getViewDefinitionClass()))
        {
            ViewConfigEntry currentErrorView = getDefaultErrorView();
            if(currentErrorView != null)
            {
                throw ambiguousDefaultErrorViewDefinitionException(viewDefinitionEntry.getViewDefinitionClass(),
                                                                   currentErrorView.getViewDefinitionClass());
            }

            setDefaultErrorView(viewDefinitionEntry);
        }
    }

    private static void setDefaultErrorView(ViewConfigEntry viewDefinitionEntry)
    {
        //TODO
        defaultErrorView.put(getClassloader(), viewDefinitionEntry);
    }

    private static ClassLoader getClassloader()
    {
        return ClassUtils.getClassLoader(null);
    }

    private static void lazyInlineViewConfigCompilation()
    {
        List<InlineViewConfigEntry> inlineViewConfigEntryList =
                inlineViewDefinitionEntryList.get(getClassloader());

        if(inlineViewConfigEntryList == null)
        {
            //there is no inline view config or it is already processed
            return;
        }

        if(isInWriteMode())
        {
            return;
        }

        registerInlineViewConfigEntry();
    }

    private static boolean isInWriteMode()
    {
        return !Boolean.TRUE.equals(lazyInitAllowed.get(getClassloader()));
    }

    private synchronized static void registerInlineViewConfigEntry()
    {
        List<InlineViewConfigEntry> inlineViewConfigEntryList =
                inlineViewDefinitionEntryList.get(getClassloader());

        // switch into paranoia mode
        if(inlineViewConfigEntryList == null)
        {
            return;
        }

        ViewConfigEntry viewConfigEntry;
        for(InlineViewConfigEntry inlineViewConfigEntry : inlineViewConfigEntryList)
        {
            viewConfigEntry = inlineViewConfigEntry.getViewConfigExtractor()
                    .extractInlineViewConfig(inlineViewConfigEntry.getViewConfigDefinition());

            if(viewConfigEntry != null)
            {
                //activate view controller annotations
                viewConfigEntry.addPageBean(viewConfigEntry.getViewDefinitionClass());

                storeViewDefinition(viewConfigEntry.getViewId(), viewConfigEntry, false, false);
            }
        }

        inlineViewDefinitionEntryList.put(getClassloader(), null);
    }
}
