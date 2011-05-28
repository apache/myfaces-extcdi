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

import javax.enterprise.util.AnnotationLiteral;

/**
 * Literal for {@link BeanValidation}
 *
 * @author Gerhard Petracek
 */
public class BeanValidationLiteral extends AnnotationLiteral<BeanValidation> implements BeanValidation
{
    private static final long serialVersionUID = -69792029466966772L;

    private final ArtifactType value;

    /**
     * Default constructor which uses {@link ArtifactType#ValidatorFactory} as value
     */
    public BeanValidationLiteral()
    {
        this.value = ArtifactType.ValidatorFactory;
    }

    /**
     * Constructor which allows to use a custom value
     * @param value value which represents the bv artifact
     */
    public BeanValidationLiteral(ArtifactType value)
    {
        this.value = value;
    }

    /**
     * @return value which is represented by this instance
     */
    public ArtifactType value()
    {
        return this.value;
    }
}
