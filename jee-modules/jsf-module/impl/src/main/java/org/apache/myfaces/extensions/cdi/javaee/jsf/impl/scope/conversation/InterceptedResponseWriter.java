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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager;
import org.apache.myfaces.extensions.cdi.core.impl.utils.CodiUtils;
import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils;

import javax.enterprise.inject.spi.Bean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Adds
 * {@link WindowContextIdHolderComponent}
 * to the tree before the call of {@link #startDocument}
 *
 * @author Gerhard Petracek
 */
class InterceptedResponseWriter extends ResponseWriter
{
    private ResponseWriter wrapped;

    InterceptedResponseWriter(ResponseWriter wrapped)
    {
        this.wrapped = wrapped;
    }

    private boolean isWindowContextIdHolderComponentAvailable()
    {
        List<UIComponent> uiComponents = FacesContext.getCurrentInstance().getViewRoot().getChildren();
        for (UIComponent uiComponent : uiComponents)
        {
            if (uiComponent instanceof WindowContextIdHolderComponent)
            {
                return true;
            }
        }

        return false;
    }

    private void addWindowContextIdHolderComponent()
    {
        FacesContext.getCurrentInstance().getViewRoot().getChildren()
                .add(createComponentWithCurrentConversationContextId());
    }

    private WindowContextIdHolderComponent createComponentWithCurrentConversationContextId()
    {
        Bean<WindowContextManager> conversationManagerBean = ConversationUtils.resolveConversationManagerBean();

        WindowContextManager conversationManager = CodiUtils.getOrCreateScopedInstanceOfBean(conversationManagerBean);

        return new WindowContextIdHolderComponent(conversationManager.getCurrentWindowContext().getId());
    }

    public String getContentType()
    {
        return wrapped.getContentType();
    }

    public String getCharacterEncoding()
    {
        return wrapped.getCharacterEncoding();
    }

    public void flush()
            throws IOException
    {
        wrapped.flush();
    }

    public void startDocument()
            throws IOException
    {
        if (!isWindowContextIdHolderComponentAvailable())
        {
            addWindowContextIdHolderComponent();
        }
        wrapped.startDocument();
    }

    public void endDocument()
            throws IOException
    {
        wrapped.endDocument();
    }

    public void startElement(String s, UIComponent uiComponent)
            throws IOException
    {
        wrapped.startElement(s, uiComponent);
    }

    public void endElement(String s)
            throws IOException
    {
        wrapped.endElement(s);
    }

    public void writeAttribute(String s, Object o, String s1)
            throws IOException
    {
        wrapped.writeAttribute(s, o, s1);
    }

    public void writeURIAttribute(String s, Object o, String s1)
            throws IOException
    {
        wrapped.writeURIAttribute(s, o, s1);
    }

    public void writeComment(Object o)
            throws IOException
    {
        wrapped.writeComment(o);
    }

    public void writeText(Object o, String s)
            throws IOException
    {
        wrapped.writeText(o, s);
    }

    public void writeText(char[] chars, int i, int i1)
            throws IOException
    {
        wrapped.writeText(chars, i, i1);
    }

    public ResponseWriter cloneWithWriter(Writer writer)
    {
        return wrapped.cloneWithWriter(writer);
    }

    public void writeText(Object o, UIComponent uiComponent, String s)
            throws IOException
    {
        wrapped.writeText(o, uiComponent, s);
    }

    public void close()
            throws IOException
    {
        wrapped.close();
    }

    public Writer append(char c)
            throws IOException
    {
        return wrapped.append(c);
    }

    public Writer append(CharSequence csq, int start, int end)
            throws IOException
    {
        return wrapped.append(csq, start, end);
    }

    public Writer append(CharSequence csq)
            throws IOException
    {
        return wrapped.append(csq);
    }

    public void write(String str, int off, int len)
            throws IOException
    {
        wrapped.write(str, off, len);
    }

    public void write(String str)
            throws IOException
    {
        wrapped.write(str);
    }

    public void write(char[] cbuf, int off, int len)
            throws IOException
    {
        wrapped.write(cbuf, off, len);
    }

    public void write(char[] cbuf)
            throws IOException
    {
        wrapped.write(cbuf);
    }

    public void write(int c)
            throws IOException
    {
        wrapped.write(c);
    }
}
