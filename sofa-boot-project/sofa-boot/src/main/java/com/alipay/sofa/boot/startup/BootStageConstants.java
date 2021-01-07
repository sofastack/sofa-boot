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
package com.alipay.sofa.boot.startup;

import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author huzijie
 * @version BootStageConstants.java, v 0.1 2020年12月31日 11:40 上午 huzijie Exp $
 */
public class BootStageConstants {

    /**
     * The running stage since JVM started to {@link SpringApplicationRunListener#starting()}
     */
    public static final String JVM_STARTING_STAGE                = "JvmStartingStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#starting()} to
     * {@link SpringApplicationRunListener#environmentPrepared(ConfigurableEnvironment)}}
     */
    public static final String ENVIRONMENT_PREPARE_STAGE         = "EnvironmentPrepareStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#environmentPrepared(ConfigurableEnvironment)} to
     * {@link SpringApplicationRunListener#contextPrepared(ConfigurableApplicationContext)}}
     */
    public static final String APPLICATION_CONTEXT_PREPARE_STAGE = "ApplicationContextPrepareStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#contextPrepared(ConfigurableApplicationContext)} to
     * {@link SpringApplicationRunListener#contextLoaded(ConfigurableApplicationContext)}}
     */
    public static final String APPLICATION_CONTEXT_LOAD_STAGE    = "ApplicationContextLoadStage";

    /**
     * The running stage since {@link SpringApplicationRunListener#contextLoaded(ConfigurableApplicationContext)} to
     * StartupContextRefreshedListener.onApplicationEvent(ContextRefreshedEvent)
     */
    public static final String APPLICATION_CONTEXT_REFRESH_STAGE = "ApplicationContextRefreshStage";

    /**
     * The running stage for ModuleCreatingStage
     */
    public static final String ISLE_MODEL_CREATING_STAGE         = "ModelCreatingStage";

    /**
     * The running stage for SpringContextInstallStage
     */
    public static final String ISLE_SPRING_CONTEXT_INSTALL_STAGE = "SpringContextInstallStage";

    /**
     * The running stage for ReadinessCheckListener
     */
    public static final String HEALTH_CHECK_STAGE                = "HealthCheckStage";
}
