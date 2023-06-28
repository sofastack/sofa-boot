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
package com.alipay.sofa.boot.Initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * An {@link ApplicationContextInitializer} that could use property to dynamic enable initializer.
 *
 * @author yuanxuan
 * @version : SwitchableApplicationContextInitializer.java, v 0.1 2023年02月09日 11:57 yuanxuan Exp $
 */
public abstract class SwitchableApplicationContextInitializer
                                                             implements
                                                             ApplicationContextInitializer<ConfigurableApplicationContext> {

    protected static final String CONFIG_KEY_PREFIX = "sofa.boot.switch.initializer.";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (isEnable(applicationContext)) {
            doInitialize(applicationContext);
        }
    }

    /**
     * @param applicationContext
     */
    protected abstract void doInitialize(ConfigurableApplicationContext applicationContext);

    /**
     * start with :sofa.boot.switch.initializer
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

    protected boolean isEnable(ConfigurableApplicationContext applicationContext) {
        String switchKey = switchKey();
        Assert.hasText(switchKey, "switch key must has text.");
        String realKey = CONFIG_KEY_PREFIX + switchKey + ".enabled";
        String switchStr = applicationContext.getEnvironment().getProperty(realKey);
        if (StringUtils.hasText(switchStr)) {
            return Boolean.parseBoolean(switchStr);
        } else {
            return matchIfMissing();
        }
    }
}
