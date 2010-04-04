/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.myfaces.extensions.cdi.core.test.api.tools;

import java.lang.annotation.Annotation;
import org.apache.myfaces.extensions.cdi.core.api.tools.annotate.DefaultAnnotation;
import org.testng.Assert;
import org.testng.annotations.Test;


public class DefaultAnnotationTest {

    @Test
    public void testDefaultAnnotationCache() throws Exception 
    {
        Annotation ann1a = DefaultAnnotation.of(MyAnnotation1.class);
        Assert.assertNotNull(ann1a);
        
        Annotation ann1b = DefaultAnnotation.of(MyAnnotation1.class);
        Assert.assertNotNull(ann1b);
        
        Assert.assertTrue(ann1a == ann1b);
        
        MyAnnotation2 ann2 = (MyAnnotation2) DefaultAnnotation.of(MyAnnotation2.class);
        Assert.assertNotNull(ann2);
        Assert.assertTrue(ann2.qualifier() == MyAnnotation1.class);
    }
    
    
    public static @interface MyAnnotation1 
    {
    }
    
    public static @interface MyAnnotation2 
    {
        Class<? extends Annotation> qualifier() default MyAnnotation1.class;
    }
}
