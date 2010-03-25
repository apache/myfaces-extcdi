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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.listener.phase;

import org.apache.myfaces.extensions.cdi.core.api.listener.phase.annotation.View;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.annotation.BeforePhase;
import org.apache.myfaces.extensions.cdi.javaee.jsf.api.listener.phase.annotation.AfterPhase;

import javax.interceptor.Interceptor;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.faces.context.FacesContext;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */

@View
@Interceptor
public class ViewControllerInterceptor
{
    @AroundInvoke
    public Object filterPhaseListenerMethods(InvocationContext invocationContext) throws Exception
    {
        Object result = null;
        try
        {
            if(invokeListenerMethod(invocationContext))
            {
                result = invocationContext.proceed();
            }
        }
        catch (Exception e)
        {
            throw e;
        }

        return result;
    }

    private boolean invokeListenerMethod(InvocationContext invocationContext)
    {
        if(!isObserverMethod(invocationContext) || !isViewAnnotationPresent(invocationContext))
        {
            return true;
        }

        View view = getViewAnnotation(invocationContext);

        if(view.value().length == 1 && "*".equals(view.value()[0]))
        {
            return true;
        }

        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return isMethodBoundToView(view.value(), viewId);
    }

    private boolean isObserverMethod(InvocationContext invocationContext)
    {
        for(Annotation[] annotations : invocationContext.getMethod().getParameterAnnotations())
        {
            for(Annotation annotation : annotations)
            {
                if(BeforePhase.class.isAssignableFrom(annotation.annotationType()) ||
                        AfterPhase.class.isAssignableFrom(annotation.annotationType()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isViewAnnotationPresent(InvocationContext invocationContext)
    {
        return invocationContext.getMethod().isAnnotationPresent(View.class) ||
             invocationContext.getMethod().getDeclaringClass().isAnnotationPresent(View.class);
    }

    private View getViewAnnotation(InvocationContext invocationContext)
    {
        View view;
        if(invocationContext.getMethod().isAnnotationPresent(View.class))
        {
            view = invocationContext.getMethod().getAnnotation(View.class);
        }
        else
        {
            view = invocationContext.getMethod().getDeclaringClass().getAnnotation(View.class);
        }
        return view;
    }

    private boolean isMethodBoundToView(String[] viewIds, String viewId)
    {
        for(String current : viewIds)
        {
            if(current.equals(viewId))
            {
                return true;
            }
        }
        return false;
    }
}
