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
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationFactory;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.ConversationKey;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.JsfUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.inject.Typed;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils.isQualifierEqual;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.convertToScope;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.conversationNotEditableException;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ExceptionUtils.conversationNotFoundException;

/**
 * TODO
 *
 * @author Gerhard Petracek
 */
@Typed()
public class JsfWindowContext implements EditableWindowContext
{
    private static final long serialVersionUID = 5272798129165017829L;

    private final String id;

    private final JsfAwareWindowContextConfig jsfAwareWindowContextConfig;
    private final boolean projectStageDevelopment;

    private Map<ConversationKey, EditableConversation> groupedConversations
            = new ConcurrentHashMap<ConversationKey, EditableConversation>();

    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private final TimeoutExpirationEvaluator expirationEvaluator;

    JsfWindowContext(String windowContextId,
                     JsfAwareWindowContextConfig jsfAwareWindowContextConfig,
                     boolean projectStageDevelopment)
    {
        this.id = windowContextId;
        this.jsfAwareWindowContextConfig = jsfAwareWindowContextConfig;
        this.projectStageDevelopment = projectStageDevelopment;

        this.expirationEvaluator = new TimeoutExpirationEvaluator(
                this.jsfAwareWindowContextConfig.getWindowContextTimeoutInMinutes());
    }

    public String getId()
    {
        return this.id;
    }

    public void closeConversations()
    {
        closeConversations(false);
    }

    public void close()
    {
        closeConversations(true);
        this.attributes.clear();
    }

    public synchronized void closeConversations(boolean forceEnd)
    {
        for (Map.Entry<ConversationKey, EditableConversation> conversationEntry : this.groupedConversations.entrySet())
        {
            endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue(), forceEnd);
        }
        JsfUtils.resetConversationCache();
    }

    public EditableConversation getConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        Class<? extends Annotation> scopeType = convertToScope(conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        EditableConversation conversation = RequestCache.getConversation(conversationKey);

        if(conversation == null)
        {
            conversation = getConversationForKey(conversationKey, false);

            //TODO
            if (conversation != null && !conversation.isActive())
            {
                endAndRemoveConversation(conversationKey, conversation, true);
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

    public Conversation closeConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        Class<? extends Annotation> scopeType = convertToScope(conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        Conversation conversation = getConversationForKey(conversationKey, true);

        if(!(conversation instanceof EditableConversation))
        {
            throw conversationNotEditableException(conversation);
        }
        return endAndRemoveConversation(conversationKey, (EditableConversation)conversation, true);
    }

    public Set<Conversation> closeConversationGroup(Class conversationGroupKey)
    {
        Set<Conversation> removedConversations = new HashSet<Conversation>();
        for(Map.Entry<ConversationKey, EditableConversation> conversationEntry : this.groupedConversations.entrySet())
        {
            if(conversationGroupKey.isAssignableFrom(conversationEntry.getKey().getConversationGroup()))
            {
                removedConversations.add(
                        endAndRemoveConversation(conversationEntry.getKey(), conversationEntry.getValue(), true));
            }
        }
        return removedConversations;
    }

    private EditableConversation endAndRemoveConversation(ConversationKey conversationKey,
                                                          EditableConversation conversation,
                                                          boolean forceEnd)
    {
        logInformationAboutConversations("before JsfWindowContext#endAndRemoveConversation");

        try
        {
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
                    conversation.close();
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

    public EditableConversation createConversation(Class conversationGroupKey, Annotation... qualifiers)
    {
        Class<? extends Annotation> scopeType = convertToScope(conversationGroupKey, qualifiers);

        ConversationKey conversationKey =
                new DefaultConversationKey(scopeType, conversationGroupKey, qualifiers);

        ConversationFactory conversationFactory = this.jsfAwareWindowContextConfig.getConversationFactory();

        return conversationFactory.createConversation(conversationKey, this.jsfAwareWindowContextConfig);
    }

    public Map<ConversationKey /*conversation group*/, EditableConversation> getConversations()
    {
        return Collections.unmodifiableMap(this.groupedConversations);
    }

    public WindowContextConfig getConfig()
    {
        return this.jsfAwareWindowContextConfig;
    }

    public boolean isActive()
    {
        return !this.expirationEvaluator.isExpired();
    }

    public Date getLastAccess()
    {
        return this.expirationEvaluator.getLastAccess();
    }

    public void touch()
    {
        this.expirationEvaluator.touch();
    }

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

    public boolean setAttribute(String name, Object value)
    {
        return setAttribute(name, value, true);
    }

    public boolean setAttribute(String name, Object value, boolean forceOverride)
    {
        if(value == null || (!forceOverride && containsAttribute(name)))
        {
            return false;
        }
        this.attributes.put(name, value);
        return true;
    }

    public boolean containsAttribute(String name)
    {
        return this.attributes.containsKey(name);
    }

    public <T> T getAttribute(String name, Class<T> targetType)
    {
        //noinspection unchecked
        return (T)this.attributes.get(name);
    }

    private EditableConversation getConversationForKey(ConversationKey conversationKey, boolean existingConversation)
    {
        EditableConversation editableConversation = this.groupedConversations.get(conversationKey);

        if(editableConversation != null)
        {
            return editableConversation;
        }

        if(!existingConversation)
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
        Iterator<Annotation> targetAnnotationIterator = targetAnnotations.iterator();

        Annotation sourceAnnotation;
        Annotation targetAnnotation;

        outer:
        while(sourceAnnotationIterator.hasNext())
        {
            sourceAnnotation = sourceAnnotationIterator.next();

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

    public void logInformationAboutConversations(String label)
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
