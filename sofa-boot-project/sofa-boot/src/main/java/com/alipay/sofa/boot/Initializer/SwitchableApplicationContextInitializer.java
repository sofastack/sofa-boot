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
import org.springframework.util.StringUtils;

/**
 * @author yuanxuan
 * @version : SwitchableApplicationContextInitializer.java, v 0.1 2023年02月09日 11:57 yuanxuan Exp $
 */
public abstract class SwitchableApplicationContextInitializer
                                                             implements
                                                             ApplicationContextInitializer<ConfigurableApplicationContext> {
    protected static final String CONFIG_KEY_PREFIX = "sofa.boot.initializer.switch";

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
     * @return
     */
    protected abstract String switchKey();

    protected boolean isEnable(ConfigurableApplicationContext applicationContext) {
        String switchStr = applicationContext.getEnvironment().getProperty(switchKey());
        if (StringUtils.hasText(switchStr)) {
            return Boolean.parseBoolean(switchStr);
        } else {
            return true;
        }
    }
}
