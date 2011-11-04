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
package org.apache.myfaces.examples.codi.jsf12.conversation.grouped.qualifier;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.inject.Typed;

/**
 * optional
 *
 * Class which allows instances of a qualifier-annotation
 */
@Typed()
public class QualifierInstances
{
    private QualifierInstances()
    {
    }

    private static class QualifierAnnotation extends AnnotationLiteral<Qualifier3> implements Qualifier3
    {
        private static final long serialVersionUID = 6638619823102047921L;
    }

    public static Qualifier3 qualifier3()
    {
        return new QualifierAnnotation();
    }
}
