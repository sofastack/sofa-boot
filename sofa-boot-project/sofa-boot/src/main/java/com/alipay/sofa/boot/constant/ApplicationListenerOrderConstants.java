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
package com.alipay.sofa.boot.constant;

import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * ApplicationListener 顺序常量
 *
 * @author xunfang
 * @version ApplicationListenerOrderConstants.java, v 0.1 2023/6/1
 */
public class ApplicationListenerOrderConstants {

    // ApplicationEnvironmentPreparedEvent order start
    /**
     * 必须最先执行
     */
    public static final int SOFA_BOOTSTRAP_RUN_LISTENER_ORDER         = Ordered.HIGHEST_PRECEDENCE;

    /**
     * LogEnvironmentPostProcessor 在此阶段初始化，Ordered.HIGHEST_PRECEDENCE + 10
     */
    public static final int ENVIRONMENT_POST_PROCESSOR_LISTENER_ORDER = EnvironmentPostProcessorApplicationListener.DEFAULT_ORDER;

    /**
     * 必须在 LogEnvironmentPreparingListener 之后, 且 LoggingApplicationListener 之前，Ordered.HIGHEST_PRECEDENCE + 13
     */
    public static final int SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER = ENVIRONMENT_POST_PROCESSOR_LISTENER_ORDER + 3;

    public static final int SOFA_TRACER_CONFIGURATION_LISTENER_ORDER  = Ordered.HIGHEST_PRECEDENCE + 50;

    static {
        Assert
            .isTrue(
                ENVIRONMENT_POST_PROCESSOR_LISTENER_ORDER < SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER,
                "ENVIRONMENT_POST_PROCESSOR_LISTENER_ORDER must init before SofaConfigSourceSupportListener");
    }

    // ApplicationEnvironmentPreparedEvent order end
}
