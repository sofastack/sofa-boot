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
package com.alipay.sofa.boot.autoconfigure.detect;

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.AutoConfigurationImportEvent;
import org.springframework.boot.autoconfigure.AutoConfigurationImportListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements for {@link AutoConfigurationImportListener} used to detect configurations defined in legacy model.
 *
 * @author huzijie
 * @version LegacyAutoConfigurationDetectListener.java, v 0.1 2023年04月18日 10:28 AM huzijie Exp $
 */
public class LegacyAutoConfigurationDetectListener implements AutoConfigurationImportListener,
                                                  BeanClassLoaderAware {

    private static final Logger LOGGER = SofaBootLoggerFactory
                                           .getLogger(LegacyAutoConfigurationDetectListener.class);

    private ClassLoader         beanClassLoader;

    @Override
    public void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event) {
        // configurations form *.import file
        Set<String> importConfigurations = new HashSet<>();
        importConfigurations.addAll(event.getCandidateConfigurations());
        importConfigurations.addAll(event.getExclusions());

        // configurations from spring.factories file
        List<String> configurations = new ArrayList<>(
                SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, beanClassLoader));

        // configurations which in  spring.factories but not in *.import will be ignored.
        Set<String> legacyConfigurations = new HashSet<>();
        configurations.forEach(className -> {
            if (!importConfigurations.contains(className)) {
                legacyConfigurations.add(className);
            }
        });

        if (!legacyConfigurations.isEmpty()) {
            LOGGER.warn(builderWarnLog(legacyConfigurations).toString());
        }
    }

    private StringBuilder builderWarnLog(Set<String> legacyConfigurations) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("These configurations defined in spring.factories file will be ignored:");
        stringBuilder.append("\n");
        legacyConfigurations.forEach(legacyConfiguration -> {
            stringBuilder.append("--- ");
            stringBuilder.append(legacyConfiguration);
            stringBuilder.append("\n");
        });
        return stringBuilder;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
}
