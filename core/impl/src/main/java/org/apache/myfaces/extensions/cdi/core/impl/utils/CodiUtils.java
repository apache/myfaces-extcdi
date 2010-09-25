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
package org.apache.myfaces.extensions.cdi.core.impl.utils;

import org.apache.myfaces.extensions.cdi.core.api.Advanced;
import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.Nonbinding;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This is a collection of a few useful static helper functions.
 * <p/>
 */
public class CodiUtils
{
    //TODO change source
    public static final String CODI_PROPERTIES = "/META-INF/extcdi/extcdi.properties";

    public static <T> T createNewInstanceOfBean(CreationalContext<T> creationalContext, Bean<T> bean)
    {
        return createNewInstanceOfBean(bean, creationalContext);
    }

    public static <T> T createNewInstanceOfBean(Bean<T> bean, CreationalContext<T> creationalContext)
    {
        return bean.create(creationalContext);
    }

    public static <T> T getOrCreateScopedInstanceOfBeanByName(String beanName, Class<T> targetClass)
    {
        Set<Bean<?>> foundBeans = BeanManagerProvider.getInstance().getBeanManager().getBeans(beanName);

        if(foundBeans.size() != 1)
        {
            throw new IllegalStateException(foundBeans.size() + " beans found with name: " + beanName);
        }

        //noinspection unchecked
        return (T)getOrCreateScopedInstanceOfBean(foundBeans.iterator().next());
    }

    public static <T> T getOrCreateScopedInstanceOfBeanByClass(Class<T> targetClass, Annotation... qualifier)
    {
        return getOrCreateScopedInstanceOfBeanByClass(targetClass, false, qualifier);
    }

    public static <T> T getOrCreateScopedInstanceOfBeanByClass(
            Class<T> targetClass, boolean optionalBeanAllowed, Annotation... qualifier)
    {
        Set<? extends Bean> foundBeans = BeanManagerProvider.getInstance().getBeanManager()
                .getBeans(targetClass, qualifier);

        if(foundBeans.size() > 1)
        {
            StringBuffer detailsOfBeans = new StringBuffer();

            for(Bean bean : foundBeans)
            {
                detailsOfBeans.append(bean.toString());
            }
            throw new IllegalStateException(foundBeans.size() + " beans found for type: " + targetClass.getName() +
                    " the found beans are: " + detailsOfBeans.toString());
        }

        if(foundBeans.size() == 1)
        {
            //noinspection unchecked
            return (T)getOrCreateScopedInstanceOfBean(foundBeans.iterator().next());
        }

        if(!optionalBeanAllowed)
        {
            throw new IllegalStateException("No bean found for type: " + targetClass.getName());
        }
        return null;
    }

    public static <T> T getOrCreateScopedInstanceOfBean(Bean<T> bean)
    {
        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
        Context context = beanManager.getContext(bean.getScope());

        T result = context.get(bean);

        if (result == null)
        {
            result = context.get(bean, getCreationalContextFor(beanManager, bean));
        }
        return result;
    }

    public static <T> void destroyBean(CreationalContext<T> creationalContext, Bean<T> bean, T beanInstance)
    {
        bean.destroy(beanInstance, creationalContext);
    }

    private static <T> CreationalContext<T> getCreationalContextFor(BeanManager beanManager, Bean<T> bean)
    {
        return beanManager.createCreationalContext(bean);
    }

    /**
     * Load Properties from a configuration file with the given resourceName.
     *
     * @param resourceName
     * @return Properties or <code>null</code> if the given property file doesn't exist
     * @throws IOException on underlying IO problems
     */
    public static Properties getProperties(String resourceName) throws IOException
    {
        Properties props = null;
        ClassLoader cl = ClassUtils.getClassLoader(resourceName);
        InputStream is = cl.getResourceAsStream(resourceName);
        if (is != null)
        {
            props = new Properties();
            props.load(is);
        }

        return props;
    }

