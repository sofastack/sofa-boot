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

import com.alipay.sofa.boot.util.ModuleUtil;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author huazhongming
 * @since 4.4.0
 */
public class AutoModuleExportApplicationContextInitializer
                                                          implements
                                                          ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String AUTO_MODULE_JDK_ENABLE_KEY = "sofa.boot.auto.module.export.jdk.enable";
    private static final String AUTO_MODULE_ALL_ENABLE_KEY = "sofa.boot.auto.module.export.all.enable";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (isEnable(applicationContext, AUTO_MODULE_ALL_ENABLE_KEY, "false")) {
            ModuleUtil.exportAllModulePackageToAll();
        } else if (isEnable(applicationContext, AUTO_MODULE_JDK_ENABLE_KEY, "true")) {
            ModuleUtil.exportAllJDKModulePackageToAll();
        }
    }

    protected boolean isEnable(ConfigurableApplicationContext applicationContext, String key,
                               String defaultValue) {
        String switchStr = applicationContext.getEnvironment().getProperty(key, defaultValue);
        return Boolean.parseBoolean(switchStr);
    }
}
