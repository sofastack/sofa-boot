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
package com.alipay.sofa.boot.listener;

import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * An {@link ApplicationListener} that could use property to dynamic enable listener.
 *
 * @author yuanxuan
 * @version : SwitchableApplicationListener.java, v 0.1 2023年02月09日 17:40 yuanxuan Exp $
 */
public abstract class SwitchableApplicationListener<E extends ApplicationEvent>
                                                                                implements
                                                                                ApplicationListener<E> {

    protected static final String CONFIG_KEY_PREFIX = "sofa.boot.switch.listener.";

    @Override
    public void onApplicationEvent(E event) {
        ApplicationContext applicationContext = null;
        Environment environment = null;
        if (event instanceof ApplicationContextEvent applicationContextEvent) {
            applicationContext = applicationContextEvent.getApplicationContext();
        } else if (event instanceof ApplicationContextInitializedEvent applicationContextInitializedEvent) {
            applicationContext = applicationContextInitializedEvent.getApplicationContext();
        } else if (event instanceof ApplicationEnvironmentPreparedEvent environmentPreparedEvent) {
            environment = environmentPreparedEvent.getEnvironment();
        } else if (event instanceof ApplicationPreparedEvent applicationPreparedEvent) {
            applicationContext = applicationPreparedEvent.getApplicationContext();
        } else if (event instanceof ApplicationReadyEvent applicationReadyEvent) {
            applicationContext = applicationReadyEvent.getApplicationContext();
        } else if (event instanceof ApplicationStartedEvent applicationStartedEvent) {
            applicationContext = applicationStartedEvent.getApplicationContext();
        } else if (event instanceof ApplicationFailedEvent applicationFailedEvent) {
            applicationContext = applicationFailedEvent.getApplicationContext();
        }
        if (environment == null && applicationContext != null) {
            environment = applicationContext.getEnvironment();
        }
        if (environment != null) {
            if (isEnable(environment)) {
                doOnApplicationEvent(event);
            }
        } else {
            doOnApplicationEvent(event);
        }
    }

    protected abstract void doOnApplicationEvent(E event);

    /**
     * sofa.boot.switch.listener
     *
     * @return switch key, must not be null.
     */
    protected abstract String switchKey();

    /**
     * Specify if the condition should match if the property is not set. Defaults to
     * {@code true}.
     *
     * @return if the condition should match if the property is missing
     */
    protected boolean matchIfMissing() {
        return true;
    }

    protected boolean isEnable(Environment environment) {
        String switchKey = switchKey();
        Assert.hasText(switchKey, "switch key must has text.");
        String realKey = CONFIG_KEY_PREFIX + switchKey + ".enabled";
        String switchStr = environment.getProperty(realKey);
        if (StringUtils.hasText(switchStr)) {
            return Boolean.parseBoolean(switchStr);
        } else {
            return matchIfMissing();
        }
    }
}
