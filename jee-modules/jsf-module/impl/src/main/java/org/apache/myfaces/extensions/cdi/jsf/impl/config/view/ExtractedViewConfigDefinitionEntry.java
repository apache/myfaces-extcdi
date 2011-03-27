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
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.EditableViewConfigDescriptor;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Gerhard Petracek
 */
class ExtractedViewConfigDefinitionEntry implements EditableViewConfigDescriptor
{
    static final String ROOT_PATH = "/";
    static final String DEFAULT_EXTENSION = Page.Extension.XHTML;

    protected static final String DEFAULT_PAGE_NAME = "";
    private static final String NOT_IMPLEMENTED_MESSAGE = "not implemented";

    private List<Class<? extends Annotation>> foundAndBlockedMetaDataTypes
            = new ArrayList<Class<? extends Annotation>>();

    //security
    private List<Class<? extends AccessDecisionVoter>> foundVoters =
            new ArrayList<Class<? extends AccessDecisionVoter>>();
    private Class<? extends ViewConfig> errorView = null;

    private List<Annotation> viewMetaDataList = new ArrayList<Annotation>();

    protected Class<? extends ViewConfig> viewDefinitionClass;
    protected String basePath = ROOT_PATH;
    private Map<String, String> simpleClassNameToPathMapping = new HashMap<String, String>();
    protected String pageName = DEFAULT_PAGE_NAME;
    private String extension = DEFAULT_EXTENSION;
    private Page.NavigationMode navigationMode = null;
    private Page.ViewParameterMode viewParameterMode = null;

    /**
     * Constructor for creating a {@link EditableViewConfigDescriptor} for the given {@link ViewConfig} definition
     * @param viewDefinitionClass current view-config
     */
    public ExtractedViewConfigDefinitionEntry(Class<? extends ViewConfig> viewDefinitionClass)
    {
        this.viewDefinitionClass = viewDefinitionClass;
    }

    /**
     * Allows to read the current base-path
     * @return current base-path
     */
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Tells if a {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.NavigationMode} has been found
     * @return true if a navigation mode has been found, false otherwise
     */
    public boolean isKnownNavigationMode()
    {
        return this.navigationMode != null;
    }

    /**
     * Tells if a {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.ViewParameterMode} has been found
     * @return true if a view-parameter mode has been found, false otherwise
     */
    public boolean isKnownViewParameterMode()
    {
        return this.viewParameterMode != null;
    }

    /**
     * {@inheritDoc}
     */
    public Page.NavigationMode getNavigationMode()
    {
        if(this.navigationMode == null)
        {
            return Page.NavigationMode.DEFAULT;
        }
        return this.navigationMode;
    }

