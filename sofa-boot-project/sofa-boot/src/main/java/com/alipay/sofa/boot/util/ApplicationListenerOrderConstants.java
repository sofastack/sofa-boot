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
package com.alipay.sofa.boot.util;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * ApplicationListener 顺序常量
 *
 * @author xunfang
 * @version ApplicationListenerOrderConstants.java, v 0.1 2023/6/1
 */
public class ApplicationListenerOrderConstants {
    /**
     * 必须最先执行
     */
    public static final int SOFA_BOOTSTRAP_RUN_LISTENER_ORDER         = Ordered.HIGHEST_PRECEDENCE;

    /**
     * 必须在其他会触发 sofa-common-tools 日志上下文初始化的组件之前
     */
    public static final int LOG_ENVIRONMENT_PREPARING_LISTENER_ORDER  = ConfigFileApplicationListener.DEFAULT_ORDER + 2;

    /**
     * 必须在 LogEnvironmentPreparingListener 之后, 且 LoggingApplicationListener 之前
     */
    public static final int SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER = LOG_ENVIRONMENT_PREPARING_LISTENER_ORDER + 3;

    public static final int SOFA_TRACER_CONFIGURATION_LISTENER_ORDER  = Ordered.HIGHEST_PRECEDENCE + 30;

    static {
        Assert
            .isTrue(SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER < LoggingApplicationListener.DEFAULT_ORDER);
    }
}
