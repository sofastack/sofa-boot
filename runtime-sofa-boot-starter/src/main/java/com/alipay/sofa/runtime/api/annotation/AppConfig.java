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
package com.alipay.sofa.runtime.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Value;

/**
 * <p>
 * Annotation to get the whole SOFA configuration of current application or to get a specific configuration from the
 * SOFA configuration of current application.
 * </p>
 * <p>
 * Sample code of get the whole SOFA configuration of current application:
 *
 * <pre>
 *
 * public class SampleConfigurationBean implements AppConfigurationAware {
 *
 *     &#064;AppConfig
 *     private AppConfiguration appConf;
 *
 *     public String getWorldFormAnnotatedAppConfig() {
 *         return appConf.getPropertyValue(&quot;Hello&quot;);
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * Sample code of get a specific configuration from the SOFA configuration of current application:
 *
 * <pre>
 *
 * public class SampleConfigurationBean implements AppConfigurationAware {
 *
 *     &#064;AppConfig(&quot;Hello&quot;)
 *     private String world;
 *
 *     public String getWorld() {
 *         return world;
 *     }
 * }
 *
 * </pre>
 * </p>
 *
 * Deprecated, you should use {@link Value} instead.
 * @author xuanbei 18/3/5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface AppConfig {

    /**
     * The key of the configuration value to get from the SOFA configuration of current application. If this value is
     * not specified or is an empty string, {@link AppConfig} will inject to whole SOFA configuration of current
     * application to the annotated field.
     */
    String value() default "";
}
