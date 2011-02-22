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
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.PageBeanConfigEntry;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.spi.ViewConfigEntry;

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
class ExtractedViewConfigDefinitionEntry implements ViewConfigEntry
{
    private static final long serialVersionUID = -8387356240329549455L;

    final static String ROOT_PATH = "/";
    final static String DEFAULT_PAGE_NAME = "";
    final static String DEFAULT_EXTENSION = Page.Extension.XHTML;

    List<Class<? extends Annotation>> foundAndBlockedMetaDataTypes = new ArrayList<Class<? extends Annotation>>();

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
    private Page.ViewParameter viewParameter = null;

    public ExtractedViewConfigDefinitionEntry(Class<? extends ViewConfig> viewDefinitionClass)
    {
        this.viewDefinitionClass = viewDefinitionClass;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public boolean isKnownNavigationMode()
    {
        return this.navigationMode != null;
    }

    public Page.NavigationMode getNavigationMode()
    {
        if(this.navigationMode == null)
        {
            return Page.NavigationMode.DEFAULT;
        }
        return this.navigationMode;
    }

    public boolean isKnownViewParameter()
    {
        return this.viewParameter != null;
    }

    public Page.ViewParameter getViewParameter()
    {
        if(this.viewParameter == null)
        {
            return Page.ViewParameter.DEFAULT;
        }
        return this.viewParameter;
    }

    public Class<? extends ViewConfig> getErrorView()
    {
        return errorView;
    }

    public List<Class<? extends Annotation>> getFoundAndBlockedMetaDataTypes()
    {
        return foundAndBlockedMetaDataTypes;
    }

    public void addAccessDecisionVoters(Class<? extends AccessDecisionVoter>[] accessDecisionVoterClasses)
    {
        Collections.addAll(this.foundVoters, accessDecisionVoterClasses);
    }

    public void setErrorView(Class<? extends ViewConfig> errorViewClass)
    {
        this.errorView = errorViewClass;
    }

    public void setExtension(String fileExtension)
    {
        this.extension = fileExtension;
    }

    public void setBasePath(String currentBasePath)
    {
        this.basePath = currentBasePath;
    }

    public void setNavigationMode(Page.NavigationMode navigationMode)
    {
        this.navigationMode = navigationMode;
    }

    public void setViewParameter(Page.ViewParameter viewParameter)
    {
        this.viewParameter = viewParameter;
    }

    public void setPageName(String pageName)
    {
        this.pageName = pageName;
    }

    public void addMetaData(Collection<Annotation> annotations)
    {
        this.viewMetaDataList.addAll(annotations);
    }

    public void setSimpleClassNameToPathMapping(String simpleName, String currentBasePath)
    {
        this.simpleClassNameToPathMapping.put(simpleName, currentBasePath);
    }

    public List<Annotation> getMetaData()
    {
        return this.viewMetaDataList;
    }

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
                className = className.substring(className.lastIndexOf(".") + 1);
                className = convertToPathSyntax(className, this.simpleClassNameToPathMapping);
            }
            else if(className.contains("$"))
            {
                className = className.substring(className.lastIndexOf("$") + 1);
            }
            else
            {
                className = className.substring(className.lastIndexOf(".") + 1);
            }
            className = createPageName(className);
            viewId.append(className);
        }
        //nested classes with manually defined page name and shared basePath
        else if(!this.simpleClassNameToPathMapping.isEmpty())
        {
            String className = this.viewDefinitionClass.getName();

            this.basePath = "";
            className = className.substring(className.lastIndexOf(".") + 1);
            className = convertToPathSyntax(className, this.simpleClassNameToPathMapping);
            className = createPageName(className);
            className = className.substring(0, className.lastIndexOf("/") + 1);
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
    public Class<? extends ViewConfig> getViewDefinitionClass()
    {
        throw new IllegalStateException("not implemented");
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

    public List<Class<? extends AccessDecisionVoter>> getAccessDecisionVoters()
    {
        return Collections.unmodifiableList(this.foundVoters);
    }

    public void addPageBean(Class pageBeanClass)
    {
        throw new IllegalStateException("not implemented");
    }

    public void invokeInitViewMethods()
    {
        throw new IllegalStateException("not implemented");
    }

    public void invokePrePageActionMethods()
    {
        throw new IllegalStateException("not implemented");
    }

    public void invokePreRenderViewMethods()
    {
        throw new IllegalStateException("not implemented");
    }

    public void invokePostRenderViewMethods()
    {
        throw new IllegalStateException("not implemented");
    }

    public List<PageBeanConfigEntry> getPageBeanDefinitions()
    {
        throw new IllegalStateException("not implemented");
    }

    public void blockMetaData(Class<? extends Annotation> metaDataClass)
    {
        this.foundAndBlockedMetaDataTypes.add(metaDataClass);
    }
}
