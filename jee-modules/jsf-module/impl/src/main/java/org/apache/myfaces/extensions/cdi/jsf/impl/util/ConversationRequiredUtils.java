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
package org.apache.myfaces.extensions.cdi.jsf.impl.util;

import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationRequired;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.impl.util.AnyLiteral;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.PageBeanDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.api.config.view.ViewConfigDescriptor;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
@Typed()
public abstract class ConversationRequiredUtils
{
    private ConversationRequiredUtils()
    {
    }

    /**
     * Checks if the page-bean for the current view hosts
     * {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationRequired}
     * and the conversation has been started or the current page is an allowed entry-point for the conversation.
     * If a violation is detected, the default-entry-point
     * (specified by {@link org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationRequired})
     * will be used as new view which will be rendered.
     *
     * @param facesContext current faces-context
     */
    public static void ensureExistingConversation(FacesContext facesContext)
    {
        String oldViewId = getViewId(facesContext);

        if(oldViewId == null)
        {
            return;
        }

        ViewConfigDescriptor entry = ViewConfigCache.getViewConfigDescriptor(oldViewId);

        if(entry == null)
        {
            return;
        }

        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();

        ConversationConfig conversationConfig =
                CodiUtils.getContextualReferenceByClass(beanManager, ConversationConfig.class);

        if(!conversationConfig.isConversationRequiredEnabled())
        {
            return;
        }

        String newViewId = checkConversationRequired(beanManager, entry);

        if(newViewId != null && !oldViewId.equals(newViewId))
        {
            UIViewRoot newViewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, newViewId);

            if(newViewRoot != null)
            {
                facesContext.setViewRoot(newViewRoot);
            }
        }
    }

    private static String checkConversationRequired(BeanManager beanManager, ViewConfigDescriptor viewConfigDescriptor)
    {
        Class<? extends ViewConfig> currentView = viewConfigDescriptor.getViewConfig();

        List<PageBeanDescriptor> pageBeanDescriptorList = viewConfigDescriptor.getPageBeanDescriptors();
        for(PageBeanDescriptor pageBeanDescriptor : pageBeanDescriptorList)
        {
            Class<?> pageBeanClass = pageBeanDescriptor.getBeanClass();

            ConversationRequired conversationRequired =
                    resolveConversationRequiredAnnotation(viewConfigDescriptor, pageBeanDescriptorList, pageBeanClass);

            if(conversationRequired == null)
            {
                continue;
            }

            if(!isEntryPoint(currentView, conversationRequired.defaultEntryPoint(), conversationRequired.entryPoints()))
            {
                EditableWindowContext editableWindowContext =
                        (EditableWindowContext)ConversationUtils.getWindowContextManager().getCurrentWindowContext();

                Set<? extends Bean> foundBeans =
                        beanManager.getBeans(pageBeanDescriptor.getBeanClass(), new AnyLiteral());

                Bean<?> foundBean;
                Set<Bean<?>> beanSet;
                Class<?> conversationGroup;
                for(Bean<?> currentBean : foundBeans)
                {
                    beanSet = new HashSet<Bean<?>>(1);
                    beanSet.add(currentBean);
                    foundBean = beanManager.resolve(beanSet);

                    //only page-beans are supported -> we have to compare them by bean-name
                    if(!pageBeanDescriptor.getBeanName().equals(foundBean.getName()))
                    {
                        continue;
                    }

                    conversationGroup = getConversationGroup(conversationRequired, foundBean);

                    if(!editableWindowContext.isConversationActive(conversationGroup,
                            foundBean.getQualifiers().toArray(new Annotation[foundBean.getQualifiers().size()])))
                    {
                        return ViewConfigCache
                                .getViewConfigDescriptor(conversationRequired.defaultEntryPoint()).getViewId();
                    }
                }
            }
        }
        return null;
    }

    private static Class<?> getConversationGroup(ConversationRequired conversationRequired, Bean<?> foundBean)
    {
        Class<?> conversationGroup;
        if(ConversationRequired.class.equals(conversationRequired.conversationGroup()))
        {
            conversationGroup = ConversationUtils.getConversationGroup(foundBean);
        }
        else
        {
            conversationGroup = conversationRequired.conversationGroup();
        }
        return conversationGroup;
    }

    private static ConversationRequired resolveConversationRequiredAnnotation(ViewConfigDescriptor viewConfigDescriptor,
            List<PageBeanDescriptor> pageBeanDescriptorList, Class<?> pageBeanClass)
    {
        ConversationRequired conversationRequired = pageBeanClass.getAnnotation(ConversationRequired.class);

        //here we support just simple constellations
        //TODO handle unsupported constellations
        if(conversationRequired == null && pageBeanDescriptorList.size() == 1)
        {
            List<ConversationRequired> conversationRequiredMetaData =
                    viewConfigDescriptor.getMetaData(ConversationRequired.class);

            if(conversationRequiredMetaData.size() == 1)
            {
                conversationRequired = conversationRequiredMetaData.iterator().next();
            }
        }
        return conversationRequired;
    }

    private static boolean isEntryPoint(Class<? extends ViewConfig> currentView,
                                        Class<? extends ViewConfig> defaultEntryPoint,
                                        Class<? extends ViewConfig>[] entryPoints)
    {
        if(currentView.equals(defaultEntryPoint))
        {
            return true;
        }

        for(Class<? extends ViewConfig> entryPoint : entryPoints)
        {
            if(currentView.equals(entryPoint))
            {
                return true;
            }
        }
        return false;
    }

    private static String getViewId(FacesContext facesContext)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        if(uiViewRoot == null)
        {
            return null;
        }

        return uiViewRoot.getViewId();
    }
}
