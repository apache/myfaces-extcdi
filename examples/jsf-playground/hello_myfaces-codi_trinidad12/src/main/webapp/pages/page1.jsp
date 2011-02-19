<?xml version="1.0" encoding="iso-8859-1" standalone="yes" ?>
<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

-->
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns:tr="http://myfaces.apache.org/trinidad">
    <jsp:directive.page contentType="text/html;charset=utf-8"/>
    <f:view>
        <tr:document title="Apache MyFaces CODI + Trinidad Demo">
            <h1 style="color:red">Dev-Demo (purpose: only for tests during development)</h1>
            <tr:form>

                <tr:panelPage>
                    <tr:inputText label="Your name" id="input1" value="#{demoBean.name}"/>
                    <tr:commandButton id="button1" text="press me" action="#{demoBean.send}"/>
                </tr:panelPage>

            </tr:form>
        </tr:document>
    </f:view>
</jsp:root>