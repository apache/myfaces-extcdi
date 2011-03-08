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

Start the demo e.g. via:
mvn clean jetty:run-exploded -PjettyConfig -Dmaven.test.skip=true

this example also demonstrates cargo tests -> if a codi snapshot is build manuelly use (in the root of the project):
mvn clean install -Pextended-manifest -Ptest-infrastructure



build commands for this example:

build example with owb + myfaces-core2
mvn clean install

build example with owb + mojarra
mvn clean install -Denvironment=owb-mojarra

build example for a jee6 application server
mvn clean install -Denvironment=jee6


Only open and start this example outside of the rest of the project. That's required due to the missing shade support.

For remote debugging:
MAVEN_OPTS
-XX:MaxPermSize=256m -Xmx1024m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n
