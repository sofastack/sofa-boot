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
package com.alipay.sofa.rpc.boot.test;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * @author khotyn
 */
public class ConfigurationMetaTest {
    @Test
    public void testConfigurationMetaFileExists() throws IOException, URISyntaxException {
        Enumeration<URL> springConfigurationMetadataFiles = Thread.currentThread()
            .getContextClassLoader().getResources("META-INF/spring-configuration-metadata.json");
        boolean found = false;
        while (springConfigurationMetadataFiles.hasMoreElements()) {
            URL fileUrl = springConfigurationMetadataFiles.nextElement();
            byte[] contents = Files.readAllBytes(Paths.get(fileUrl.toURI()));
            if (new String(contents).contains(SofaBootRpcProperties.class.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Spring configuration metadata not generated", found);
    }
}