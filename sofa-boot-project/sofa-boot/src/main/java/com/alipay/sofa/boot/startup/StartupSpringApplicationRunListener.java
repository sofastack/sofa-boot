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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link SpringApplicationRunListener} to compute startup stages cost time.
 *
 * @author Zhijie
 * @since 2020/7/20
 */
public class StartupSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {

    private final SpringApplication     application;

    private final String[]              args;

    private final StartupReporter       startupReporter;

    private final ApplicationStartup    userApplicationStartup;

    private BufferingApplicationStartup applicationStartup;

    private BaseStat                    jvmStartingStage;

    private BaseStat                    environmentPrepareStage;

    private ChildrenStat<BaseStat>      applicationContextPrepareStage;

    private BaseStat                    applicationContextLoadStage;

    public StartupSpringApplicationRunListener(SpringApplication springApplication, String[] args) {
        this.application = springApplication;
        this.args = args;
        this.startupReporter = new StartupReporter();
        this.userApplicationStartup = springApplication.getApplicationStartup();
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        jvmStartingStage = new BaseStat();
        jvmStartingStage.setName(BootStageConstants.JVM_STARTING_STAGE);
        jvmStartingStage.setStartTime(ManagementFactory.getRuntimeMXBean().getStartTime());
        jvmStartingStage.setEndTime(System.currentTimeMillis());
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
                                    ConfigurableEnvironment environment) {
        environmentPrepareStage = new BaseStat();
        environmentPrepareStage.setName(BootStageConstants.ENVIRONMENT_PREPARE_STAGE);
        environmentPrepareStage.setStartTime(jvmStartingStage.getEndTime());
        environmentPrepareStage.setEndTime(System.currentTimeMillis());
        startupReporter.setAppName(environment.getProperty(SofaBootConstants.APP_NAME_KEY));
        startupReporter.bindToStartupReporter(environment);

        // create BufferingApplicationStartup if user not custom.
        if (ApplicationStartup.DEFAULT == userApplicationStartup || userApplicationStartup == null) {
            this.applicationStartup = new BufferingApplicationStartup(startupReporter.getBufferSize());
        } else if (userApplicationStartup instanceof BufferingApplicationStartup userApplicationStartup) {
            // use user custom BufferingApplicationStartup
            this.applicationStartup = userApplicationStartup;
        } else {
            // disable startup static when user custom other type ApplicationStartup;
            this.applicationStartup = null;
        }
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        applicationContextPrepareStage = new ChildrenStat<>();
        applicationContextPrepareStage
            .setName(BootStageConstants.APPLICATION_CONTEXT_PREPARE_STAGE);
        applicationContextPrepareStage.setStartTime(environmentPrepareStage.getEndTime());
        applicationContextPrepareStage.setEndTime(System.currentTimeMillis());
        if (application instanceof StartupSpringApplication startupSpringApplication) {
            List<BaseStat> baseStatList = startupSpringApplication.getInitializerStartupStatList();
            applicationContextPrepareStage.setChildren(new ArrayList<>(baseStatList));
            baseStatList.clear();
        }
        if (applicationStartup != null) {
            context.setApplicationStartup(applicationStartup);
        }
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        applicationContextLoadStage = new BaseStat();
        applicationContextLoadStage.setName(BootStageConstants.APPLICATION_CONTEXT_LOAD_STAGE);
        applicationContextLoadStage.setStartTime(applicationContextPrepareStage.getEndTime());
        applicationContextLoadStage.setEndTime(System.currentTimeMillis());
        context.getBeanFactory().addBeanPostProcessor(
            new StartupReporterBeanPostProcessor(startupReporter));
        context.getBeanFactory().registerSingleton("STARTUP_REPORTER_BEAN", startupReporter);
        StartupSmartLifecycle startupSmartLifecycle = new StartupSmartLifecycle(startupReporter);
        startupSmartLifecycle.setApplicationContext(context);
        context.getBeanFactory().registerSingleton("STARTUP_SMART_LIfE_CYCLE",
            startupSmartLifecycle);
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        // refresh applicationRefreshStage
        ChildrenStat<ModuleStat> applicationRefreshStage = (ChildrenStat<ModuleStat>) startupReporter
            .getStageNyName(BootStageConstants.APPLICATION_CONTEXT_REFRESH_STAGE);
        applicationRefreshStage.setStartTime(applicationContextLoadStage.getEndTime());
        applicationRefreshStage.setCost(applicationRefreshStage.getEndTime()
                                        - applicationRefreshStage.getStartTime());

        // init rootModuleStat
        ModuleStat rootModule = applicationRefreshStage.getChildren().get(0);
        rootModule.setStartTime(applicationRefreshStage.getStartTime());
        rootModule.setCost(rootModule.getEndTime() - rootModule.getStartTime());

        // report all stage
        startupReporter.addCommonStartupStat(jvmStartingStage);
        startupReporter.addCommonStartupStat(environmentPrepareStage);
        startupReporter.addCommonStartupStat(applicationContextPrepareStage);
        startupReporter.addCommonStartupStat(applicationContextLoadStage);
        startupReporter.applicationBootFinish();

        SofaBootLoggerFactory.getLogger(StartupSpringApplicationRunListener.class).info(
            getStartedMessage(context.getEnvironment(), timeTaken));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private String getStartedMessage(Environment environment, Duration timeTakenToStartup) {
        StringBuilder message = new StringBuilder();
        message.append("Started ");
        message.append(environment.getProperty(SofaBootConstants.APP_NAME_KEY));
        String startupLogExtraInfo = environment
            .getProperty(SofaBootConstants.STARTUP_LOG_EXTRA_INFO);
        if (StringUtils.hasText(startupLogExtraInfo)) {
            message.append(" with extra info [");
            message.append(startupLogExtraInfo);
            message.append("]");
        }
        message.append(" in ");
        message.append(timeTakenToStartup.toMillis() / 1000.0);
        message.append(" seconds");
        return message.toString();
    }
}
