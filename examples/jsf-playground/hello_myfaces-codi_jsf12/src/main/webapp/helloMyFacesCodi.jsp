<%--
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<html>
    <head>
        <title>Hello World</title>
    </head>
    <body>
        <f:view>
            <h1 style="color:red">Dev-Demo (purpose: only for tests during development)</h1>

            <h1>JSF-Demo</h1>
            <h:form id="jsfMainForm">
                <h:panelGrid columns="2">
                    <h:outputLabel for="txtInfo" value="Bean-value:"/>
                    <h:outputText id="txtInfo" value="#{facesDemoBean.text}"/>

                    <h:commandButton value="send"/>
                    <h:panelGroup/>
                </h:panelGrid>
            </h:form>

            <h1>BV-Demo</h1>
            <h:form id="bvMainForm">
                <h:panelGrid columns="3">
                    <h:outputLabel for="txtInfo" value="Bean-value:"/>
                    <h:inputText id="txtInfo" value="#{beanValidationDemoBean.text}" label="Bean-value"/>
                    <h:message for="txtInfo" showSummary="false" showDetail="true" errorStyle="color: red;"/>

                    <h:commandButton value="send" action="#{beanValidationDemoBean.send}"/>
                    <h:panelGroup/>
                    <h:panelGroup/>
                </h:panelGrid>
            </h:form>

            <h:form id="conversationForm">
                <h:panelGrid>
                    <h:commandLink value="next" action="#{conversationDemoBean1.next}"/>
                    <h:commandLink value="close conversation group 1" action="#{conversationDemoBean1.endGroup1}"/>
                    <h:commandLink value="close conversation group 1 with qualifier" action="#{conversationDemoBean1.endGroup1WithQualifiers}"/>
                    <h:commandLink value="close conversation of bean" action="#{conversationDemoBean1.endConversation}"/>
                    <h:panelGroup/>
                    <h:commandLink value="close conversation of g1" action="#{conversationControllerDemoBean.closeGroup1}"/>
                    <h:commandLink value="close conversation on exception of g1" action="#{conversationControllerDemoBean.closeGroup1OnException}"/>
                </h:panelGrid>
            </h:form>
            
            <h:panelGrid>
                <h:outputText value="#{conversationDemoBean1.value}"/>
                <h:outputText value="#{conversationDemoBean2.value}"/>
                <h:outputText value="#{conversationDemoBean3.value}"/>
                <h:outputText value="#{conversationDemoBean4.value}"/>
                <h:outputText value="#{currentWindowContext.id}"/>
            </h:panelGrid>

            <h:messages globalOnly="true" infoStyle="color: blue;" warnStyle="color: orange;" errorStyle="color: red;" fatalStyle="color:darkred;"/>
        </f:view>
    </body>
</html>
