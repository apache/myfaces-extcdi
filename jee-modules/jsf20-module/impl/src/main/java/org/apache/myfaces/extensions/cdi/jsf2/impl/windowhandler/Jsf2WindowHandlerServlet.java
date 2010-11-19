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
package org.apache.myfaces.extensions.cdi.jsf2.impl.windowhandler;

import org.apache.myfaces.extensions.cdi.core.api.provider.BeanManagerProvider;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Set;

/**
 * Serves the windowhandler.html from the resource path
 */
@WebServlet(urlPatterns = {Jsf2WindowHandlerServlet.WINDOWHANDLER_URL})
public class Jsf2WindowHandlerServlet extends HttpServlet
{
    private static final long serialVersionUID = 6109043260242858474L;

    public final static String URL_PARAM = "url";
    public final static String WINDOWHANDLER_URL = "/windowhandler";

    private ClientInformation clientInformation;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        if (!isJavaScriptEnabled())
        {
            String url = req.getParameter(URL_PARAM);
            if (url == null)
            {
                throw new ServletException("could not find url parameter!");
            }
            url = URLDecoder.decode(url, "UTF-8");
            resp.sendRedirect(url);
            return;
        }

        sendWindowHandler(req, resp);
    }

    private void sendWindowHandler(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html");

        InputStream is = ClassUtils.getClassLoader(null).getResourceAsStream("static/windowhandler.html");
        OutputStream os = resp.getOutputStream();
        try
        {
            byte[] buf = new byte[16 * 4096];
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1)
            {
                os.write(buf, 0, bytesRead);
            }
        }
        finally
        {
            is.close();
            os.close();
        }
    }

    private boolean isJavaScriptEnabled()
    {
        if (clientInformation == null)
        {
            //TODO use CodiUtils
            BeanManager beanManager = BeanManagerProvider.getInstance().getBeanManager();
            Set<Bean<?>> beans = beanManager.getBeans(ClientInformation.class);
            Bean<?> clientInformationBean = beanManager.resolve(beans);
            CreationalContext<?> cc = beanManager.createCreationalContext(clientInformationBean);
            clientInformation = (ClientInformation) beanManager
                    .getReference(clientInformationBean, ClientInformation.class, cc);
        }

        return clientInformation.isJavaScriptEnabled();
    }
}
