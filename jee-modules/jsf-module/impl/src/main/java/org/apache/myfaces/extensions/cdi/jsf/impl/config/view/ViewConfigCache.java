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
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;
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

    private static Map<ClassLoader, Map<String, ViewConfigDescriptor>>
            viewIdToViewDefinitionEntryMapping
            = new HashMap<ClassLoader, Map<String, ViewConfigDescriptor>>();

    private static Map<ClassLoader, Map<Class<? extends ViewConfig>, ViewConfigDescriptor>>
            viewDefinitionToViewDefinitionEntryMapping =
            new HashMap<ClassLoader, Map<Class<? extends ViewConfig>, ViewConfigDescriptor>>();

    private static Map<ClassLoader, List<InlineViewConfigDescriptor>>
            inlineViewDefinitionEntryList =
            new HashMap<ClassLoader, List<InlineViewConfigDescriptor>>();

    private static Map<ClassLoader, ViewConfigDescriptor>
            defaultErrorView =
            new HashMap<ClassLoader, ViewConfigDescriptor>();

    private static Map<ClassLoader, Class>
            inlineViewConfigRootMarker =
            new HashMap<ClassLoader, Class>();

    private static volatile Map<ClassLoader, Boolean>
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

    static void addViewConfigDescriptor(String viewId, ViewConfigDescriptor viewConfigDescriptor)
    {
        storeViewConfigDescriptor(viewId, viewConfigDescriptor, false);
    }

    static void replaceViewConfigDescriptor(String viewId, ViewConfigDescriptor viewDefinitionEntry)
    {
        storeViewConfigDescriptor(viewId, viewDefinitionEntry, true);
    }

    /**
     * Resolves the {@link ViewConfigDescriptor} for the given view-id
     * @param viewId view-id of the page
     * @return view-config-descriptor which represents the given view-id, null otherwise
     */
    public static ViewConfigDescriptor getViewConfigDescriptor(String viewId)
    {
        return getViewIdToViewDefinitionEntryMapping(true).get(viewId);
    }

    /**
     * Resolves all descriptors for the known {@link ViewConfig}s
     * @return all descriptors for the known view-configs
     */
    public static Collection<ViewConfigDescriptor> getViewConfigDescriptors()
    {
        Map<String, ViewConfigDescriptor> entryMap = getViewIdToViewDefinitionEntryMapping(true);

        return entryMap.values();
    }

    /**
     * Resolves the {@link ViewConfigDescriptor} for the given view-config-class
     * @param viewDefinitionClass view-config-class of the page
     * @return view-config-descriptor which represents the given view-config-class
     */
    public static ViewConfigDescriptor getViewConfigDescriptor(Class<? extends ViewConfig> viewDefinitionClass)
    {
        return getViewDefinitionToViewDefinitionEntryMapping(true).get(viewDefinitionClass);
    }

    /**
     * Resolves the descriptor for the default-error page
     * @return descriptor for the default-error page
     */
    public static ViewConfigDescriptor getDefaultErrorViewConfigDescriptor()
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
                .add(new InlineViewConfigDescriptor(viewConfigExtractor, beanClass));
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
                                    ViewConfigDescriptor viewDefinitionEntry,
                                    boolean allowReplace,
                                    boolean lazyInit)
    {
        if(getViewIdToViewDefinitionEntryMapping(lazyInit).containsKey(viewId) && !allowReplace)
        {
            throw ambiguousViewDefinitionException(
                    viewId,
                    viewDefinitionEntry.getViewConfig(),
                    getViewIdToViewDefinitionEntryMapping(lazyInit).get(viewId).getViewConfig());
        }

        tryToStorePageAsDefaultErrorPage(viewDefinitionEntry);

        getViewIdToViewDefinitionEntryMapping(lazyInit).put(viewId, viewDefinitionEntry);
        getViewDefinitionToViewDefinitionEntryMapping(lazyInit)
                .put(viewDefinitionEntry.getViewConfig(), viewDefinitionEntry);
    }

    static void storeViewConfigDescriptor(String viewId,
                                          ViewConfigDescriptor viewConfigDescriptor,
                                          boolean allowReplace)
    {
        storeViewDefinition(viewId, viewConfigDescriptor, allowReplace, true);
    }

    private static Map<String, ViewConfigDescriptor> getViewIdToViewDefinitionEntryMapping(boolean lazyInit)
    {
        if(lazyInit)
        {
            lazyInlineViewConfigCompilation();
        }

        Map<String, ViewConfigDescriptor> result = viewIdToViewDefinitionEntryMapping.get(getClassloader());

        if(result == null)
        {
            result = new HashMap<String, ViewConfigDescriptor>();
            viewIdToViewDefinitionEntryMapping.put(getClassloader(), result);
        }

        return result;
    }

    private static Map<Class<? extends ViewConfig>, ViewConfigDescriptor>
        getViewDefinitionToViewDefinitionEntryMapping(boolean lazyInit)
    {
        if(lazyInit)
        {
            lazyInlineViewConfigCompilation();
        }

        Map<Class<? extends ViewConfig>, ViewConfigDescriptor> result =
                viewDefinitionToViewDefinitionEntryMapping.get(getClassloader());

        if(result == null)
        {
            result = new HashMap<Class<? extends ViewConfig>, ViewConfigDescriptor>();
            viewDefinitionToViewDefinitionEntryMapping.put(getClassloader(), result);
        }
        return result;
    }

    private static List<InlineViewConfigDescriptor> getInlineViewDefinitionToViewDefinitionEntryList()
    {
        List<InlineViewConfigDescriptor> inlineViewConfigDescriptors =
                inlineViewDefinitionEntryList.get(getClassloader());

        if(inlineViewConfigDescriptors == null)
        {
            inlineViewConfigDescriptors = new ArrayList<InlineViewConfigDescriptor>();
            inlineViewDefinitionEntryList.put(getClassloader(), inlineViewConfigDescriptors);
        }
        return inlineViewConfigDescriptors;
    }

    private static void tryToStorePageAsDefaultErrorPage(ViewConfigDescriptor viewDefinitionEntry)
    {
        if(DefaultErrorView.class.isAssignableFrom(viewDefinitionEntry.getViewConfig()))
        {
            ViewConfigDescriptor currentErrorView = getDefaultErrorViewConfigDescriptor();
            if(currentErrorView != null)
            {
                throw ambiguousDefaultErrorViewDefinitionException(viewDefinitionEntry.getViewConfig(),
                                                                   currentErrorView.getViewConfig());
            }

            setDefaultErrorView(viewDefinitionEntry);
        }
    }

    private static void setDefaultErrorView(ViewConfigDescriptor viewDefinitionEntry)
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
        List<InlineViewConfigDescriptor> inlineViewConfigDescriptors =
                inlineViewDefinitionEntryList.get(getClassloader());

        if(inlineViewConfigDescriptors == null)
        {
            //there is no inline view config or it is already processed
            return;
        }

        if(isInWriteMode())
        {
            return;
        }

        registerInlineViewConfigDescriptor();
    }

    private static boolean isInWriteMode()
    {
        return !Boolean.TRUE.equals(lazyInitAllowed.get(getClassloader()));
    }

    private static synchronized void registerInlineViewConfigDescriptor()
    {
        List<InlineViewConfigDescriptor> inlineViewConfigDescriptors =
                inlineViewDefinitionEntryList.get(getClassloader());

        // switch into paranoia mode
        if(inlineViewConfigDescriptors == null)
        {
            return;
        }

        ViewConfigDescriptor viewConfig;
        for(InlineViewConfigDescriptor inlineViewConfigDescriptor : inlineViewConfigDescriptors)
        {
            viewConfig = inlineViewConfigDescriptor.getViewConfigExtractor()
                    .extractInlineViewConfig(inlineViewConfigDescriptor.getViewConfigDefinition());

            if(viewConfig != null)
            {
                if(viewConfig instanceof EditableViewConfigDescriptor)
                {
                    //activate view controller annotations
                    ((EditableViewConfigDescriptor)viewConfig).addPageBean(viewConfig.getViewConfig());
                }

                storeViewDefinition(viewConfig.getViewId(), viewConfig, false, false);
            }
        }

        inlineViewDefinitionEntryList.put(getClassloader(), null);
    }
}
