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
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.boot.util.StringUtils;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试 Annotation 的 service 没有实现接口，发布失败
 *
 * @author darian1996
 * @since 3.9.1
 */
public class AnnotationSofaServiceTypeCheckFailTest {

    @AfterClass
    public static void clearLogFiles() throws IOException {
        final String logRootPath = StringUtils.hasText(System.getProperty("logging.path")) ? System
            .getProperty("logging.path") : "./logs";
        FileUtils.deleteDirectory(new File(logRootPath));
    }

    @Test
    public void testSofaServiceClassNotAssignableFromInterfaceType() throws IOException {
        String logRootPath = StringUtils.hasText(System.getProperty("logging.path")) ? System
            .getProperty("logging.path") : "./logs";
        File sofaLog = new File(logRootPath + File.separator + "sofa-runtime" + File.separator
                                + "sofa-default.log");
        FileUtils.write(sofaLog, "", System.getProperty("file.encoding"));

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "AnnotationSofaServiceTypeCheckFailTest");
        properties.put("logging.path", logRootPath);
        properties.put("com.alipay.sofa.boot.serviceInterfaceTypeCheck", "true");

        SpringApplication springApplication = new SpringApplication(
            TestSofaServiceNotAssignableFromInterfaceTypeConfiguration.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.setDefaultProperties(properties);

        Assert.assertThrows(BeanCreationException.class, springApplication::run);

        String content = FileUtils.readFileToString(sofaLog, System.getProperty("file.encoding"));
        Assert
            .assertTrue(content
                .contains("SOFA-BOOT-01-00104: Bean "
                          + "[com.alipay.sofa.runtime.test.beans.facade.SampleService] "
                          + "type is [class java.lang.Object] not isAssignableFrom "
                          + "[interface com.alipay.sofa.runtime.test.beans.facade.SampleService] , please check it"));
    }

    @Configuration(proxyBeanMethods = false)
    @Import(RuntimeConfiguration.class)
    @EnableAutoConfiguration
    static class TestSofaServiceNotAssignableFromInterfaceTypeConfiguration {
        @Bean
        @SofaService(interfaceType = SampleService.class)
        public Object object() {
            return new Object();
        }
    }
}