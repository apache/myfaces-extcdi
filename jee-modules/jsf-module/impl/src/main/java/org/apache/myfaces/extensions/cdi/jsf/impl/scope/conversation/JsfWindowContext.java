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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.Conversation;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationSubGroup;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.ConversationConfig;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.event.CloseWindowContextEvent;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationFactory;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils.isQualifierEqual;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.*;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.*;

/**
 * TODO
 *
 * @author Gerhard Petracek
 */
@Typed()
class JsfWindowContext implements EditableWindowContext
{
    private static final long serialVersionUID = 5272798129165017829L;

    private final String id;

    private final WindowContextConfig windowContextConfig;
    private final ConversationConfig conversationConfig;
    private final boolean projectStageDevelopment;

    //all implementations will be serializable
    private BeanManager beanManager;

    private ConcurrentHashMap<ConversationKey, EditableConversation> groupedConversations
            = new ConcurrentHashMap<ConversationKey, EditableConversation>();

    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private final TimeoutExpirationEvaluator expirationEvaluator;

    JsfWindowContext(String windowContextId,
                     WindowContextConfig windowContextConfig,
                     ConversationConfig conversationConfig,
                     boolean projectStageDevelopment,
                     BeanManager beanManager)
    {
        this.id = windowContextId;
        this.windowContextConfig = windowContextConfig;
        this.conversationConfig = conversationConfig;
        this.projectStageDevelopment = projectStageDevelopment;
        this.beanManager = beanManager;

        this.expirationEvaluator = new TimeoutExpirationEvaluator(
                this.windowContextConfig.getWindowContextTimeoutInMinutes());
    }

    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    public void closeConversations()
    {
        //don't force it because window-scoped beans shouldn't be affected
        closeConversations(false);
    }

    /**
     * {@inheritDoc}
     */
    public void close()
    {
        if(this.windowContextConfig.isCloseWindowContextEventEnabled())
        {
            this.beanManager.fireEvent(new CloseWindowContextEvent(this));
        }

        closeConversations(true);
        this.attributes.clear();
    }