    /**
     * Lookup the given property from the default CODI properties file.
     *
     * @param propertyName
     * @return the value of the property or <code>null</code> it it doesn't exist.
     * @throws IOException
     * @throws IllegalArgumentException if the standard CODI properties file couldn't get found
     */
    public static String getCodiProperty(String propertyName) throws IOException
    {
        String value = null;
        Properties props = getProperties(CODI_PROPERTIES);

        if (props != null)
        {
            value = props.getProperty(propertyName);
        }

        return value;
    }

    public static ProjectStage getCurrentProjectStage()
    {
        return getOrCreateScopedInstanceOfBeanByClass(ProjectStage.class);
    }

    public static <T> T tryToInjectDependencies(T instance)
    {
        if(instance == null)
        {
            return null;
        }

        if(instance.getClass().isAnnotationPresent(Advanced.class))
        {
            injectFields(instance);
        }
        return instance;
    }

    /**
     * Performes dependency injection for objects which aren't know as bean
     *
     * @param instance the target instance
     * @param <T> generic type
     * @return an instance produced by the {@link javax.enterprise.inject.spi.BeanManager} or
     * a manually injected instance (or null if the given instance is null)
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T injectFields(T instance)
    {
        if(instance == null)
        {
            return null;
        }

        BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
        
        T foundBean = (T)getOrCreateScopedInstanceOfBeanByClass(instance.getClass(), true);

        if(foundBean != null)
        {
            return foundBean;
        }

        CreationalContext creationalContext = beanManager.createCreationalContext(null);

        AnnotatedType annotatedType = beanManager.createAnnotatedType(instance.getClass());
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(instance, creationalContext);
        return instance;
    }

    /*
     * source code from OWB
     *
     * TODO -=jakobk=- these methods rely on the String representation of the
     * Qualifiers, which is IMHO the wrong way. I think this should be changed
     * here and also in OWB!
     */
    //method from OWB AnnotationUtil#hasAnnotationMember - TODO test & refactor it
    public static boolean isQualifierEqual(Annotation sourceAnnotation, Annotation targetAnnotation)
    {
        Class<? extends Annotation> sourceAnnotationType = sourceAnnotation.annotationType();

        if (!sourceAnnotation.annotationType().equals(targetAnnotation.annotationType()))
        {
            return false;
        }

        Method[] methods = sourceAnnotationType.getDeclaredMethods();

        List<String> list = new ArrayList<String>();

        for (Method method : methods)
        {
            Annotation[] annots = method.getDeclaredAnnotations();

            if (annots.length > 0)
            {
                for (Annotation annot : annots)
                {
                    if (!annot.annotationType().equals(Nonbinding.class))
                    {
                        list.add(method.getName());
                    }
                }
            }
            else
            {
                list.add(method.getName());
            }
        }

        return checkEquality(sourceAnnotation.toString(), targetAnnotation.toString(), list);
    }
    
    //method from OWB AnnotationUtil#hasAnnotationMember - TODO test & refactor it
    private static boolean checkEquality(String src, String member, List<String> arguments)
    {
        if ((checkEquBuffer(src, arguments).toString().trim()
                .equals(checkEquBuffer(member, arguments).toString().trim())))
        {
            return true;
        }

        return false;
    }

    //method from OWB AnnotationUtil#hasAnnotationMember - TODO test & refactor it
    private static StringBuffer checkEquBuffer(String src, List<String> arguments)
    {
        int index = src.indexOf('(');

        String sbstr = src.substring(index + 1, src.length() - 1);

        StringBuffer srcBuf = new StringBuffer();

        StringTokenizer tok = new StringTokenizer(sbstr, ",");
        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();

            StringTokenizer tok2 = new StringTokenizer(token, "=");
            while (tok2.hasMoreElements())
            {
                String tt = tok2.nextToken();
                if (arguments.contains(tt.trim()))
                {
                    srcBuf.append(tt);
                    srcBuf.append("=");

                    if (tok2.hasMoreElements())
                    {
                        String str = tok2.nextToken();
                        if(str.charAt(0) == '"' && str.charAt(str.length() -1) == '"')
                        {
                            str = str.substring(1,str.length()-1);
                        }

                        srcBuf.append(str);
                    }
                }
            }

        }

        return srcBuf;
    }
}
