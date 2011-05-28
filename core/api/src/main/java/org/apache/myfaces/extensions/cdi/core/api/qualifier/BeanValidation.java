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
package org.apache.myfaces.extensions.cdi.core.api.qualifier;

import javax.inject.Qualifier;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Qualifier which allows to decouple the bean-validation module and the other modules like the jsf module.
 *
 * @author Gerhard Petracek
 */
@Target({PARAMETER, FIELD, METHOD, CONSTRUCTOR, TYPE})
@Retention(RUNTIME)
@Documented

//cdi annotations
@Qualifier
public @interface BeanValidation
{
    /**
     * Enum which specifies the current artifact-type
     */
    public enum ArtifactType
    {
        ValidatorFactory, Validator, ConstraintValidatorFactory, MessageInterpolator, TraversableResolver
    }

    /**
     * Details of the qualifier
     * @return type which allows different qualifiers with one annotation
     */
    ArtifactType value() default ArtifactType.ValidatorFactory;
}
