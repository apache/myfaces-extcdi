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
package org.apache.myfaces.extensions.cdi.jsf.impl.security;

import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoterContext;
import org.apache.myfaces.extensions.cdi.core.impl.security.spi.EditableAccessDecisionVoterContext;
import org.apache.myfaces.extensions.cdi.core.impl.util.CodiUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.security.spi.SecurityStrategy;
import org.apache.myfaces.extensions.cdi.core.api.security.Secured;
import org.apache.myfaces.extensions.cdi.core.api.security.AccessDecisionVoter;
import static org.apache.myfaces.extensions.cdi.core.impl.util.SecurityUtils.invokeVoters;

import javax.inject.Inject;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.context.Dependent;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Dependent
public class DefaultSecurityStrategy implements SecurityStrategy
{
    private static final long serialVersionUID = -7999599690398948059L;

    //all implementations will be serializable
    @Inject
    private BeanManager beanManager;

    /**
     * {@inheritDoc}
     */
    public Object execute(InvocationContext invocationContext) throws Exception
    {
        AccessDecisionVoterContext voterContext =
                CodiUtils.getContextualReferenceByClass(beanManager, AccessDecisionVoterContext.class, true);

        Secured secured = null;

        List<Annotation> annotatedTypeMetadata = extractMetadata(invocationContext);

        for (Annotation annotation : annotatedTypeMetadata)
        {
            if(Secured.class.isAssignableFrom(annotation.annotationType()))
            {
                secured = (Secured)annotation;
            }
            else if(voterContext instanceof EditableAccessDecisionVoterContext)
            {
                ((EditableAccessDecisionVoterContext)voterContext)
                        .addMetaData(annotation.annotationType().getName(), annotation);
            }
        }

        if(secured != null)
        {
            Class<? extends AccessDecisionVoter>[] voterClasses = secured.value();

            invokeVoters(invocationContext, this.beanManager, voterContext,
                    Arrays.asList(voterClasses), secured.errorView());
        }

        return invocationContext.proceed();
    }

    private List<Annotation> extractMetadata(InvocationContext invocationContext)
    {
        List<Annotation> result = new ArrayList<Annotation>();

        Method method = invocationContext.getMethod();

        result.addAll(getAllAnnotations(method.getAnnotations()));
        result.addAll(getAllAnnotations(method.getDeclaringClass().getAnnotations()));

        return result;
    }

    private List<Annotation> getAllAnnotations(Annotation[] annotations)
    {
        List<Annotation> result = new ArrayList<Annotation>();

        String annotationName;
        for(Annotation annotation : annotations)
        {
            annotationName = annotation.annotationType().getName();
            if(annotationName.startsWith("java.") || annotationName.startsWith("javax."))
            {
                continue;
            }

            result.add(annotation);
            result.addAll(getAllAnnotations(annotation.annotationType().getAnnotations()));
        }

        return result;
    }
}
