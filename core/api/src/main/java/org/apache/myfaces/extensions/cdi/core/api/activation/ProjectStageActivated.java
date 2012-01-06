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
package org.apache.myfaces.extensions.cdi.core.api.activation;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Use this annotation, e.g. in conjunction with {@link javax.enterprise.inject.Alternative}
 * to 'disable' the &#064;Alternative based on the current ProjectStage.</p>
 * <p>Please note that you still have to add all alternative beans to the respective
 * beans.xml &lt;alternatives&gt; section.</p>
 * <p>&#064;ProjectStageActivated can also be used to veto other kind of beans.</p>
 *
 * <p>Example Usage:</p>
 * <pre>
 * &#064;Alternative
 * &#064;ProjectStageActivated({ProjectStage.Development.class, ProjectStage.UnitTest.class})
 * &#064;ApplicationScoped
 * public class MockMailServiceImpl implements MailService {...}
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ProjectStageActivated 
{
    /**
     * The {@link org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage}s
     * which lead to activating this alternative bean.
     * If the current ProjectStage is not in this list, the bean will get vetoed.
     * @return 1-n project-stages which are allowed for the annotated artifact
     */
    Class<? extends ProjectStage>[] value();
}
