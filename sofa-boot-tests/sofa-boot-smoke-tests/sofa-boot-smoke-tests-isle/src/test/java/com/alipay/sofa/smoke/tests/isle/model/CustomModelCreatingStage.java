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
package com.alipay.sofa.smoke.tests.isle.model;

import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huzijie
 * @version CustomModelCreatingStage.java, v 0.1 2023年02月03日 10:21 AM huzijie Exp $
 */
public class CustomModelCreatingStage extends ModelCreatingStage {

    private final List<URL> additionUrls = new ArrayList<>();

    public CustomModelCreatingStage(String... paths) {
        for (String path : paths) {
            URL url = getClass().getClassLoader().getResource(
                path + "/" + DeploymentDescriptorConfiguration.SOFA_MODULE_FILE);
            Assert.notNull(url, "url must not be null: " + path);
            additionUrls.add(url);
        }
    }

    @Override
    public List<URL> getUrls() throws IOException {
        List<URL> urls = super.getUrls();
        urls.addAll(additionUrls);
        return urls;
    }
}
