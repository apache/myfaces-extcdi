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
package org.apache.myfaces.extensions.cdi.test.webapp.conversation.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ConversationScoped;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowContext;
import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier1;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier2;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier3;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier4;
import org.apache.myfaces.extensions.cdi.test.webapp.conversation.qualifier.ConversationQualifier5;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Jakob Korherr
 */
@Named
@ConversationScoped
public class ConversationBeanWithInjectedQualifierBeans implements Serializable
{

    @Inject
    @ConversationQualifier1
    private ConversationBeanWithQualifier _beanWithQualifier1;

    @Inject
    @ConversationQualifier2
    private ConversationBeanWithQualifier _beanWithQualifier2DefaultValues;

    @Inject
    @ConversationQualifier2(value = "test value", number = 4711)
    private ConversationBeanWithQualifier _beanWithQualifier2NonDefaultValues;

    @Inject
    @ConversationQualifier3(value = "test value", input = "test input") // input is @Nonbinding
    private ConversationBeanWithQualifier _beanWithQualifier3;

    @Inject
    @ConversationQualifier4
    @ConversationQualifier5
    private ConversationBeanWithQualifier _beanWithQualifier4And5;

    //@Inject
    //@ConversationQualifier4
    //private ConversationBeanWithQualifier _beanWithQualifier4; //TODO ambiguous?

    @Inject
    private WindowContext _windowContext;


    public ConversationBeanWithQualifier getBeanWithQualifier1()
    {
        return _beanWithQualifier1;
    }

    public ConversationBeanWithQualifier getBeanWithQualifier2DefaultValues()
    {
        return _beanWithQualifier2DefaultValues;
    }

    public ConversationBeanWithQualifier getBeanWithQualifier2NonDefaultValues()
    {
        return _beanWithQualifier2NonDefaultValues;
    }

    public ConversationBeanWithQualifier getBeanWithQualifier3()
    {
        return _beanWithQualifier3;
    }

    public ConversationBeanWithQualifier getBeanWithQualifier4And5()
    {
        return _beanWithQualifier4And5;
    }

    public String closeConversationWithQualifier1()
    {
        _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                DefaultAnnotation.of(ConversationQualifier1.class));

        return null;
    }

    public String closeConversationWithQualifier2DefaultValues()
    {
        _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                DefaultAnnotation.of(ConversationQualifier2.class));

        return null;
    }

    public String closeConversationWithQualifier2NonDefaultValuesMatching()
    {
        _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                new MatchingConversationQualifier2());

        return null;
    }

    public String closeConversationWithQualifier2NonDefaultValuesNonMatching()
    {
        try
        {
            // this will fail and thus throw an Exception
            _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                    new NonMatchingConversationQualifier2());
        }
        catch(Exception e)
        {
            // noop - we will see that the Conversation has not been closed
        }

        return null;
    }

    public String closeConversationWithQualifier3()
    {
        _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                new NonbindingMatchingConversationQualifier3());

        return null;
    }

    public String closeConversationWithQualifier4()
    {
        try
        {
            // this will fail and thus throw an Exception
            _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                DefaultAnnotation.of(ConversationQualifier4.class));
        }
        catch(Exception e)
        {
            // noop - we will see that the Conversation has not been closed
        }

        return null;
    }

    public String closeConversationWithQualifier5()
    {
        try
        {
            // this will fail and thus throw an Exception
            _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                DefaultAnnotation.of(ConversationQualifier5.class));
        }
        catch(Exception e)
        {
            // noop - we will see that the Conversation has not been closed
        }

        return null;
    }

    public String closeConversationWithQualifier4And5()
    {
        _windowContext.closeConversation(ConversationBeanWithQualifier.class,
                DefaultAnnotation.of(ConversationQualifier4.class),
                DefaultAnnotation.of(ConversationQualifier5.class));
        
        return null;
    }

}

class MatchingConversationQualifier2
        extends AnnotationLiteral<ConversationQualifier2>
        implements ConversationQualifier2
{

    public String value()
    {
        return "test value";
    }

    public int number()
    {
        return 4711;
    }
}

class NonMatchingConversationQualifier2
        extends AnnotationLiteral<ConversationQualifier2>
        implements ConversationQualifier2
{

    public String value()
    {
        return "non-matching value";
    }

    public int number()
    {
        return 1234;
    }
}

/**
 * Matches only value(), but not input() from the InjectionPoint,
 * since input() is @Nonbinding and mustnot be considered
 */
class NonbindingMatchingConversationQualifier3
        extends AnnotationLiteral<ConversationQualifier3>
        implements ConversationQualifier3
{

    public String value()
    {
        return "test value";
    }

    /**
     * NOTE that input() is @Nonbinding
     * 
     * @return
     */
    public String input()
    {
        return "some totally different value";
    }
}
