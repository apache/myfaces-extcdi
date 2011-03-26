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
package org.apache.myfaces.extensions.cdi.test.cargo;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.myfaces.extensions.cdi.core.api.UnhandledException;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigCache;
import org.apache.myfaces.extensions.cdi.jsf.impl.config.view.ViewConfigExtension;
import org.apache.myfaces.extensions.cdi.test.cargo.strategy.AbstractSimpleCargoTestStrategy;
import org.apache.myfaces.extensions.cdi.test.strategy.AbstractJsfAwareTestStrategy;

import javax.enterprise.inject.Typed;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Gerhard Petracek
 */
@Typed()
public class SimplePageInteraction
{
    private static final Logger LOGGER = Logger.getLogger(SimplePageInteraction.class.getName());

    private WebClient webClient;

    private String baseURL;

    private HtmlPage currentPage;
    private HtmlForm currentForm;

    private String initialWindowId;
    private String okButtonId = "ok";
    private boolean checkWindowId;
    private boolean defaultFormActive = true;

    public SimplePageInteraction(TestConfiguration testConfiguration)
    {
        this.webClient = testConfiguration.getWebClient();
        this.baseURL = testConfiguration.getBaseURL();
        this.checkWindowId = testConfiguration.isCheckWindowId();
    }

    public SimplePageInteraction with(Class<? extends ViewConfig> pageDefinition)
    {
        checkUsage();
        new ViewConfigExtension()
        {
            @Override
            public void addPageDefinition(Class pageDefinitionClass)
            {
                super.addPageDefinition(pageDefinitionClass);
            }
        }.addPageDefinition(pageDefinition);
        return this;
    }

    protected void checkUsage()
    {
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        RuntimeException runtimeException = new RuntimeException();

        StackTraceElement[] stackTrace = runtimeException.getStackTrace();

        Class currentClass;
        for(StackTraceElement element : stackTrace)
        {
            currentClass = ClassUtils.tryToLoadClassForName(element.getClassName());
            if(currentClass == null)
            {
                continue;
            }

            if(AbstractJsfAwareTestStrategy.class.isAssignableFrom(currentClass) &&
                    !AbstractSimpleCargoTestStrategy.class.isAssignableFrom(currentClass))
            {
                LOGGER.warning(getClass().getName() + "#with is only required for tests which extend " +
                    AbstractSimpleCargoTestStrategy.class.getName() + ". It's used with " + element.getClassName());
                return;
            }
        }
    }

    public SimplePageInteraction start(Class<? extends ViewConfig> pageDefinition)
    {
        this.currentPage = getPage(pageDefinition);
        this.initialWindowId = getCurrentWindowId();

        return this;
    }

    public SimplePageInteraction useDefaultForm()
    {
        if(this.defaultFormActive)
        {
            return useForm("mainForm");
        }
        return this;
    }

    public SimplePageInteraction useForm(String formId)
    {
        this.currentForm = this.currentPage.getFormByName(formId);
        this.defaultFormActive = false;
        return this;
    }

    public SimplePageInteraction useIdForOkButton(String okButtonId)
    {
        this.okButtonId = okButtonId;
        return this;
    }

    public SimplePageInteraction setValue(String inputId, String value)
    {
        setInputValue(this.currentPage, inputId, value);
        return this;
    }

    public SimplePageInteraction clickOk()
    {
        return clickOk(null);
    }

    public SimplePageInteraction clickOk(Class<? extends ViewConfig> expectedPage)
    {
        click(this.okButtonId, viewId(expectedPage));

        if(expectedPage != null)
        {
            checkCurrentPage(expectedPage);
        }

        return this;
    }

    public SimplePageInteraction click(String id)
    {
        click(id, null);
        return this;
    }

    public SimplePageInteraction checkState(Class<? extends ViewConfig> expectedPage)
    {
        checkCurrentPage(expectedPage);
        checkCurrentWindowId();
        return this;
    }

    public SimplePageInteraction checkCurrentPage(Class<? extends ViewConfig> expectedPage)
    {
        assertTrue(url(this.currentPage).contains(viewId(expectedPage)));
        return this;
    }

    public SimplePageInteraction checkCurrentWindowId()
    {
        if(this.checkWindowId)
        {
            assertEquals(this.initialWindowId, getCurrentWindowId());
        }
        return this;
    }

    public SimplePageInteraction checkTextValue(String inputId, String expectedValue)
    {
        return checkValue(inputId, expectedValue, true);
    }

    public SimplePageInteraction checkInputValue(String inputId, String expectedValue)
    {
        return checkValue(inputId, expectedValue, false);
    }

    protected SimplePageInteraction checkValue(String inputId, String expectedValue, boolean outputText)
    {
        assertEquals(expectedValue, getValue(this.currentPage, inputId, outputText));
        return this;
    }

    protected String getValue(HtmlPage htmlPage, String id, boolean outputText)
    {
        if(outputText)
        {
            return htmlPage.getElementById(id).getTextContent();
        }
        return htmlPage.getElementById(id).getAttribute("value");
    }

    protected void setInputValue(HtmlPage htmlPage, String inputId, String value)
    {
        setInputValue(htmlPage, this.currentForm, inputId, value);
    }

    protected void setInputValue(HtmlPage htmlPage, HtmlForm htmlForm, String inputId, String value)
    {
        try
        {
            htmlForm.getInputByName(inputId).setValueAttribute(value);
        }
        catch (ElementNotFoundException e)
        {
            ((HtmlInput)htmlPage.getElementById(inputId)).setValueAttribute(value);
        }
    }

    protected String getCurrentWindowId()
    {
        //TODO
        return this.currentPage.getUrl().getQuery();
    }

    protected HtmlPage getPage(Class<? extends ViewConfig> pageDefinition)
    {
        return getPage(ViewConfigCache.getViewConfig(pageDefinition).getViewId());
    }

    protected HtmlPage getPage(String viewId)
    {
        try
        {
            if(viewId.startsWith("/"))
            {
                viewId = viewId.substring(1);
            }

            return this.webClient.getPage(this.baseURL + viewId);
        }
        catch (IOException e)
        {
            if(e instanceof HttpHostConnectException)
            {
                throw new ContainerNotStartedException(e);
            }
            else
            {
                throw new UnhandledException(e);
            }
        }
    }

    protected void click(String id, String expectedTarget)
    {
        try
        {
            HtmlInput commandNode;

            try
            {
                commandNode = this.currentForm.getInputByName(id);
            }
            catch (ElementNotFoundException e)
            {
                //in case of get-requests it isn't required that the element is in a form
                commandNode = this.currentPage.getHtmlElementById(id);
            }

            this.currentPage = commandNode.click();
            useDefaultForm();

            if(expectedTarget != null)
            {
                assertTrue(url(this.currentPage).contains(expectedTarget));
            }
        }
        catch (IOException e)
        {
            throw new UnhandledException(e);
        }
    }

    protected String viewId(Class<? extends ViewConfig> pageDefinition)
    {
        if(pageDefinition == null)
        {
            return null;
        }
        return ViewConfigCache.getViewConfig(pageDefinition).getViewId();
    }

    protected String url(HtmlPage htmlPage)
    {
        return htmlPage.getUrl().toString();
    }
}
