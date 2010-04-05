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
package org.apache.myfaces.blank.bv;

import org.apache.myfaces.extensions.cdi.core.api.Advanced;

import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
@Model
public class BeanValidationDemoBean
{
    @NotNull(groups = TestGroup.class)
    private String welcomeText = "Hello MyFaces CODI!";

    @NotNull
    private String text;

    @NotNull
    private String forcedViolation;

    private Validator validator;

    private FacesContext facesContext;

    @Inject
    public BeanValidationDemoBean(@Advanced Validator validator, FacesContext facesContext)
    {
        this.validator = validator;
        this.facesContext = facesContext;
        performManualValidation(TestGroup.class);
    }

    /**
     * MyFaces ExtVal is used to autom. validate properties bound to the UI
     * This method just illustrates that it's possible to directly use the validator-factory provided by MyFaces CODI
     */
    public void send()
    {
        performManualValidation();
    }

    private void performManualValidation(Class... groups)
    {
        Set<ConstraintViolation<BeanValidationDemoBean>> violations = this.validator.validate(this, groups);

        if (!violations.isEmpty())
        {
            ConstraintViolation violation = violations.iterator().next();
            String message = "property: " + violation.getPropertyPath().toString()
                    + " - message: " + violation.getMessage();
            this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        }
    }

    /*
     * generated
     */
    protected BeanValidationDemoBean()
    {
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