    /**
     * {@inheritDoc}
     */
    public Page.ViewParameterMode getViewParameterMode()
    {
        if(this.viewParameterMode == null)
        {
            return Page.ViewParameterMode.DEFAULT;
        }
        return this.viewParameterMode;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends ViewConfig> getErrorView()
    {
        return errorView;
    }

    /**
     * Exposes the meta-data types which have been found so far
     * @return found meta-data types
     */
    public List<Class<? extends Annotation>> getFoundAndBlockedMetaDataTypes()
    {
        return foundAndBlockedMetaDataTypes;
    }

    /**
     * Allows to add {@link AccessDecisionVoter}s to the current entry
     * @param accessDecisionVoterClasses access-decision-voter which should be added
     */
    public void addAccessDecisionVoters(Class<? extends AccessDecisionVoter>[] accessDecisionVoterClasses)
    {
        Collections.addAll(this.foundVoters, accessDecisionVoterClasses);
    }

    /**
     * Allows to change the error-view for the current entry
     * @param errorViewClass custom error-view
     */
    public void setErrorView(Class<? extends ViewConfig> errorViewClass)
    {
        this.errorView = errorViewClass;
    }

    /**
     * Allows to change the file-extension which used be used for the page represented by this entry
     * @param fileExtension custom file-extension
     */
    public void setExtension(String fileExtension)
    {
        this.extension = fileExtension;
    }

    /**
     * Allows to change the base-path of the page
     * @param currentBasePath custom base-path
     */
    public void setBasePath(String currentBasePath)
    {
        this.basePath = currentBasePath;
    }

    /**
     * Allows to change the {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.NavigationMode} for the
     * page represented by this entry
     * @param navigationMode custom navigation-mode
     */
    public void setNavigationMode(Page.NavigationMode navigationMode)
    {
        this.navigationMode = navigationMode;
    }

    /**
     * Allows to change the {@link org.apache.myfaces.extensions.cdi.jsf.api.config.view.Page.ViewParameterMode} for the
     * page represented by this entry.
     * @param viewParameterMode custom view-param mode
     */
    public void setViewParameterMode(Page.ViewParameterMode viewParameterMode)
    {
        this.viewParameterMode = viewParameterMode;
    }

    /**
     * Allows to change the name of the page represented by this entry.
     * @param pageName custom page-name
     */
    public void setPageName(String pageName)
    {
        this.pageName = pageName;
    }

    /**
     * Allows to add further meta-data entries for the current entry
     * @param annotations meta-data
     */
    public void addMetaData(Collection<Annotation> annotations)
    {
        this.viewMetaDataList.addAll(annotations);
    }

    /**
     * Maps a custom base-path to the (simple) name of a {@link ViewConfig} class (might be empty)
     * @param simpleName (simple) class-name
     * @param currentBasePath base-path (might be empty)
     */
    public void setSimpleClassNameToPathMapping(String simpleName, String currentBasePath)
    {
        this.simpleClassNameToPathMapping.put(simpleName, currentBasePath);
    }

    /**
     * {@inheritDoc}
     */
    public List<Annotation> getMetaData()
    {
        return this.viewMetaDataList;
    }

    /**
     * {@inheritDoc}
     */
    public String getViewId()
    {
        StringBuilder viewId = new StringBuilder(this.basePath);
        if(this.pageName.equals(""))
        {
            String className = this.viewDefinitionClass.getName();

            //MyClass$MyInnerClass will be converted to /MyClass/MyInnerClass
            if(className.contains("$") && ".".equals(this.basePath))
            {
                this.basePath = "";
                className = className.substring(className.lastIndexOf('.') + 1);
                className = convertToPathSyntax(className, this.simpleClassNameToPathMapping);
            }
            else if(className.contains("$"))
            {
                className = className.substring(className.lastIndexOf('$') + 1);
            }
            else
            {
                className = className.substring(className.lastIndexOf('.') + 1);
            }
            className = createPageName(className);
            viewId.append(className);
        }
        //nested classes with manually defined page name and shared basePath
        else if(!this.simpleClassNameToPathMapping.isEmpty())
        {
            String className = this.viewDefinitionClass.getName();

            this.basePath = "";
            className = className.substring(className.lastIndexOf('.') + 1);
            className = convertToPathSyntax(className, this.simpleClassNameToPathMapping);
            className = createPageName(className);
            className = className.substring(0, className.lastIndexOf('/') + 1);
            className += this.pageName;
            viewId.append(className);
        }
        else
        {
            viewId.append(this.pageName);
        }
        viewId.append(".");
        viewId.append(this.extension);
        String result = viewId.toString();

        if(result.startsWith("."))
        {
            if(result.startsWith("./"))
            {
                result = result.substring(1);
            }
            else
            {
                result = ROOT_PATH + result.substring(1);
            }
        }

        result = ensureValidViewIds(result);
        return result;
    }

    protected String createPageName(String className)
    {
        className = className.substring(0, 1).toLowerCase() + className.substring(1);
        return className;
    }

    /*
     * TODO refactor it!
     */

    /**
     * {@inheritDoc}
     */
    public Class<? extends ViewConfig> getViewConfig()
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    private String ensureValidViewIds(String result)
    {
        if(!result.startsWith("/"))
        {
            result = "/" + result;
        }

        //TODO
        return result.replace("///", "/").replace("//", "/");
    }

    private String convertToPathSyntax(String className, Map<String, String> simpleClassNameToPathMapping)
    {
        String[] parts = className.split("\\$");
        StringBuilder path = new StringBuilder();

        for(String part : parts)
        {
            if(simpleClassNameToPathMapping.containsKey(part))
            {
                path.append(simpleClassNameToPathMapping.get(part));
            }
            else
            {
                path.append(part.substring(0, 1).toLowerCase());
                path.append(part.substring(1));
            }
            path.append("/");
        }
        String result = path.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * {@inheritDoc}
     */
    public List<Class<? extends AccessDecisionVoter>> getAccessDecisionVoters()
    {
        return Collections.unmodifiableList(this.foundVoters);
    }

    /**
     * not available
     */
    public void addPageBean(Class pageBeanClass)
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    /**
     * not available
     */
    public void invokeInitViewMethods()
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    /**
     * not available
     */
    public void invokePrePageActionMethods()
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    /**
     * not available
     */
    public void invokePreRenderViewMethods()
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    /**
     * not available
     */
    public void invokePostRenderViewMethods()
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    /**
     * not available
     */
    public List<PageBeanDescriptor> getPageBeanConfigs()
    {
        throw new IllegalStateException(NOT_IMPLEMENTED_MESSAGE);
    }

    /**
     * Forces to block the given meta-data for new scanning processes
     * @param metaDataClass meta-data which should be blocked
     */
    public void blockMetaData(Class<? extends Annotation> metaDataClass)
    {
        this.foundAndBlockedMetaDataTypes.add(metaDataClass);
    }
}
