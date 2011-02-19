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
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
<head>
    <title>Client-Side Window-Handler Demo</title>
</head>
<body>
<f:view>
    <h1 style="color:red">Dev-Demo (purpose: only for tests during development)</h1>
    
    <h1>Client-Side Window-Handler Demo</h1>
    <h:form>
        <h:panelGrid columns="3">
            <h:outputText value="Please enter your name"/>
            <h:inputText value="#{helloWorldBacking.name}"/>
            <h:commandButton value="next" action="#{helloWorldBacking.next}"/>
        </h:panelGrid>
    </h:form>
    <h:messages globalOnly="true" showDetail="true" showSummary="false" errorStyle="color: red;"/>
</f:view>
</body>
</html>
