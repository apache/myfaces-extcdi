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
package org.apache.myfaces.extensions.cdi.core.test.impl.utils;

import org.apache.myfaces.extensions.cdi.core.api.tools.DefaultAnnotation;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Test class for CodiUtils.
 *
 * @author Jakob Korherr
 */
public class CodiUtilsTest
{

    @Test
    public void test_isQualifierEqual_DefaultAnnotation_emptyQualifier()
    {
        EmptyQualifier q1 = DefaultAnnotation.of(EmptyQualifier.class);
        EmptyQualifier q2 = DefaultAnnotation.of(EmptyQualifier.class);

        Assert.assertTrue(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_DefaultAnnotation_AnnotationLiteral_emptyQualifier()
    {
        EmptyQualifier q1 = DefaultAnnotation.of(EmptyQualifier.class);
        EmptyQualifier q2 = new EmptyQualifierAnnotationLiteral();

        Assert.assertTrue(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_DefaultAnnotation_nonEmptyQualifier()
    {
        TestQualifier q1 = DefaultAnnotation.of(TestQualifier.class);
        TestQualifier q2 = DefaultAnnotation.of(TestQualifier.class);

        Assert.assertTrue(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_DefaultAnnotation_AnnotationLiteral_nonEmptyQualifier()
    {
        TestQualifier q1 = DefaultAnnotation.of(TestQualifier.class);
        TestQualifier q2 = new TestQualifierAnnotationLiteral();

        Assert.assertTrue(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_AnnotationLiteral_nonEmptyQualifier()
    {
        TestQualifier q1 = new TestQualifierAnnotationLiteral();
        TestQualifier q2 = new TestQualifierAnnotationLiteral();

        Assert.assertTrue(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_AnnotationLiteral_Different_String()
    {
        TestQualifier q1 = new TestQualifierAnnotationLiteral();
        TestQualifierAnnotationLiteral q2 = new TestQualifierAnnotationLiteral();
        
        q2.setValue("different value");

        Assert.assertFalse(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_AnnotationLiteral_Different_int()
    {
        TestQualifier q1 = new TestQualifierAnnotationLiteral();
        TestQualifierAnnotationLiteral q2 = new TestQualifierAnnotationLiteral();

        q2.setNumber(4711);

        Assert.assertFalse(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_AnnotationLiteral_Different_array()
    {
        TestQualifier q1 = new TestQualifierAnnotationLiteral();
        TestQualifierAnnotationLiteral q2 = new TestQualifierAnnotationLiteral();

        q2.setFloatArray(new float[]{47F, 11F});

        Assert.assertFalse(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_AnnotationLiteral_Different_Enum()
    {
        TestQualifier q1 = new TestQualifierAnnotationLiteral();
        TestQualifierAnnotationLiteral q2 = new TestQualifierAnnotationLiteral();

        q2.setEnumValue(RetentionPolicy.SOURCE);

        Assert.assertFalse(CodiUtils.isQualifierEqual(q1, q2));
    }

    @Test
    public void test_isQualifierEqual_AnnotationLiteral_Nonbinding_Different()
    {
        TestQualifierNonbinding q1 = DefaultAnnotation.of(TestQualifierNonbinding.class);
        TestQualifierNonbinding q2 = new TestQualifierNonbindingAnnotationLiteral();

        Assert.assertTrue(CodiUtils.isQualifierEqual(q1, q2));
    }

}

@Retention(RUNTIME)
@Qualifier
@interface EmptyQualifier
{

}

class EmptyQualifierAnnotationLiteral
        extends AnnotationLiteral<EmptyQualifier>
        implements EmptyQualifier
{
}

@Retention(RUNTIME)
@Qualifier
@interface TestQualifier
{

    String value() default "default-value";

    int number() default -1;

    float[] floatArray() default {1.0F, 1.2F};

    RetentionPolicy enumValue() default RetentionPolicy.RUNTIME;

}

class TestQualifierAnnotationLiteral
        extends AnnotationLiteral<TestQualifier>
        implements TestQualifier
{

    // default values
    private String value = "default-value";
    private int number = -1;
    private float[] floatArray = new float[]{1.0F, 1.2F};
    private RetentionPolicy enumValue = RetentionPolicy.RUNTIME;

    // annotation methods

    public String value()
    {
        return value;
    }

    public int number()
    {
        return number;
    }

    public float[] floatArray()
    {
        return floatArray;
    }

    public RetentionPolicy enumValue()
    {
        return enumValue;
    }

    // setter

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public void setFloatArray(float[] floatArray)
    {
        this.floatArray = floatArray;
    }

    public void setEnumValue(RetentionPolicy enumValue)
    {
        this.enumValue = enumValue;
    }

}

@Retention(RUNTIME)
@Qualifier
@interface TestQualifierNonbinding
{

    String value() default "default-value";

    @MyCustomAnnotation // to show that there can be more than one annotation here
    @Nonbinding
    int number() default -1;

}

@Retention(RUNTIME)
@interface MyCustomAnnotation
{
}

class TestQualifierNonbindingAnnotationLiteral
        extends AnnotationLiteral<TestQualifierNonbinding>
        implements TestQualifierNonbinding
{

    public String value()
    {
        return "default-value";
    }

    public int number()
    {
        return 4711;
    }
}