    private synchronized void closeConversations(boolean forceEnd)
    {
        for (Map.Entry<ConversationKey, EditableConversation> conversationEntry : this.groupedConversations.entrySet())
        {
            closeAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue(), null, forceEnd);
        }
        JsfUtils.resetConversationCache();
    }

    /**
     * {@inheritDoc}
     */
    public EditableConversation getConversation(Class<?> conversationGroupKey, Annotation... qualifiers)
    {
        ConversationSubGroup conversationSubGroup = conversationGroupKey.getAnnotation(ConversationSubGroup.class);
        Class<?>[] subGroups = null;
        if(conversationSubGroup != null)
        {
            subGroups = conversationSubGroup.subGroup();
            conversationGroupKey = convertToSubGroup(conversationGroupKey);
        }

        Class<? extends Annotation> scopeType = convertToScope(this.beanManager, conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        EditableConversation conversation = RequestCache.getConversation(conversationKey);

        if(conversation == null)
        {
            conversation = getConversationForKey(conversationKey, false);

            //TODO
            if (conversation != null && !conversation.isActive())
            {
                closeAndRemoveConversation(conversationKey, conversation, subGroups, true);
                conversation = null;
            }

            if (conversation == null)
            {
                conversation = createConversation(conversationGroupKey, qualifiers);
                this.groupedConversations.put(conversationKey, conversation);
            }

            RequestCache.setConversation(conversationKey, conversation);
        }
        return conversation;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConversationActive(Class conversationGroupKey, Annotation... qualifiers)
    {
        Class<? extends Annotation> scopeType = convertToScope(this.beanManager, conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        EditableConversation conversation = RequestCache.getConversation(conversationKey);

        if(conversation == null)
        {
            conversation = getConversationForKey(conversationKey, false);

            if (conversation == null)
            {
                return false;
            }
        }
        return conversation.getActiveState();
    }

    /**
     * {@inheritDoc}
     */
    public Conversation closeConversation(Class<?> conversationGroupKey, Annotation... qualifiers)
    {
        ConversationSubGroup conversationSubGroup = conversationGroupKey.getAnnotation(ConversationSubGroup.class);
        Class<?>[] subGroups = null;
        if(conversationSubGroup != null)
        {
            subGroups = conversationSubGroup.subGroup();
            conversationGroupKey = convertToSubGroup(conversationGroupKey);
        }

        Class<? extends Annotation> scopeType = convertToScope(this.beanManager, conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        EditableConversation conversation = getConversationForKey(conversationKey, true);

        return closeAndRemoveConversation(conversationKey, conversation, subGroups, true);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Conversation> closeConversationGroup(Class<?> conversationGroupKey)
    {
        ConversationSubGroup conversationSubGroup = conversationGroupKey.getAnnotation(ConversationSubGroup.class);
        Class<?>[] subGroups = null;
        if(conversationSubGroup != null)
        {
            subGroups = conversationSubGroup.subGroup();
            conversationGroupKey = convertToSubGroup(conversationGroupKey);
        }

        Set<Conversation> removedConversations = new HashSet<Conversation>();
        for(Map.Entry<ConversationKey, EditableConversation> conversationEntry : this.groupedConversations.entrySet())
        {
            if(conversationGroupKey.isAssignableFrom(conversationEntry.getKey().getConversationGroup()))
            {
                removedConversations.add(
                        closeAndRemoveConversation(
                                conversationEntry.getKey(), conversationEntry.getValue(), subGroups, true));
            }
        }
        return removedConversations;
    }

    private EditableConversation closeAndRemoveConversation(ConversationKey conversationKey,
                                                            EditableConversation conversation,
                                                            Class<?>[] subGroups,
                                                            boolean forceEnd)
    {
        logInformationAboutConversations("before JsfWindowContext#endAndRemoveConversation");

        try
        {
            if(subGroups != null)
            {
                return closeSubGroups(conversationKey, subGroups);
            }

            if (forceEnd)
            {
                conversation.close();
                return this.groupedConversations.remove(conversationKey);
            }
            else
            {
                conversation.deactivate();

                if(!conversation.isActive())
                {
                    return this.groupedConversations.remove(conversationKey);
                }
            }
        }
        finally
        {
            logInformationAboutConversations("after JsfWindowContext#endAndRemoveConversation");
        }

        return null;
    }

    private EditableConversation closeSubGroups(ConversationKey conversationKey, Class<?>[] subGroups)
    {
        EditableConversation editableConversation = this.groupedConversations.get(conversationKey);

        if(editableConversation == null)
        {
            throw new IllegalStateException(conversationKey.toString() +
                    " is no valid key for an existing conversation");
        }

        List<Class<?>> implicitSubGroupCandidates = new ArrayList<Class<?>>();
        for(Class<?> subGroup : subGroups)
        {
            if(editableConversation.removeBeanEntry(subGroup) == null)
            {
                //no bean was scoped -> try to use the sub-group as sub-group-type
                implicitSubGroupCandidates.add(subGroup);
            }
        }

        tryToCloseImplicitConversationSubGroup(editableConversation, implicitSubGroupCandidates);

        return editableConversation;
    }

    private void tryToCloseImplicitConversationSubGroup(
            EditableConversation editableConversation, List<Class<?>> subGroupTypes)
    {
        if(!subGroupTypes.isEmpty())
        {
            Set<Class<?>> concreteBeanClasses;
            for(Class subGroupType : subGroupTypes)
            {
                concreteBeanClasses = editableConversation.getBeanSubGroup(subGroupType);

                for(Class<?> beanClass : concreteBeanClasses)
                {
                    editableConversation.removeBeanEntry(beanClass);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public EditableConversation createConversation(Class<?> conversationGroupKey, Annotation... qualifiers)
    {
        Class<? extends Annotation> scopeType = convertToScope(this.beanManager, conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        ConversationFactory conversationFactory = CodiUtils.getContextualReferenceByClass(ConversationFactory.class);
        return conversationFactory.createConversation(conversationKey, this.conversationConfig);
    }

    /**
     * {@inheritDoc}
     */
    public Map<ConversationKey /*conversation group*/, EditableConversation> getConversations()
    {
        return Collections.unmodifiableMap(this.groupedConversations);
    }

    /**
     * {@inheritDoc}
     */
    public WindowContextConfig getConfig()
    {
        return this.windowContextConfig;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive()
    {
        return !this.expirationEvaluator.isExpired();
    }

    /**
     * {@inheritDoc}
     */
    public Date getLastAccess()
    {
        return this.expirationEvaluator.getLastAccess();
    }

    /**
     * {@inheritDoc}
     */
    public void touch()
    {
        this.expirationEvaluator.touch();
    }

    /**
     * {@inheritDoc}
     */
    public void removeInactiveConversations()
    {
        Iterator<EditableConversation> conversations = this.groupedConversations.values().iterator();

        EditableConversation conversation;
        while (conversations.hasNext())
        {
            conversation = conversations.next();

            if (!conversation.getActiveState())
            {
                conversations.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean setAttribute(String name, Object value)
    {
        return setAttribute(name, value, true);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setAttribute(String name, Object value, boolean forceOverride)
    {
        if(value == null || (!forceOverride && containsAttribute(name)))
        {
            return false;
        }
        this.attributes.put(name, value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAttribute(String name)
    {
        return this.attributes.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getAttribute(String name, Class<T> targetType)
    {
        //noinspection unchecked
        return (T)this.attributes.get(name);
    }

    private EditableConversation getConversationForKey(ConversationKey conversationKey, boolean forceNewConversation)
    {
        EditableConversation editableConversation = this.groupedConversations.get(conversationKey);

        if(editableConversation != null)
        {
            return editableConversation;
        }

        if(!forceNewConversation)
        {
            return null;
        }

        if(conversationKey.getQualifiers() != null)
        {
            /*
             * we have to manually recalculate the conversation keys due to the restrictions of annotations.
             * we don't do that per default because in most use-cases you don't need to create manual qualifiers.
             * that means most use-cases will have a better performance and only very few will have an overhead as soon
             * as a conversation gets closed manually.
             */
            editableConversation = getConversationForDynamicKey(conversationKey);
        }

        if(editableConversation != null)
        {
            return editableConversation;
        }

        throw conversationNotFoundException(conversationKey.toString());
    }

    private EditableConversation getConversationForDynamicKey(ConversationKey conversationKey)
    {
        for(Map.Entry<ConversationKey, EditableConversation> conversationEntry : this.groupedConversations.entrySet())
        {
            if(isSameConversationType(conversationEntry.getKey(), conversationKey))
            {
                if(compareAnnotations(conversationEntry.getKey().getQualifiers(), conversationKey.getQualifiers()))
                {
                    return conversationEntry.getValue();
                }
            }

        }

        return null;
    }

    private boolean isSameConversationType(ConversationKey currentConversationKey, ConversationKey conversationKey)
    {
        return currentConversationKey.getConversationGroup().equals(conversationKey.getConversationGroup()) &&
                currentConversationKey.getScope().equals(conversationKey.getScope());
    }

    private boolean compareAnnotations(Set<Annotation> source, Set<Annotation> target)
    {
        Set<Annotation> sourceAnnotations = new HashSet<Annotation>(source);
        Set<Annotation> targetAnnotations = new HashSet<Annotation>(target);

        Iterator<Annotation> sourceAnnotationIterator = sourceAnnotations.iterator();

        Annotation sourceAnnotation;
        Annotation targetAnnotation;

        outer:
        while(sourceAnnotationIterator.hasNext())
        {
            sourceAnnotation = sourceAnnotationIterator.next();
            
            Iterator<Annotation> targetAnnotationIterator = targetAnnotations.iterator();
            while (targetAnnotationIterator.hasNext())
            {
                targetAnnotation = targetAnnotationIterator.next();

                if(isQualifierEqual(sourceAnnotation, targetAnnotation))
                {
                    sourceAnnotationIterator.remove();
                    targetAnnotationIterator.remove();

                    continue outer;
                }
            }
        }

        return sourceAnnotations.isEmpty() && targetAnnotations.isEmpty();
    }

    private void logInformationAboutConversations(String label)
    {
        if(!this.projectStageDevelopment)
        {
            return;
        }

        Logger logger = Logger.getLogger(JsfWindowContext.class.getName());
        logger.info(label);
        logger.info("\n*** conversations - start ***");
        for(Map.Entry<ConversationKey, EditableConversation> conversationEntry : this.groupedConversations.entrySet())
        {
            if(conversationEntry.getValue() instanceof DefaultConversation)
            {
                logger.info(conversationEntry.getValue().toString());
            }
        }
        logger.info("\n\n*** conversations - end ***");
        logger.info("***************************");
    }
}
