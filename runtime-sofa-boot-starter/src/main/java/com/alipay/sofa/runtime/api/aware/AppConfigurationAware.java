/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.runtime.api.aware;

import com.alipay.sofa.runtime.api.component.AppConfiguration;
import org.springframework.beans.factory.annotation.Value;

/**
 * Interface used to implemented by a Spring bean who want to get the SOFA configuration of current application. Sample
 * usage:
 *
 * <pre>
 * public class SampleConfigurationBean implements AppConfigurationAware {
 *
 *     private AppConfiguration appConfiguration;
 *
 *     &#064;Override
 *     public void setAppConfiguration(AppConfiguration appConfiguration) {
 *         this.appConfiguration = appConfiguration;
 *     }
 * }
 * </pre>
 *
 * Deprecated, you should use {@link Value} instead
 * @author xuanbei 18/3/5
 */
@Deprecated
public interface AppConfigurationAware {

    /**
     * Set the SOFA configuration of current application to the Spring bean implemented this class
     *
     * @param appConfiguration The SOFA configuration of current application.
     */
    void setAppConfiguration(AppConfiguration appConfiguration);
}
